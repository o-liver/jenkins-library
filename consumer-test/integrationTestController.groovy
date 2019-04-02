@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC

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

def notifyGithub(state, description, hash) {
    println "[INFO] Notifying about state '${state}' for commit '${hash}'."

    def http = new HTTPBuilder("https://api.github" +
        ".com/repos/o-liver/jenkins-library/statuses/${hash}")
    http.auth.basic System.getenv('INTEGRATION_TEST_VOTING_USER'), System.getenv('INTEGRATION_TEST_VOTING_TOKEN')

    def postBody = [
        state      : state,
        target_url : System.getenv('TRAVIS_BUILD_WEB_URL'),
        description: description,
        context    : "integration-tests"
    ]

    http.post(path: '/', body: postBody, requestContentType: URLENC) { resp ->

        println "POST Success: ${resp.statusLine}"
        assert resp.statusLine.statusCode == 201
    }
}