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

}
