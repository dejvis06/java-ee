package com.example.cdi.beans.salute.impl;

import com.example.cdi.beans.salute.Salute;
import com.example.cdi.beans.salute.qualifiers.ServiceMan;

import javax.ejb.Stateful;
import java.io.Serializable;
import java.text.MessageFormat;

@Stateful
@ServiceMan(value = ServiceMan.ServiceType.POLICE)
@com.example.cdi.beans.salute.qualifiers.Police
public class Police implements Salute, Serializable {
    @Override
    public String salute(String name) {
        return MessageFormat.format("Yes sir! {0}", name);
    }
}
