package com.example.cdi.beans.producers;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
public class LoggerProducer {

    @Produces
    // Defaults to dependent
    public Logger produceLogger(InjectionPoint injectionPoint) {
        return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
    }
}
