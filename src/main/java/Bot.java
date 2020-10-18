import com.github.theholywaffle.teamspeak3.TS3Api;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Bot implements Runnable {
    String botName = "[DEFAULT]bot";
    ServerGateway serverGateway;

    int getFourRndDigits() {
        return ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    String getClientNickname(TS3Api api, int clientID) {
        String nickname;
        nickname = api.getClientInfo(clientID).getNickname();
        if (nickname.equals("")) {
            int randomNumber = 1000 + (int) (Math.random() * 9999);
            nickname = "anonymous" + randomNumber;
        }
        return nickname;
    }
}
