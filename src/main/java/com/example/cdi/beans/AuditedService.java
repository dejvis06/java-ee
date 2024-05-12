package com.example.cdi.beans;

import com.example.cdi.beans.types.Logged;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class AuditedService {

    @Inject
    private Logger logger;

    @Logged
    public void auditedMethod() {
        logger.log(Level.INFO, "Auditing...");
    }
}
