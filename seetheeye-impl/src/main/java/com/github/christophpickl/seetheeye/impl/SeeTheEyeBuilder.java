package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.event.Observes;
import javax.inject.Provider;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class SeeTheEyeBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(SeeTheEyeBuilder.class);

    private final Collection<AbstractConfig> configs = new HashSet<>();

    SeeTheEyeBuilder() {}

    public SeeTheEyeBuilder configs(AbstractConfig config, AbstractConfig... evenMore) {
        addConfig(config);
        for (AbstractConfig more : evenMore) {
            addConfig(more);
        }
        return this;
    }

    private void addConfig(AbstractConfig config) {
        LOG.debug("Installing config: {}", config.getClass().getName());
        configs.add(config);
        // we could also preValidate here ;)
    }


    public SeeTheEye build() {
        preValidate();
        Collection<Bean> beans = new LinkedHashSet<>();
        Collection<Class<? extends Provider<?>>> providers = new LinkedHashSet<>();
        for (AbstractConfig config : configs) {
            config.configure();
            beans.addAll(config.getInstalledBeans());
            providers.addAll(config.getInstalledProviders());
        }
        addOptionalObserves(beans);
        postValidate(beans, providers);
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


    private void postValidate(Collection<Bean> beans, Collection<Class<? extends Provider<?>>> providers) {
        new CycleDetector().validateNoInjectCycles(beans);

        Map<Class<?>, Bean> beansByType = new HashMap<>();
        for (Bean bean : beans) {
            Class<?> beanType;
            if (bean.getBeanInterface().isPresent()) {
                beanType = bean.getBeanInterface().get();
            } else {
                beanType = bean.getBeanType();
            }
            beansByType.put(beanType, bean);
        }

        for (Class<? extends Provider<?>> provider : providers) {
            Class<?> providingBeanType = SeeTheEye.extractProviderTypeParameter(provider);
            if (beansByType.containsKey(providingBeanType)) {
                throw new SeeTheEyeException.ConfigInvalidException("Configured both, a provider and a bean for type: " + providingBeanType.getName());
            }
        }
    }


    private void preValidate() {
        Collection<Class<?>> installedBeanTypes = new HashSet<>();
        Collection<Class<?>> installedBeanInterfaces = new HashSet<>();
        for (AbstractConfig config : configs) {
            for (Bean bean : config.getInstalledBeans()) {
                if (bean.getBeanInterface().isPresent()) {
                    Class<?> interfase = bean.getBeanInterface().get();
                    if (installedBeanInterfaces.contains(interfase)) {
                        throw new SeeTheEyeException.ConfigInvalidException("Duplicate bean interface registration for: " + interfase.getName() + "!");
                    }
                    installedBeanInterfaces.add(interfase);

                } else {
                    MetaClass type = bean.getMetaClass();
                    if (installedBeanTypes.contains(type.getClazz())) {
                        throw new SeeTheEyeException.ConfigInvalidException("Duplicate bean type registration for: " + type.getName() + "!");
                    }
                    installedBeanTypes.add(type.getClazz());
                }
            }
        }
    }

    static class CycleDetector {

        private Collection<Class<?>> beansInProgress; // yesss, shared state ;)

        public void validateNoInjectCycles(Collection<Bean> beans) {
            LOG.debug("Validating no cycles injected.");
            beansInProgress = new HashSet<>();
            for (Bean bean : beans) {
                validateNoInjectCyclesRecursive(bean.getMetaClass());
            }
        }

        private void validateNoInjectCyclesRecursive(MetaClass type) {
            beansInProgress.add(type.getClazz());

            List<Class<?>> params = type.getConstructorParameters();
            LOG.trace("Validating bean '{}' with params: {}", type.getName(), Arrays.toString(params.toArray()));
            for (Class<?> param : params) {
                if (beansInProgress.contains(param)) {
                    throw new SeeTheEyeException.ConfigInvalidException("Cyclic dependency found for bean of type '" + type.getName() + "'!" +
                            " (Beans in progress: " + Arrays.toString(beansInProgress.toArray()) + ")");
                }
                if (param.isInterface()) {
                    // i think this is somehow a hack. validation needs to happen somewhen later, where we have Bean access.
                    continue;
                }
                validateNoInjectCyclesRecursive(new MetaClass(param));
            }
//                FIXME !!! beansInProgress.remove(type.getClazz());
        }
    }

}
