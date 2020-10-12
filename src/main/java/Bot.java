import com.github.theholywaffle.teamspeak3.TS3Api;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Bot implements Runnable {
    String botName = "[DEFAULT]bot";
    ServerGateway serverGateway;
    Logger botlog = new Logger("bot-log");

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

    public void start() {
        Thread thread;
        try {
            botlog.log("" + this.botName + " <RUNNING> " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
            System.out.println("" + this.botName + " <RUNNING> " + LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute());
            this.serverGateway = new ServerGateway();
            thread = new Thread(this);
            thread.start();
            thread.join();
            Thread.sleep(1000 * 60 * this.serverGateway.settings.timeToLive);
            this.serverGateway.api.logout();
            this.serverGateway.settings.query.exit();
            System.out.println("" + this.botName + " <DEAD> " + this.serverGateway.settings.timeToLive);
            botlog.log("" + this.botName + " <DEAD> " + this.serverGateway.settings.timeToLive);
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + e.getMessage());
        } finally {
            try {
                serverGateway.api.logout();
                serverGateway.settings.query.exit();
            } catch (Exception e) {
                System.out.println(LocalDateTime.now().getHour() + ":" + LocalDateTime.now().getMinute() + " " + e.getMessage());
            }
            System.gc();
        }
    }
}
