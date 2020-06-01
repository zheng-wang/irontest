package io.irontest.core.teststep;

public class MQCheckQueueDepthResponse extends APIResponse {
    private int queueDepth;

    public int getQueueDepth() {
        return queueDepth;
    }

    public void setQueueDepth(int queueDepth) {
        this.queueDepth = queueDepth;
    }

    public String toString() {
        return "Queue Depth: " + queueDepth;
    }
}
