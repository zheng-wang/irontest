package io.irontest.models.testrun;

import io.irontest.models.TestResult;

import java.util.Date;

/**
 * Created by Zheng on 9/03/2018.
 */
public class TestRun {
    private long id;        //  id of corresponding database record
    private Date startTime;
    private long duration;              //  in milliseconds
    private TestResult result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }
}
