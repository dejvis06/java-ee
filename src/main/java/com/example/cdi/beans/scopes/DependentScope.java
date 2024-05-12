package com.example.cdi.beans.scopes;

import javax.enterprise.context.Dependent;
import java.io.Serializable;

@Dependent
public class DependentScope implements Serializable {

    public String getHashCode() {
        return this.hashCode() + " ";
    }
}
