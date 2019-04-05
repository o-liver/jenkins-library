class TestRunnerThread extends Thread {

    def testCase
    def area

    public TestRunnerThread(area, testCase) {
        this.area = area
        this.testCase = testCase
    }

    public void run() {
        println "[INFO] Test case '${testCase}' in area '${area}' launched."
        println "[INFO] Waiting for test case '${testCase}' in area '${area}'."
        println "[INFO] Test case '${testCase}' in area '${area}' finished."
    }
}
