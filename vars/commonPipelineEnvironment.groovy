/**
 * commonPipelineEnvironment
 * Provides project specific settings.
 *
 */

class commonPipelineEnvironment implements Serializable {
    private Map configProperties = [:]

    private String mtarFilePath

    /**
     * Sets the map of configuration properties. Any existing map is overwritten.
     * @param map
     *           A map of configuration properties.
     */
    def setConfigProperties(map) {
        configProperties = map
    }
    /**
     * Returns the map of project specific configuration properties. No defensive copy is created.
     * @return A map containing project specific configuration properties.
     */
    def getConfigProperties() {
        return configProperties
    }
    /**
     * Sets property `key` with value `value`. Any existing property with key `key` is overwritten.
     * @param property
     *           The key of the property.
     * @param value
     *           The value of the property.
     */
    def setConfigProperty(property, value) {
        configProperties[property] = value
    }
    /**
     * Gets a specific value from the configuration property.
     * @param property
     *           The key of the property.
     * @return The value associated with key `key`. `null` is returned in case the property does not exist.
     */
    def getConfigProperty(property) {
        if (configProperties[property] != null)
            return configProperties[property].trim()
        else
            return configProperties[property]
    }
    /**
     * Returns the path of the mtar archive file.
     * @return The path of the mtar archive file.
     */
    def getMtarFilePath() {
        return mtarFilePath
    }
    /**
     * Sets the path of the mtar archive file. Any old value is discarded.
     * @param mtarFilePath
     *           The path of the mtar archive file name.
     */
    void setMtarFilePath(mtarFilePath) {
        this.mtarFilePath = mtarFilePath
    }
}
