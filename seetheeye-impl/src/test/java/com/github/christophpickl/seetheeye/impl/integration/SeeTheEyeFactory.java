package com.github.christophpickl.seetheeye.impl.integration;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.impl.AbstractConfig;
import com.github.christophpickl.seetheeye.impl.SeeTheEye;

class SeeTheEyeFactory {

    static SeeTheEyeApi newEye(Action1<Config> action) {
        AbstractConfig config = new AbstractConfig() {};
        action.exec(config);
        return SeeTheEye.prepare().configs(config).build();
    }

}
