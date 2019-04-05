import ITUtils

class TestRunnerThread extends Thread {

    def testCase
    def area
    def workspaces

    public TestRunnerThread(workspaces, area, testCase) {
        this.area = area
        this.testCase = testCase
        this.workspaces = workspaces
    }

    public void run() {
        def testCaseRootDir = "${workspaces}/${area}/${testCase}"
        ITUtils.newEmptyDir(testCaseRootDir)
        println "[INFO] Test case '${testCase}' in area '${area}' launched."
        println "[INFO] Waiting for test case '${testCase}' in area '${area}'."
        println "[INFO] Test case '${testCase}' in area '${area}' finished."
    }
}
