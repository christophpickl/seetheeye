package com.github.christophpickl.seetheeye.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Provider;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

@Deprecated
public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final Collection<AbstractConfig> configs = new HashSet<>();

    SeeTheEyeBuilder() {}

    public SeeTheEyeBuilder configs(AbstractConfig config, AbstractConfig... evenMore) {
        return this;
    }

    public SeeTheEye build() {
//        preValidate();
        Collection<Bean> beans = new LinkedHashSet<>();
        Collection<Class<? extends Provider<?>>> providers = new LinkedHashSet<>();
        for (AbstractConfig config : configs) {
            config.configure();
            beans.addAll(config.getInstalledBeans());
            providers.addAll(config.getInstalledProviders());
        }
        addOptionalObserves(beans);
//        postValidate(beans, providers);
        return new SeeTheEye(beans, providers);
    }

    private void addOptionalObserves(Collection<Bean> beans) {
        for (Bean bean : beans) {
            // FIXME hardcoded String observer
            // for each method, check params for @Observers, verify single arg, extract type param)
            for (Method method : bean.getBeanType().getDeclaredMethods()) {
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1 && parameters[0].isAnnotationPresent(Observes.class)) {
                    LOG.debug("Found observer method {} for bean: {}", method, bean);
                    bean.addObserver(new EventObserver(parameters[0].getType(), method));
                }
            }
        }

    }


}
