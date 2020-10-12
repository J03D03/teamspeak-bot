import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;

public class ServerGateway {
    ServerSettings settings;
    TS3Api api;

    public ServerGateway() {
        this.settings = new ServerSettings();
        establishConnection();
        getApi();
    }

    public void establishConnection() {
        final TS3Config config = new TS3Config();
        config.setHost(settings.hostAddress);
        config.setQueryPort(settings.queryPort);
        config.setFloodRate(TS3Query.FloodRate.custom(settings.floodRate));
        config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        settings.query = new TS3Query(config);
        settings.query.connect();
    }

    public void getApi() {
        api = settings.query.getApi();
        api.login(settings.queryName, settings.queryPassword);
        api.selectVirtualServerById(settings.serverId);
    }
}

