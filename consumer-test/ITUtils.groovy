import groovy.io.FileType
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7')
import groovyx.net.http.RESTClient

import static groovyx.net.http.ContentType.JSON

class ITUtils {

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

    static def newEmptyDir(String dirName) {
        def dir = new File(dirName)
        if (dir.exists()) {
            dir.deleteDir()
        }
        dir.mkdirs()
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

    static executeShell(String command) {
        def stdOut = new StringBuilder()
        def process = command.execute()
        process.consumeProcessOutputStream(stdOut)
        process.waitForOrKill(10000) //Allow process to run for max 10 seconds
        def exitCode = process.exitValue()
        if (exitCode>0) {
            throw new RuntimeException(
                "Shell command '${command}' exited with exit code ${exitCode}")
        }
        return stdOut.toString().trim()
    }
}
