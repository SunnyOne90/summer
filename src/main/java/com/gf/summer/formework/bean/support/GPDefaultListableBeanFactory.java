package com.gf.summer.formework.bean.support;

import com.gf.summer.formework.bean.config.GPBeanDefinition;
import com.gf.summer.formework.context.support.GPAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPDefaultListableBeanFactory extends GPAbstractApplicationContext {
    //存储注册信息的BeanDefinition
    protected final Map<String, GPBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, GPBeanDefinition>();
}
