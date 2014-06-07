package com.github.christophpickl.seetheeye.impl2.validation;

import com.github.christophpickl.seetheeye.api.configuration.ConfigurationDeclaration;
import com.github.christophpickl.seetheeye.api.SeeTheEyeException;
import com.github.christophpickl.seetheeye.impl2.configuration.DefinitionRepository;
import com.google.common.base.Joiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;

public class ConfigurationValidator {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationValidator.class);

    public void validatePre(Collection<ConfigurationDeclaration> declarations) {
        LOG.debug("validatePre(declarations)");

        Collection<String> errorMessages = new LinkedList<>();
        errorMessages.addAll(new PreValidator(declarations).detect());
        throwExceptionIfErrorExists(errorMessages);
    }

    public void validatePost(DefinitionRepository repo) {
        LOG.debug("validatePost(repo)");
        Collection<String> errorMessages = new LinkedList<>();
        errorMessages.addAll(new PostDependencyCycleDetector(repo).detect());
        throwExceptionIfErrorExists(errorMessages);
    }


    private static void throwExceptionIfErrorExists(Collection<String> errorMessages) {
        if (!errorMessages.isEmpty()) {
            String prefix = "\n\t- ";
            throw new SeeTheEyeException.ConfigInvalidException(
                    "Following errors occured:" + prefix + Joiner.on(prefix).join(errorMessages));
        }
    }
}
