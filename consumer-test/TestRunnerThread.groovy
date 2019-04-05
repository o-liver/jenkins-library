class TestRunnerThread extends Thread {

    def testCase

    public TestRunnerThread(testCase) {
        this.testCase = testCase
    }

    public void run() {
        println "This is your string: ${testCase}"
        println "Job done"
    }
}
