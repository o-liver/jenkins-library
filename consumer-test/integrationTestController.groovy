import ITUtils

WORKSPACES_ROOT = 'workspaces'
TEST_CASES_DIR = 'testCases'

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

ITUtils.newEmptyDir(WORKSPACES_ROOT)
TestRunnerThread.workspacesRootDir = WORKSPACES_ROOT
TestRunnerThread.libraryVersionUnderTest = ITUtils.executeShell("git log --format=\"%H\" -n 1")
TestRunnerThread.repositoryUnderTest = System.getenv('TRAVIS_REPO_SLUG:-o-liver/jenkins-library')

def testCaseThreads = listTestCaseThreads()
testCaseThreads.each { it ->
    it.start()
    it.join()
}



def listTestCaseThreads() {

    //Each dir that includes a yml file is a test case
    def testCases = ITUtils.listYamlInDirRecursive(TEST_CASES_DIR)
    def threads = []
    testCases.each { file ->
        threads << new TestRunnerThread(file.toString())
    }
    return threads
}
