package io.irontest.core.teststep;

import io.irontest.models.teststep.MQRFH2Header;

public class MQDequeueResponse extends APIResponse {
    private String bodyAsText;
    private MQRFH2Header mqrfh2Header;    //  null means no RFH2 header

    public String getBodyAsText() {
        return bodyAsText;
    }

    public void setBodyAsText(String bodyAsText) {
        this.bodyAsText = bodyAsText;
    }

    public MQRFH2Header getMqrfh2Header() {
        return mqrfh2Header;
    }

    public void setMqrfh2Header(MQRFH2Header mqrfh2Header) {
        this.mqrfh2Header = mqrfh2Header;
    }
}
