/**
 * Initializes the commonPipelineEnvironment, which is used throughout the pipeline.
 *
 * @param script
 *           The reference to the pipeline script (Jenkinsfile). Normally `this` needs to be provided.
 * @param configFile
 *           Property file defining project specific settings.
 */

def call(Map parameters = [:]) {

    handlePipelineStepErrors (stepName: 'setupCommonPipelineEnvironment', stepParameters: parameters) {

        def configFile = parameters.get('configFile', '.pipeline/config.properties')
        def script = parameters.script

        Map configMap = [:]
        if (configFile.length() > 0)
            configMap = readProperties (file: configFile)
        script.commonPipelineEnvironment.setConfigProperties(configMap)

    }
}
