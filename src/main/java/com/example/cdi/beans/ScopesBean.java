package com.example.cdi.beans;

import com.example.cdi.beans.types.Web;
import com.example.cdi.beans.scopes.ApplicationScope;
import com.example.cdi.beans.scopes.DependentScope;
import com.example.cdi.beans.scopes.RequestScope;
import com.example.cdi.beans.scopes.SessionScope;

import javax.inject.Inject;
import java.io.Serializable;

@Web
public class ScopesBean implements Serializable {

    //Field injection point
    @Inject
    private RequestScope requestScope;
    @Inject
    private ApplicationScope applicationScope;
    @Inject
    private SessionScope sessionScope;
    @Inject
    private DependentScope dependentScope;

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
