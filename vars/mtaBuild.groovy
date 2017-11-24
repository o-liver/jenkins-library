import com.sap.piper.Utils

/**
 * mtaBuild
 * Executes the SAP MTA Archive Builder to create an mtar archive of the application.
 *
 * @param script
 *           The common script environment of the Jenkinsfile running. Typically the reference to the script calling the pipeline step is provided with the `this` parameter, as in `script: this`. This allows the function to access the [`commonPipelineEnvironment`](commonPipelineEnvironment.md) for retrieving, for example, configuration parameters.
 * @param buildTarget
 *           The target platform to which the mtar can be deployed.
 * @param mtaJarLocation
 *           The path of the `mta.jar` file. If no parameter is provided, the path is retrieved from the Jenkins environment variables using `env.MTA_JAR_LOCATION`. If the Jenkins environment variable is not set it is assumed that `mta.jar` is located in the current working directory.
 * @return The file name of the resulting archive is returned with this step. The file name is extracted from the key `ID` defined in `mta.yaml`.
 */

def call(Map parameters = [:]) {

    handlePipelineStepErrors (stepName: 'mtaBuild', stepParameters: parameters) {

        def utils = new Utils()
        def buildTarget = utils.getMandatoryParameter(parameters, 'buildTarget', null)
        def script = parameters.script
        if (script == null){
            script = [commonPipelineEnvironment: commonPipelineEnvironment]
        }

        def mtaYaml = readYaml file: "${pwd()}/mta.yaml"

        //[Q]: Why not yaml.dump()? [A]: This reformats the whole file.
        sh "sed -ie \"s/\\\${timestamp}/`date +%Y%m%d%H%M%S`/g\" \"${pwd()}/mta.yaml\""

        def id = mtaYaml.ID
        if (!id) {
            error "Property 'ID' not found in mta.yaml file at: '${pwd()}'"
        }

        def mtarFileName = "${id}.mtar"

        def mtaJar = getMtaJar(parameters)

        sh  """#!/bin/bash
            export PATH=./node_modules/.bin:${PATH}
            java -jar ${mtaJar} --mtar ${mtarFileName} --build-target=${buildTarget} build
            """

        def mtarFilePath = "${pwd()}/${mtarFileName}"
        script.commonPipelineEnvironment.setMtarFilePath(mtarFilePath)

        return mtarFilePath
    }
}

private getMtaJar(parameters) {
    def mtaJarLocation = 'mta.jar' //default, maybe it is in current working directory

    if(parameters?.mtaJarLocation){
        mtaJarLocation = "${parameters.mtaJarLocation}/mta.jar"
        echo "[mtaBuild] MTA JAR \"${mtaJarLocation}\" retrieved from parameters."
        return mtaJarLocation
    }

    if(env?.MTA_JAR_LOCATION){
        mtaJarLocation = "${env.MTA_JAR_LOCATION}/mta.jar"
        echo "[mtaBuild] MTA JAR \"${mtaJarLocation}\" retrieved from environment."
        return mtaJarLocation
    }

    echo "[mtaBuild] Using MTA JAR from current working directory."
    return mtaJarLocation
}
