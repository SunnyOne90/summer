package com.gf.summer.formework.context;

import com.gf.summer.formework.context.support.GPAbstractApplicationContext;
import com.gf.summer.formework.core.GPBeanFactory;

public class GPApplicationContext extends GPAbstractApplicationContext implements GPBeanFactory {

    private String[] configLoactions;

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

    }

    public Object getBean(String beanName) {
        return null;
    }
}
