package org.springframework.context;

public interface MyApplicationContextInitializer<C extends MyConfigurableApplicationContext> {

    void initialize(C applicationContext);
}
