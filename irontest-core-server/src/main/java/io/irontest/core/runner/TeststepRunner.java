package io.irontest.core.runner;

import io.irontest.models.Teststep;

/**
 * Created by Trevor Li on 7/14/15.
 */
public interface TeststepRunner {
    Object run(Teststep teststep) throws Exception;
}
