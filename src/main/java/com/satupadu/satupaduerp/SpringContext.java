package com.satupadu.satupaduerp;

import com.satupadu.satupaduerp.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// menyimpan container Spring (IoC container) supaya form Swing bisa mengambil controller
public class SpringContext {
    private static AnnotationConfigApplicationContext context;

    public static void start() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }
}
