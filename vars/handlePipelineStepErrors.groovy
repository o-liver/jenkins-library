/**
 * handlePipelineStepErrors
 * Used by other steps to make error analysis easier. Lists parameters and other data available to the step in which the error occurs.
 *
 * @param stepParameters The parameters from the step to be executed. The list of parameters is then shown in the console output.
 * @param stepName The name of the step executed to be shown in the console output.
 * @param echoDetails Echo beginning and end of step call as well as detailed error message in case of failure.
 */

def call(Map parameters = [:], body) {

    def stepParameters = parameters.stepParameters //mandatory
    def stepName = parameters.stepName //mandatory
    def echoDetails = parameters.get('echoDetails', true)

    try {

        if (stepParameters == null && stepName == null)
            error "step handlePipelineStepErrors requires following mandatory parameters: stepParameters, stepName"

        if (echoDetails)
            echo "--- BEGIN LIBRARY STEP: ${stepName}.groovy ---"

        body()

    } catch (Throwable err) {
        if (echoDetails)
            echo """----------------------------------------------------------
--- ERROR OCCURED IN LIBRARY STEP: ${stepName}
----------------------------------------------------------

FOLLOWING PARAMETERS WERE AVAILABLE TO THIS STEP:
***
${stepParameters}
***

ERROR WAS:
***
${err}
***

FURTHER INFORMATION:
* Documentation of step ${stepName}: .../${stepName}/
* Pipeline documentation: https://...
* GitHub repository for pipeline steps: https://...
 
----------------------------------------------------------"""
        throw err
    } finally {
        if (echoDetails)
            echo "--- END LIBRARY STEP: ${stepName}.groovy ---"
    }
}
