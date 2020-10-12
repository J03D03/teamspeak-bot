import com.github.theholywaffle.teamspeak3.TS3Query;

public class ServerSettings {
    TS3Query query;
    String queryName;
    String queryPassword;
    String hostAddress = "127.0.0.1";
    String topicKeyword = "*BOT*";
    int floodRate = 200;
    int queryPort = 10011;      // Default queryPort (TeamSpeak)
    int timeToLive = 20;        // [m]inutes
    int channelAdminId = 5;     // Default channelAdmin ID (TeamSpeak)
    int defaultChannelID = 1;   // With a fresh TeamSpeak installation the 'Default Channel' has the ID=1
    int serverId = 1;           // Depending on how many TeamSpeak instances you run on your server

    public ServerSettings(){
        loadServerSettings();
    }

    private void loadServerSettings()
    {
        // MANDATORY:
        queryName = System.getenv("TSBOT_QUERY_NAME");
        queryPassword = System.getenv("TSBOT_QUERY_PASSWORD");

        if(System.getenv("TSBOT_HOST_ADDRESS") != null) {
            hostAddress = System.getenv("TSBOT_HOST_ADDRESS");
        }
        if(System.getenv("TSBOT_TOPIC_KEYWORD") != null) {
            topicKeyword = System.getenv("TSBOT_TOPIC_KEYWORD");
        }
        if(System.getenv("TSBOT_TIME_TO_LIVE") != null){
            timeToLive = Integer.parseInt(System.getenv("TSBOT_TIME_TO_LIVE"));
        }
        if(System.getenv("TSBOT_FLOOD_RATE") != null){
            floodRate = Integer.parseInt(System.getenv("TSBOT_FLOOD_RATE"));
        }
        if(System.getenv("TSBOT_CHANNEL_ADMIN_ID") != null){
            channelAdminId = Integer.parseInt(System.getenv("TSBOT_CHANNEL_ADMIN_ID"));
        }
        if(System.getenv("TSBOT_DEFAULT_CHANNEL_ID") != null){
            defaultChannelID = Integer.parseInt(System.getenv("TSBOT_DEFAULT_CHANNEL_ID"));
        }
        if(System.getenv("TSBOT_QUERY_PORT") != null){
            queryPort = Integer.parseInt(System.getenv("TSBOT_QUERY_PORT"));
        }
        if(System.getenv("TSBOT_SERVER_ID") != null){
            serverId = Integer.parseInt(System.getenv("TSBOT_SERVER_ID"));
        }
    }
}
