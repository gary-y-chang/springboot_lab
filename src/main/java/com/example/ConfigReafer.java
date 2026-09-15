package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

@Component
public class ConfigReafer {
    
    @Value ("${server.port}")
    private int port;

    @Value ("${logging.level.com.example}")
    private String debugLevel;

    public int getPort() {
        return port;
    }

    public String getDebugLevel() {
        return debugLevel;
    }

    @PostConstruct 
    public void printConfig() {
        System.out.println("--------- port: " + port);
        System.out.println("--------- debugLevel: " + debugLevel);
    }

}
