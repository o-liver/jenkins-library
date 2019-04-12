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

    static def executeShell(command) {
        def stdOut = new StringBuilder(), stdErr = new StringBuilder()
        def process = command.execute()
        process.consumeProcessOutput(stdOut, stdErr)
        process.waitForOrKill(30000) //Allow process to run for max 30 seconds
        def exitCode = process.exitValue()
        if (exitCode>0) {
            throw new RuntimeException(
                "Shell command '${command}' exited with exit code ${exitCode}. Error: '${stdErr}'")
        }
        return stdOut.toString().trim()
    }
}
