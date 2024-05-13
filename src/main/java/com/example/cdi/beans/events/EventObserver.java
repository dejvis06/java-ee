package com.example.cdi.beans.events;

import com.example.cdi.beans.events.qualifiers.Admin;
import com.example.cdi.beans.events.qualifiers.PopularStand;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class EventObserver implements Serializable {

    @Inject
    private Logger logger;

    void plainEvent(@Observes EventData eventData) {
        //Persist in databse, send to another application outside your app
        //Essentially you can do whatever you want with the event data here.
        //We will just log it
        logger.log(Level.INFO, "User {0} logged in at {1}. Logged from plain event observer",
                new Object[]{eventData.getEmail(), eventData.getLoginTime()});
    }

    void userLoggedIn(@Observes @PopularStand EventData eventData) {
        //Persist in databse, send to another application outside your app
        //Essentially you can do whatever you want with the event data here.
        //We will just log it
        logger.log(Level.INFO, "User {0} logged in at {1}. Logged from qualified observer",
                new Object[]{eventData.getEmail(), eventData.getLoginTime()});
    }

    void asyncObserver(@ObservesAsync @PopularStand EventData eventData) {
        //Persist in databse, send to another application outside your app
        //Essentially you can do whatever you want with the event data here.
        //We will just log it
        try {
            Thread.sleep(6000);
            logger.log(Level.INFO, "User {0} logged in at {1}. Logged from async observer",
                    new Object[]{eventData.getEmail(), eventData.getLoginTime()});
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    void conditionalObserver(@Observes(notifyObserver = Reception.IF_EXISTS,
            during = TransactionPhase.IN_PROGRESS) @Admin EventData eventData) {
        logger.log(Level.INFO, "The CEO {0} logged in at {1}. Logged from conditional observer",
                new Object[]{eventData.getEmail(), eventData.getLoginTime()});
    }
}
