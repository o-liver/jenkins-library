import ITUtils

class TestRunnerThread extends Thread {

    static def workspacesRootDir
    static def libraryVersionUnderTest
    static def repositoryUnderTest
    def area
    def testCase
    def testCaseRootDir
    def testCaseWorkspace

    public TestRunnerThread(testCaseFilePath) {
        // Regex pattern expects a folder structure such as '/rootDir/areaDir/testCase.extension'
        def testCaseMatches = (testCaseFilePath.toString() =~ /^[\w\-]+\\/([\w\-]+)\\/([\w\-]+)\..*\u0024/)
        this.area = testCaseMatches[0][1]
        this.testCase = testCaseMatches[0][2]
        this.testCaseRootDir = "${workspacesRootDir}/${area}/${testCase}"
        this.testCaseWorkspace = "${testCaseRootDir}/workspace"

        ITUtils.newEmptyDir(this.testCaseRootDir)
    }

    public void run() {
        println "[INFO] Test case '${testCase}' in area '${area}' launched."
        ITUtils.executeShell("git clone -b ${testCase} https://github.com/sap/cloud-s4-sdk-book ${testCaseWorkspace}")
        println "[INFO] Waiting for test case '${testCase}' in area '${area}'."
        println "[INFO] Test case '${testCase}' in area '${area}' finished."
    }
}
