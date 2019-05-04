package com.gf.summer.formework.bean.config;

public class GPBeanDefinition {
    private String beenClassName;

    private boolean lazyInit = false;

    private String factoryBeanName;

    public String getBeenClassName() {
        return beenClassName;
    }

    public void setBeenClassName(String beenClassName) {
        this.beenClassName = beenClassName;
    }

    public boolean isLazyInit() {
        return lazyInit;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
