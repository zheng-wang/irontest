package io.irontest.core.teststep;

public class JMSClearQueueResponse extends APIResponse {
    private int clearedMessagesCount;

    public int getClearedMessagesCount() {
        return clearedMessagesCount;
    }

    public void setClearedMessagesCount(int clearedMessagesCount) {
        this.clearedMessagesCount = clearedMessagesCount;
    }
}
