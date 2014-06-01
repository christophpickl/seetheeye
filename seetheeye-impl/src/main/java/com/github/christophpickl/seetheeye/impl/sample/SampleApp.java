package com.github.christophpickl.seetheeye.impl.sample;

import com.github.christophpickl.seetheeye.impl.AbstractConfig;
import com.github.christophpickl.seetheeye.impl.Log4j;
import com.github.christophpickl.seetheeye.impl.SeeTheEye;
import com.google.common.base.Preconditions;

import javax.inject.Inject;

class SampleApp {

    static {
        Log4j.init();
    }

    public static void main(String[] args) {
        SeeTheEye eye = SeeTheEye.prepare().configs(new SampleConfig()).build();
        Service service = eye.get(Service.class);
        service.greet();
    }

    static class SampleConfig extends AbstractConfig {

        @Override protected void configure() {
            installConcreteBean(BackendImpl.class).as(Backend.class);
            installConcreteBean(ServiceImpl.class).as(Service.class);
        }
    }

    interface Backend {
        String loadName();
    }

    static class BackendImpl implements Backend {

        @Override
        public String loadName() {
            return "World";
        }
    }

    interface Service {
        void greet();
    }

    static class ServiceImpl implements Service {

        private final Backend backend;

        @Inject ServiceImpl(Backend backend) {
            this.backend = Preconditions.checkNotNull(backend);
        }

        @Override
        public void greet() {
            System.out.println("Hello " + backend.loadName() + "!");
        }
    }

}
