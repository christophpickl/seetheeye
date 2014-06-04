package com.github.christophpickl.seetheeye.api.integration;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.testng.annotations.Test;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Test
public abstract class EventTestSpec extends BaseTest {

    static class Service {
        List<String> dispatchedEvents = new LinkedList<>();
        Event<String> event;
        // @Inject is optional if a single ctor is existing ;)
        Service(Event<String> event) {
            this.event = event;
        }
        public void fire(String eventData) {
            event.fire(eventData);
        }
        public void observe(@Observes String eventData) {
            dispatchedEvents.add(eventData);
        }
    }

    public void event() {
        SeeTheEyeApi eye = newEye(config -> config.installConcreteBean(Service.class));
        Service service = eye.get(Service.class);
        service.fire("foo");
        assertThat(service.dispatchedEvents, contains("foo"));
    }
    // split event dispatcher and observer in two separate classes
    // provider must be able to observe events as well
    // support non-singletons for event bus
    // support multiple @Observers for a single bean
    // @Observer for bound instances


}
