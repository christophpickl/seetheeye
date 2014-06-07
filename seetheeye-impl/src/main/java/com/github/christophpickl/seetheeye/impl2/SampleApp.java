package com.github.christophpickl.seetheeye.impl2;

import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.configuration.AbstractConfiguration;

class SampleApp {

    static { Log4j.init(); }

    public static void main(String[] args) {
        SeeTheEyeApi eye = SeeTheEye.builder().add(new SampleConfiguration()).build();
        eye.get(Greeter.class).greet("World");
    }

    static class SampleConfiguration extends AbstractConfiguration {
        @Override protected void configure() {
            installBean(InformalSalutator.class).as(Salutator.class);
            installBean(StandardOutGreeter.class).as(Greeter.class);
        }
    }

    interface Salutator { String salutation(); }
    static class InformalSalutator implements Salutator { @Override public String salutation() { return "Hello"; } }

    interface Greeter { void greet(String name); }
    static class StandardOutGreeter implements Greeter {
        private final Salutator salutator;
        // @Inject is optional if only a single constructor is existing anyway
        StandardOutGreeter(Salutator salutator) { this.salutator = salutator; }
        @Override public void greet(String name) { System.out.println(salutator.salutation() + " " + name + "!"); }
    }

}
