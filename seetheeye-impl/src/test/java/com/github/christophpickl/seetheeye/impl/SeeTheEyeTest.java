package com.github.christophpickl.seetheeye.impl;

import com.github.christophpickl.seetheeye.api.Action1;
import com.github.christophpickl.seetheeye.api.Config;
import com.github.christophpickl.seetheeye.api.SeeTheEyeApi;
import com.github.christophpickl.seetheeye.api.SeeTheEyeTestSpec;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

@Test
public class SeeTheEyeTest extends SeeTheEyeTestSpec {

    static {
        Log4j.init();
    }

//    public static void main(String[] args) {
//        new SeeTheEyeTest().installingAndGettingBackSingleConcreteBean();
//    }


    @Override
    protected SeeTheEyeApi newEye(Action1<Config> action) {
        AbstractConfig config = new AbstractConfig() {};
        action.exec(config);
        return SeeTheEye.prepare().configs(config).build();
    }

}
