package com.example.cdi.beans;

import com.example.cdi.beans.producers.LoggerProducer;
import com.example.cdi.beans.types.Web;
import com.example.cdi.beans.scopes.ApplicationScope;
import com.example.cdi.beans.scopes.DependentScope;
import com.example.cdi.beans.scopes.RequestScope;
import com.example.cdi.beans.scopes.SessionScope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Web
public class ScopesBean implements Serializable {

    @Inject
    private Logger logger;
    @Inject
    private RequestScope requestScope;
    @Inject
    private ApplicationScope applicationScope;
    @Inject
    private SessionScope sessionScope;
    @Inject
    private DependentScope dependentScope;

    //Lifecyle callback
    @PostConstruct
    private void init() {
        logger.log(Level.INFO, "*******************************************");
        logger.log(Level.INFO, "Scopes bean called");
        logger.log(Level.INFO, "********************************************");
    }

    @PreDestroy
    private void kill() {
        logger.log(Level.INFO, "*******************************************");
        logger.log(Level.INFO, "Scopes bean gonna be killed :-( ");
        logger.log(Level.INFO, "********************************************");
    }

    public String requestScopeHashCode() {
        return requestScope.getHashCode();
    }

    public String applicatioinScopeHashCode() {
        return applicationScope.getHashCode();
    }

    public String sessionScopeHashCode() {
        return sessionScope.getHashCode();
    }

    public String dependentScopeHashCode() {
        return dependentScope.getHashCode();
    }

}
