package dev.splityosis.sysengine.configlib.exceptions;

import dev.splityosis.sysengine.configlib.configuration.Configuration;

public class ConfigNotRegisteredException extends RuntimeException{

    public ConfigNotRegisteredException(Configuration configuration){
        super("Config of type '"+configuration.getClass()+"' is not registered.");
    }
}
