package com.gf.summer.formework.context;

import com.gf.summer.formework.annotation.GPAutowired;
import com.gf.summer.formework.annotation.GPController;
import com.gf.summer.formework.annotation.GPService;
import com.gf.summer.formework.bean.GPBeanWrapper;
import com.gf.summer.formework.bean.config.GPBeanDefinition;
import com.gf.summer.formework.bean.support.GPBeanDefinitionReader;
import com.gf.summer.formework.bean.support.GPDefaultListableBeanFactory;
import com.gf.summer.formework.context.support.GPAbstractApplicationContext;
import com.gf.summer.formework.core.GPBeanFactory;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GPApplicationContext extends GPDefaultListableBeanFactory implements GPBeanFactory {

    private String[] configLoactions;
    private GPBeanDefinitionReader reader;
    //保存单利bean的容器
    private Map<String,Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    //通用的IOC容器
    private Map<String,GPBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, GPBeanWrapper>();

    public GPApplicationContext(String ... configLoactions){
        this.configLoactions = configLoactions;
        try {
            refresh();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void refresh() throws Exception {
        //定位配置文件相关信息
        reader = new GPBeanDefinitionReader(configLoactions);
        //加载配置文件信息 封装成
        List<GPBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册  将bean的相关信息放入ioc容器中
        doRegisterBeanDefinition(beanDefinitions);
        //把不是延迟加载的bean进行提前初始化
        doAutowrited();
    }

    private void doAutowrited() {
        for (Map.Entry<String, GPBeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if(!entry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
    }

    private void doRegisterBeanDefinition(List<GPBeanDefinition> beanDefinitions)throws Exception {
        for (GPBeanDefinition beanDefinition :beanDefinitions){
            if(super.beanDefinitionMap.containsKey(beanDefinition)){
                throw new Exception("The “" + beanDefinition.getFactoryBeanName() + "” is exists!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
            //到这里为止容器初始化完毕
        }
    }
    //依赖注入，从这里开始读取beanDefinition中的信息
    //然后通过反射创建一个实例并返回
    //spring做法是，不会把最原始的对象放出去，会用一个beanWrapper来进行一次包装
    public Object getBean(String beanName) {
        Object instance = null;
        //通过beanName 取出BeanDefinition相关信息
        GPBeanDefinition beanDefinition = super.beanDefinitionMap.get(beanName);
        //实例化bean
        instance = instantiateBean(beanName,beanDefinition);
        //将实例化后的bean保存到beanWrapper中
        GPBeanWrapper beanWrapper = new GPBeanWrapper(instance);
        //将beanWrapper保存到容器中
        this.factoryBeanInstanceCache.put(beanName,beanWrapper);
        //依赖注入类中的相关变量
        populateBean(beanName,new GPBeanDefinition(),beanWrapper);

        return instance;
    }


    private Object instantiateBean(String beanName, GPBeanDefinition gpBeanDefinition) {
        String className = gpBeanDefinition.getBeanClassName();
        //1.拿到实例化的对象和类名

        Object instance = null;
        try {
            if(this.singletonObjects.containsKey(className)){
                instance = singletonObjects.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonObjects.put(className,instance);
                this.singletonObjects.put(gpBeanDefinition.getFactoryBeanName(),instance);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return instance;
    }

    private void populateBean(String beanName, GPBeanDefinition gpBeanDefinition, GPBeanWrapper gpBeanWrapper) {
        Object obj = gpBeanWrapper.getWrappedInstance();
        Class<?> clazz = gpBeanWrapper.getWrappedClass();
        //如果这个类中不存在这两个注解，那么就return
        if(!(clazz.isAnnotationPresent(GPController.class) || clazz.isAnnotationPresent(GPService.class)))return;
        //反射取出这个类中的所有信息
        Field[] fields = clazz.getDeclaredFields();
        for (Field field: fields){
            //查找有没有GPAutowired 这样的注解
            if(!field.isAnnotationPresent(GPAutowired.class))continue;
            GPAutowired autowired = field.getAnnotation(GPAutowired.class);
            String autowiredName = autowired.value().trim();
            if("".equals(autowiredName)){
                //取出这个带有标签的变量类型  也就是类的全路径
                autowiredName = field.getType().getName();
            }
            //强制访问
            field.setAccessible(true);
            if(this.factoryBeanInstanceCache.get(autowiredName) == null) continue;
            try {
                field.set(obj,this.factoryBeanInstanceCache.get(autowiredName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


}
