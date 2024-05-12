package com.example.cdi.beans.scopes;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class RequestScope {


    public String getHashCode() {

        return this.hashCode() + " ";
    }


}
