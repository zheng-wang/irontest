package io.irontest.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Zheng on 24/07/2016.
 */
public class TeststepRun {
    private Teststep teststep;

    private Date startTime;
    private long duration;              //  number of milliseconds
    private String response;
    private long responseTime;          //  endpoint response time (number of milliseconds)
    private String errorMessage;        //  exception stack trace or custom error message

    private List<AssertionVerification> assertionVerifications = new ArrayList<AssertionVerification>();

    private TestResult result;
}
