package com.example.cdi.beans;

import com.example.cdi.beans.types.Logged;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

@Logged
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LoggedInterceptor {

    @Inject
    private Logger logger;

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        try {
            return context.proceed(); // Proceed with method invocation
        } finally {
            // log after proceeding with method invocation
            logger.log(Level.INFO, "Intercepting method {0}", context.getMethod().getName());
        }
    }
}
