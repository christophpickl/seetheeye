package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // we could also validate here ;)
    }


    public SeeTheEye build() {
        validate();
        Collection<Bean> beans = new LinkedHashSet<>();
        for (AbstractConfig config : configs) {
            beans.addAll(config.getInstalledBeans());
        }
        new CycleDetector().validateNoInjectCycles(beans);
        return new SeeTheEye(beans);
    }

    private void validate() {
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
