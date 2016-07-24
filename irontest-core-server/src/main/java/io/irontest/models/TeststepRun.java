package io.irontest.models;

import java.util.Date;

/**
 * Created by Zheng on 24/07/2016.
 */
public class TeststepRun {
    private String teststepName;
    private String teststepType;
    private String teststepAction;
    private Date startTime;
    private long duration;              //  number of milliseconds
    private TestResult result;
    private String endpointURL;
    private String endpointUsername;
    private long endpointResponseTime;  //  number of milliseconds
    private Object request;
    private String response;
    private String errorMessage;        //  exception stack trace or custom error message
}
