import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ChannelBot extends Bot {
    private static final int UNLIMITED = -1;
    private static final int PWPROTECTED = 0;
    private final HashMap<Integer, DynamicChannel> dynamicChannelMap;

    public ChannelBot() {
        dynamicChannelMap = new HashMap();
        botName = "[CHANNEL]bot";
    }

    void addDynamicChannel(int channelID, int maxUser, String channelName) {
        dynamicChannelMap.put(channelID, new DynamicChannel(channelName, maxUser));
    }

    void setChannelProperties(Map<ChannelProperty, String> properties, TS3Api api, int clientID, int targetChannelID, DynamicChannel channel) {
        if (channel.getMaxUser() == PWPROTECTED) {
            int randomNumber = ThreadLocalRandom.current().nextInt(1000, 9999);
            properties.put(ChannelProperty.CHANNEL_PASSWORD, "" + randomNumber);
            api.pokeClient(clientID, randomNumber + "");
        }
        if (channel.getMaxUser() > 0) {
            properties.put(ChannelProperty.CHANNEL_MAXCLIENTS, Integer.toString(channel.getMaxUser()));
            properties.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
        }
        properties.put(ChannelProperty.CHANNEL_CODEC_QUALITY, "10");
        properties.put(ChannelProperty.CHANNEL_DELETE_DELAY, "0");
        properties.put(ChannelProperty.CHANNEL_FLAG_PERMANENT, "0");
        properties.put(ChannelProperty.CPID, String.valueOf(targetChannelID));
    }

    public void run() {
        try {
            final TS3Api api = serverGateway.api;
            api.setNickname(botName + getFourRndDigits());
            final int botID = api.whoAmI().getId();
            fetchDynamicChannel(api);
            api.unregisterAllEvents();
            api.registerEvent(TS3EventType.CHANNEL, 0);
            api.addTS3Listeners(new TS3EventAdapter() {
                @Override
                public void onClientMoved(ClientMovedEvent client) {
                    int clientID = client.getClientId();
                    ClientInfo clientInfo = api.getClientInfo(clientID);
                    if (!clientInfo.isServerQueryClient() && clientInfo.isRegularClient()) {
                        int targetChannelID = client.getTargetChannelId();
                        if (dynamicChannelMap.containsKey(targetChannelID)) {
                            int tempChannelID = createChannel(api, targetChannelID, clientID);
                            if (tempChannelID != -1) {
                                api.moveClient(clientID, tempChannelID);
                                api.setClientChannelGroup(serverGateway.settings.channelAdminId, tempChannelID, clientInfo.getDatabaseId());
                                api.moveClient(botID, serverGateway.settings.defaultChannelID); //move ts3bot to lobby otherwise the temporary channel will not be destroyed after the client left.
                            } else {
                                api.moveClient(clientID, serverGateway.settings.defaultChannelID);
                                api.pokeClient(clientID, "Please check your (TextMessage)");
                                api.sendPrivateMessage(clientID, "There are two known issues why your channel could not be created. Your name might be too long or the channel already exists.");
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            botlog.log(ex.getStackTrace().toString());
            System.out.println(ex.getMessage());
        }
    }

    int createChannel(TS3Api api, int targetID, int clientID) {
        int tempChannelID = -1;
        try {
            DynamicChannel channel = dynamicChannelMap.get(targetID);
            String nickname = getClientNickname(api, clientID);
            Map<ChannelProperty, String> properties = new HashMap<>();
            setChannelProperties(properties, api, clientID, targetID, channel);
            tempChannelID = api.createChannel(channel.getChannelName() + " " + nickname, properties);
        } catch (Exception ex) {
            botlog.log("Could not create the channel.");
        }
        return tempChannelID;

    }

    private void fetchDynamicChannel(TS3Api api) {
        List<Channel> list = api.getChannels();
        for (Channel channel : list) {
            if (channel.isPermanent()) {
                String topic = channel.getTopic();
                if (topic.contains(serverGateway.settings.topicKeyword)) {
                    topic = topic.replace(serverGateway.settings.topicKeyword, "");
                    String[] topicParts = topic.split(";");
                    String subChannelName = topicParts[0];
                    if (topicParts.length > 1) {
                        int maxUser = PWPROTECTED;
                        if (!topicParts[1].contains("pw")) {
                            maxUser = Integer.parseInt(topicParts[1]);
                        }
                        addDynamicChannel(channel.getId(), maxUser, subChannelName);
                    } else {
                        addDynamicChannel(channel.getId(), UNLIMITED, subChannelName);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        ChannelBot bot = new ChannelBot();
        bot.start();
    }
}
