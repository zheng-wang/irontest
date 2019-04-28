package io.irontest.core.runner;

import com.ibm.mqlight.api.CompletionListener;
import com.ibm.mqlight.api.NonBlockingClient;
import com.ibm.mqlight.api.NonBlockingClientAdapter;
import io.irontest.models.teststep.AMQPTeststepProperties;
import io.irontest.models.teststep.Teststep;

public class AMQPTeststepRunner extends TeststepRunner {
    protected BasicTeststepRun run(Teststep teststep) {
        AMQPTeststepProperties otherProperties = (AMQPTeststepProperties) teststep.getOtherProperties();

        NonBlockingClient.create(teststep.getEndpoint().getUrl(), new NonBlockingClientAdapter<Void>() {
            public void onStarted(NonBlockingClient client, Void context) {
                client.send(otherProperties.getNodeAddress(), (String) teststep.getRequest(), null, new CompletionListener() {
                    public void onSuccess(NonBlockingClient client, Object context) {
                        client.stop(null, null);
                    }
                    public void onError(NonBlockingClient client, Object context, Exception exception) {
                        client.stop(null, null);
                        throw new RuntimeException("Failed to send message to AMQP endpoint.", exception);
                    }
                }, null);
            }
        }, null);

        return new BasicTeststepRun();
    }
}
