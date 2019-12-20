package com.sap.piper

import com.sap.piper.MapUtils

@API
class DefaultValueCache implements Serializable {
    private static DefaultValueCache instance

    private Map defaultValues

    private DefaultValueCache(Map defaultValues){
        this.defaultValues = defaultValues
    }

    static getInstance(scriptBinding){
        if (instance) {
            return instance
        }
        if(scriptBinding?.hasVariable("defaultValueCacheInstance")) {
            Map defaultValues = scriptBinding?.getProperty("defaultValueCacheInstance")
            return createInstance(defaultValues, scriptBinding)
        }
    }

    static createInstance(Map defaultValues, scriptBinding){
        instance = new DefaultValueCache(defaultValues)
        if(!scriptBinding?.hasVariable("defaultValueCacheInstance")) {
            scriptBinding?.setProperty("defaultValueCacheInstance", defaultValues)
        }
        return instance
    }

    static boolean hasInstance(){
        return instance!=null
    }

    Map getDefaultValues(){
        return defaultValues
    }

    static reset(){
        instance = null
    }

    static void prepare(Script steps, Map parameters = [:]) {
        if(parameters == null) parameters = [:]
        if(!DefaultValueCache.hasInstance() || parameters.customDefaults) {
            def defaultValues = [:]
            def configFileList = ['default_pipeline_environment.yml']
            def customDefaults = parameters.customDefaults

            if(customDefaults in String)
                customDefaults = [customDefaults]
            if(customDefaults in List)
                configFileList += customDefaults
            for (def configFileName : configFileList){
                if(configFileList.size() > 1) steps.echo "Loading configuration file '${configFileName}'"
                def configuration = steps.readYaml text: steps.libraryResource(configFileName)
                defaultValues = MapUtils.merge(
                        MapUtils.pruneNulls(defaultValues),
                        MapUtils.pruneNulls(configuration))
            }
            DefaultValueCache.createInstance(defaultValues, steps.getBinding())
        }
    }

    @Override
    public String toString() {
        return "DefaultValueCache{" +
            "defaultValues=" + defaultValues +
            '}'
    }
}
