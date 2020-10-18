import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class LegacyBot extends Bot {
    private static final int TIMEOUT = 60; //[s]
    private static final int MAX_SEM_AVAILABLE = 1;
    private final Semaphore sem = new Semaphore(MAX_SEM_AVAILABLE);

    public LegacyBot() {
        botName = "[LEGACY]bot";
    }

    public void run() {
        try {
            final TS3Api api = serverGateway.api;
            api.setNickname(botName + getFourRndDigits());
            api.whoAmI().getId();
            api.registerEvent(TS3EventType.SERVER, 0);
            api.registerEvent(TS3EventType.CHANNEL, 0);
            api.addTS3Listeners(new TS3EventAdapter() {
                @Override
                public void onClientLeave(ClientLeaveEvent e) {
                    if (sem.tryAcquire()) {
                        runCheckRoutine(api);
                    }
                }
                @Override
                public void onClientMoved(ClientMovedEvent e) {
                    if (sem.tryAcquire()) {
                        runCheckRoutine(api);
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    void runCheckRoutine(TS3Api api) {
        try {
            Date today = new Date();
            System.out.println(today.toString() + " Checking..");
            api.unregisterAllEvents();
            checkChannelCommander(api);
            TimeUnit.SECONDS.sleep(TIMEOUT);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            System.gc();
            api.registerEvent(TS3EventType.SERVER, 0);
            api.registerEvent(TS3EventType.CHANNEL, 0);
            sem.release();
        }
    }

    void checkChannelCommander(TS3Api api) {
        List<Channel> channels = api.getChannels();
        List<Client> clients = api.getClients();
        for (Channel channel : channels) {
            if (channel.getTotalClients() > 0 && !channel.isPermanent()) {
                int channelId = channel.getId();
                List<Client> clientsInChannel = new ArrayList();
                for (Client client : clients) {
                    if (client.getChannelId() == channelId) {
                        if (client.getChannelGroupId() == serverGateway.settings.channelAdminId) {
                            clientsInChannel = null;
                            break;
                        } else {
                            if (!client.isServerQueryClient())
                                clientsInChannel.add(client);
                        }
                    }
                }
                if (clientsInChannel != null) {
                    try {
                        int rndNr = (int) (Math.random() * clientsInChannel.size());
                        api.setClientChannelGroup(serverGateway.settings.channelAdminId, channelId, clientsInChannel.get(rndNr).getDatabaseId());
                        int parentChannelId = api.getChannelInfo(channelId).getParentChannelId();
                        String topic = api.getChannelInfo(parentChannelId).getTopic();
                        topic = topic.replace(serverGateway.settings.topicKeyword, "").split(";")[0];
                        String newChannelName = topic + " " + clientsInChannel.get(rndNr).getNickname();
                        Map<ChannelProperty, String> properties = new HashMap<>();
                        properties.put(ChannelProperty.CHANNEL_NAME, newChannelName);
                        api.editChannel(channel.getId(), properties);
                    } catch (Exception ex) {
                        System.out.println("\t  " + ex.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        LegacyBot bot = new LegacyBot();
        bot.serverGateway = new ServerGateway();
        bot.run();
    }
}

