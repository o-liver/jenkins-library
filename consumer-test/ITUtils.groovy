import groovy.io.FileType

class ITUtils {

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

    static def executeShell(area, command) {
        def stdOut = new StringBuilder(), stdErr = new StringBuilder()
        def process = command.execute()
        process.waitForProcessOutput(stdOut, stdErr)
        int exitCode = process.exitValue()
        if (exitCode>0) {
            println "Trouble in area '${area}': Shell exited with code ${exitCode}."
            println "Shell command was: '${command}'"
            println "Console output: ${stdOut}"
            println "Console error: '${stdErr}'"
            System.exit(exitCode)
        }
        return stdOut.toString().trim()
    }
}
