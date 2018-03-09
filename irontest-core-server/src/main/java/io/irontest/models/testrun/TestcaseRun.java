package io.irontest.models.testrun;

/**
 * Used for collecting data when running one test case.
 * Created by Trevor Li on 7/24/15.
 */
public class TestcaseRun extends TestRun {
    private Long id;
    private long testcaseId;
    private String testcaseName;
    private String testcaseFolderPath;

    public TestcaseRun() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
