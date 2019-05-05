package com.gf.summer.formework.core;

import com.gf.summer.demo.action.MyAction;
import com.gf.summer.formework.context.GPApplicationContext;
import com.gf.summer.formework.context.support.GPAbstractApplicationContext;

public class Test {
    public static void main(String[] args) {
        System.out.println(Test.class.getSimpleName());
        System.out.println(Test.class.getName());
        GPApplicationContext applicationContext = new GPApplicationContext("classpath:application.properties");
        MyAction action = (MyAction) applicationContext.getBean("myAction");
        System.out.println(action);
    }
}
