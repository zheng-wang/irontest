package io.irontest.core.teststep;

public class JMSCheckQueueDepthResponse extends APIResponse {
    private int queueDepth;

    public int getQueueDepth() {
        return queueDepth;
    }

    public void setQueueDepth(int queueDepth) {
        this.queueDepth = queueDepth;
    }
}
