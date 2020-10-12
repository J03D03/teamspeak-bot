public class DynamicChannel {
    private final int maxUser;
    private final String channelName;

    /**
     * DynamicChannel
     *
     * @param channelName name of channel
     * @param maxUser     maximal amount of users in dynamic channel
     */
    public DynamicChannel(String channelName, int maxUser) {
        this.channelName = channelName;
        this.maxUser = maxUser;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public int getMaxUser() {
        return this.maxUser;
    }
}


