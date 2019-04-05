import TestRunnerThread
import ITUtils

def WORKSPACES_ROOT = 'workspaces'
def TEST_CASES_DIR = 'testCases'

/*
In case the build is performed for a pull request TRAVIS_COMMIT is a merge
commit between the base branch and the PR branch HEAD. That commit is actually built.
But for notifying about a build status we need the commit which is currently
the HEAD of the PR branch.

In case the build is performed for a simple branch (not associated with a PR)
In this case there is no merge commit between any base branch and HEAD of a PR branch.
The commit which we need for notifying about a build status is in this case simply
TRAVIS_COMMIT itself.
*/
def commitHash = System.getenv('TRAVIS_PULL_REQUEST_SHA') ?: System.getenv('TRAVIS_COMMIT')

ITUtils.notifyGithub("pending", "Integration tests in progress.", commitHash)

def testCaseThreads = listThreadsOfTestCases(WORKSPACES_ROOT, TEST_CASES_DIR)
testCaseThreads.each { it ->
    it.start()
    it.join()
}



def listThreadsOfTestCases(String workspacesRootDir, String testCasesDirName) {
    ITUtils.newEmptyDir(workspacesRootDir)

    //Each dir that includes a yml file is a test case
    def testCases = ITUtils.listYamlInDirRecursive(testCasesDirName)
    def threads = []
    testCases.each { file ->
        // Regex pattern expects a folder structure such as '/rootDir/areaDir/testCase.extension'
        def testCaseMatches = (file.toString() =~ /^[\w\-]+\\/([\w\-]+)\\/([\w\-]+)\..*\u0024/)
        area = testCaseMatches[0][1]
        testCase = testCaseMatches[0][2]
        threads << new TestRunnerThread(workspacesRootDir, area, testCase)
    }
    return threads
}
