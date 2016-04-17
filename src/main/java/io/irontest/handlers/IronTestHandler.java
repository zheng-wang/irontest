package io.irontest.handlers;

import io.irontest.models.Endpoint;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface IronTestHandler {
    Object invoke(String request, Endpoint endpoint) throws Exception;
}
