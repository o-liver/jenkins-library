package com.sap.piper

import com.cloudbees.groovy.cps.NonCPS

/**
 * Utils
 * Provides utility functions.
 *
 */

/**
 * Retrieves the parameter value for parameter `paramName` from parameter map `map`. In case there is no parameter with the given key contained in parameter map `map` `defaultValue` is returned. In case there no such parameter contained in `map` and `defaultValue` is `null` an exception is thrown.
 * @param map
 *           A map containing configuration parameters.
 * @param paramName
 *           The key of the parameter which should be looked up.
 * @param defaultValue
 *           The value which is returned in case there is no parameter with key `paramName` contained in `map`.
 * @return The value to the parameter to be retrieved, or the default value if the former is `null`, either since there is no such key or the key is associated with value `null`. In case the parameter is not defined or the value for that parameter is `null`and there is no default value an exception is thrown.
 */
@NonCPS
def getMandatoryParameter(Map map, paramName, defaultValue) {

    def paramValue = map[paramName]

    if (paramValue == null)
        paramValue = defaultValue

    if (paramValue == null)
        throw new Exception("ERROR - NO VALUE AVAILABLE FOR ${paramName}")
    return paramValue

}

/**
 * Retrieves the git-remote-url and git-branch. The parameters 'GIT_URL' and 'GIT_BRANCH' are retrieved from Jenkins job configuration. If these are not set, the git-url and git-branch are retrieved from the same repository where the Jenkinsfile resides.
 * @param script
 *           The script calling the method. Basically the `Jenkinsfile`. It is assumed that the script provides access to the parameters defined when launching the build, especially `GIT_URL` and `GIT_BRANCH`.
 * @return A map containing git-url and git-branch: `[url: gitUrl, branch: gitBranch]`
 */
def retrieveGitCoordinates(script){
    def gitUrl = script.params.GIT_URL
    def gitBranch = script.params.GIT_BRANCH
    if(!gitUrl && !gitBranch) {
        echo "[INFO] Parameters 'GIT_URL' and 'GIT_BRANCH' not set in Jenkins job configuration. Assuming application to be built is contained in the same repository as this Jenkinsfile."
        gitUrl = scm.userRemoteConfigs[0].url
        gitBranch = scm.branches[0].name
    }
    else if(!gitBranch) {
        error "Parameter 'GIT_BRANCH' not set in Jenkins job configuration. Either set both GIT_URL and GIT_BRANCH of the application to be built as Jenkins job parameters or put this Jenkinsfile into the same repository as the application to be built."
    }
    else if(!gitUrl) {
        error "Parameter 'GIT_URL' not set in Jenkins job configuration. Either set both GIT_URL and GIT_BRANCH of the application to be built as Jenkins job parameters or put this Jenkinsfile into the same repository as the application to be built."
    }
    echo "[INFO] Building '${gitBranch}@${gitUrl}'."

    return [url: gitUrl, branch: gitBranch]
}

