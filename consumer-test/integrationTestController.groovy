import groovy.io.FileType
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON

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
def COMMIT_HASH_FOR_STATUS_NOTIFICATIONS = System.getenv('TRAVIS_PULL_REQUEST_SHA') ?: System.getenv('TRAVIS_COMMIT')

println "commit sha: ${COMMIT_HASH_FOR_STATUS_NOTIFICATIONS}"

notifyGithub("pending", "Integration tests in progress.", COMMIT_HASH_FOR_STATUS_NOTIFICATIONS)

def workspacesRootDir = new File('workspaces')
deleteDirIfExists(workspacesRootDir)
def testCases = listYamlInDirRecursive('testCases')
testCases.each { file ->
    def testCaseMatches = (file.toString() =~ /^[\w\-]+\\/([\w\-]+)\\/([\w\-]+)\..*\u0024/)
    area = testCaseMatches[0][1]
    testCase = testCaseMatches[0][2]
    def testCaseRootDir = new File("${workspacesRootDir}/${area}/${testCase}")
    deleteDirIfExists(testCaseRootDir)
    testCaseRootDir.mkdirs()
}


def notifyGithub(state, description, hash) {
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

static def deleteDirIfExists(File dirname) {
    if (dirname.exists()) {
        dirname.deleteDir()
    }
}

static def listYamlInDirRecursive(String dirname) {
    def dir = new File(dirname)
    def yamlFiles = []
    dir.eachFileRecurse(FileType.FILES) { file ->
        if (file.getName().endsWith('.yml'))
            yamlFiles << file
    }
    return yamlFiles
}
