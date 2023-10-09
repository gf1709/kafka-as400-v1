package com.gf.kafka_as400;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class StaticContextInitializer {
    @Autowired
    private ConfigurableEnvironment _environment;

    @PostConstruct
    public void init() {
        AppConfig.setEnvironment(_environment);
    }
}
