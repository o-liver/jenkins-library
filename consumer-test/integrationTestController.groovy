import TestRunnerThread

evaluate(new File("./ITUtils.groovy"))
utils = new ITUtils()
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

utils.notifyGithub("pending", "Integration tests in progress.", commitHash)

def testCaseThreads = listThreadsOfTestCases('workspaces', 'testCases')
testCaseThreads.each { it ->
    it.start()
    it.join()
}



def listThreadsOfTestCases(String rootDirName, String testCasesDirName) {
    utils.newEmptyDir(rootDirName)

    def testCases = utils.listYamlInDirRecursive(testCasesDirName)
    def threads = []
    testCases.each { file ->
        // Regex pattern expects a folder structure such as '/rootDir/areaDir/testCase.extension'
        def testCaseMatches = (file.toString() =~ /^[\w\-]+\\/([\w\-]+)\\/([\w\-]+)\..*\u0024/)
        area = testCaseMatches[0][1]
        testCase = testCaseMatches[0][2]
        def testCaseRootDir = "${rootDirName}/${area}/${testCase}"
        utils.newEmptyDir(testCaseRootDir)
        threads << new TestRunnerThread(area, testCase)
    }
    return threads
}
