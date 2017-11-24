import com.sap.piper.Utils

/**
 * neoDeployment
 * Deploys an Application to SAP Cloud Platform (SAP CP) using the SAP Cloud Platform Console Client (Neo Java Web SDK).
 *
 * @param archivePath
 *           Path of the archive to be deployed.
 * @param script
 *           The common script environment of the Jenkinsfile run. Typically `this` is passed to this parameter. This allows the function to access the [`commonPipelineEnvironment`](commonPipelineEnvironment.md) for retrieving e.g. configuration parameters.
 * @param archivePath
 *           The path to the archive for deployment to SAP CP.
 * @param deployHost
 *           The SAP Cloud Platform host to deploy to.
 * @param deployAccount
 *           The SAP Cloud Platform account to deploy to.
 * @param credentialsId
 *           The Jenkins credentials containing user and password used for SAP CP deployment.
 * @param neoHome
 *           The path to the `neo-java-web-sdk` tool used for SAP CP deployment. If no parameter is provided, the path is retrieved from the Jenkins environment variables using `env.NEO_HOME`. If this Jenkins environment variable is not set it is assumed that the tool is available in the `PATH`.
 *
 * @return none
 */

def call(parameters = [:]) {

    handlePipelineStepErrors (stepName: 'neoDeploy', stepParameters: parameters) {

        def utils = new Utils()
        def script = parameters.script
        if (script == null){
            script = [commonPipelineEnvironment: commonPipelineEnvironment]
        }

        def archivePath = new File(utils.getMandatoryParameter(parameters, 'archivePath', null))
        if (!archivePath.isAbsolute()) {
            archivePath = new File(pwd(), archivePath.getPath())
        }
        if (!archivePath.exists()){
            error "Archive cannot be found with parameter archivePath: '${archivePath}'."
        }

        def defaultDeployHost = script.commonPipelineEnvironment.getConfigProperty('DEPLOY_HOST')
        def defaultDeployAccount = script.commonPipelineEnvironment.getConfigProperty('CI_DEPLOY_ACCOUNT')
        def defaultCredentialsId = script.commonPipelineEnvironment.getConfigProperty('neoCredentialsId')
        if (defaultCredentialsId == null) {
            defaultCredentialsId = 'CI_CREDENTIALS_ID'
        }

        def deployHost = utils.getMandatoryParameter(parameters, 'deployHost', defaultDeployHost)
        def deployAccount = utils.getMandatoryParameter(parameters, 'deployAccount', defaultDeployAccount)
        def credentialsId = parameters.get('neoCredentialsId', defaultCredentialsId)

        def neoExecutable = getNeoExecutable(parameters)

        withCredentials([usernamePassword(
                credentialsId: credentialsId,
                passwordVariable: 'password',
                usernameVariable: 'username'
        )]) {
            sh """#!/bin/bash
                    ${neoExecutable} deploy-mta \
                      --user ${username} \
                      --host ${deployHost} \
                      --source "${archivePath.getAbsolutePath()}" \
                      --account ${deployAccount} \
                      --password ${password} \
                      --synchronous
               """
        }
    }
}

private getNeoExecutable(parameters) {

    def neoExecutable = 'neo' // default, if nothing below applies maybe it is the path.

    if (parameters?.neoHome) {
        neoExecutable = "${parameters.neoHome}/tools/neo.sh"
        echo "[neoDeploy] Neo executable \"${neoExecutable}\" retrieved from parameters."
        return neoExecutable
    }

    if (env?.NEO_HOME) {
        neoExecutable = "${env.NEO_HOME}/tools/neo.sh"
        echo "[neoDeploy] Neo executable \"${neoExecutable}\" retrieved from environment."
        return neoExecutable
    }

    echo "Using Neo executable from PATH."
    return neoExecutable
}
