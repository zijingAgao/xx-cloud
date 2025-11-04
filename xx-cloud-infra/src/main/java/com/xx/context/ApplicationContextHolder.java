package com.xx.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * @author Agao
 * @date 2025/11/4 16:01
 */
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext APP_CONTEXT;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHolder.APP_CONTEXT = applicationContext;
    }

    /**
     * Get ioc container bean by type.
     */
    public static <T> T getBean(Class<T> clazz) {
        return APP_CONTEXT.getBean(clazz);
    }

    /**
     * Get ioc container bean by name.
     */
    public static Object getBean(String name) {
        return APP_CONTEXT.getBean(name);
    }

    /**
     * Get ioc container bean by name and type.
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return APP_CONTEXT.getBean(name, clazz);
    }

    /**
     * Get a set of ioc container beans by type.
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return APP_CONTEXT.getBeansOfType(clazz);
    }

    /**
     * Find whether the bean has annotations.
     */
    public static <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
        return APP_CONTEXT.findAnnotationOnBean(beanName, annotationType);
    }

    /**
     * Get application context.
     */
    public static ApplicationContext getInstance() {
        return APP_CONTEXT;
    }

}
