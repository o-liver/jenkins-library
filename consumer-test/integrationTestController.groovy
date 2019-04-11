import ITUtils
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON

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

notifyGithub("pending", "Integration tests in progress.", commitHash)

ITUtils.newEmptyDir(WORKSPACES_ROOT)
TestRunnerThread.workspacesRootDir = WORKSPACES_ROOT
TestRunnerThread.libraryVersionUnderTest = ITUtils.executeShell("git log --format=\"%H\" -n 1")
TestRunnerThread.repositoryUnderTest = System.getenv('TRAVIS_REPO_SLUG') ?: 'SAP/jenkins-library'

//This auxiliary thread is needed in order to produce some output while the
//test are running. Otherwise the job will be canceled after 10 minutes without output.
def auxiliaryThread = Thread.start {
    sleep(10000)
    println "[INFO] Integration tests still running."
}

def testCaseThreads = listTestCaseThreads()
testCaseThreads.each { it ->
    it.start()
    it.join()
}

auxiliaryThread.join()


static def notifyGithub(state, description, hash) {
    println "[INFO] Notifying about state '${state}' for commit '${hash}'."

    def http = new RESTClient("https://api.github" +
        ".com/repos/o-liver/jenkins-library/statuses/${hash}")
    http.headers['User-Agent'] = 'groovy-script'
    http.headers['Authorization'] = "token ${System.getenv('INTEGRATION_TEST_VOTING_TOKEN')}"

    def postBody = [
        state      : state,
        target_url : System.getenv('TRAVIS_BUILD_WEB_URL'),
        description: description,
        context    : "integration-tests"
    ]

    http.post(body: postBody, requestContentType: JSON) { response ->
        assert response.statusLine.statusCode == 201
    }

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
