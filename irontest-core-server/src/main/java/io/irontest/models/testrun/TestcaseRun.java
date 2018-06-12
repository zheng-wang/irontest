package io.irontest.models.testrun;

/**
 * Used for collecting data when running one test case.
 */
public class TestcaseRun extends TestRun {
    private long testcaseId;
    private String testcaseName;
    private String testcaseFolderPath;

    public TestcaseRun() {}

    public TestcaseRun(TestcaseRun testcaseRun) {
        super.setId(testcaseRun.getId());
        super.setResult(testcaseRun.getResult());
        super.setStartTime(testcaseRun.getStartTime());
        super.setDuration(testcaseRun.getDuration());
        this.testcaseId = testcaseRun.getTestcaseId();
        this.testcaseName = testcaseRun.getTestcaseName();
        this.testcaseFolderPath = testcaseRun.getTestcaseFolderPath();
    }

    public long getTestcaseId() {
        return testcaseId;
    }

    public void setTestcaseId(long testcaseId) {
        this.testcaseId = testcaseId;
    }

    public String getTestcaseName() {
        return testcaseName;
    }

    public void setTestcaseName(String testcaseName) {
        this.testcaseName = testcaseName;
    }

    public String getTestcaseFolderPath() {
        return testcaseFolderPath;
    }

    public void setTestcaseFolderPath(String testcaseFolderPath) {
        this.testcaseFolderPath = testcaseFolderPath;
    }
}
