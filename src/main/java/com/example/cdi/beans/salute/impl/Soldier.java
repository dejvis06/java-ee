package com.example.cdi.beans.salute.impl;



import com.example.cdi.beans.salute.Salute;
import com.example.cdi.beans.salute.qualifiers.ServiceMan;

import javax.ejb.Stateless;
import java.text.MessageFormat;

@Stateless
@ServiceMan(value = ServiceMan.ServiceType.SOLDIER)
@com.example.cdi.beans.salute.qualifiers.Soldier
public class Soldier implements Salute {
    @Override
    public String salute(String name) {
        return MessageFormat.format("Aye Aye Capt {0}", name);
    }
}
