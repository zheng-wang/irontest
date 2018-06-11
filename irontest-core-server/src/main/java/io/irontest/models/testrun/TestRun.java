package io.irontest.models.testrun;

import com.fasterxml.jackson.annotation.JsonView;
import io.irontest.models.TestResult;
import io.irontest.resources.ResourceJsonViews;

import java.util.Date;

public class TestRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private long id;        //  id of corresponding database record
    private Date startTime;
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private long duration;              //  in milliseconds
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
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
