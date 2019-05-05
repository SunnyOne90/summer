package com.gf.summer.formework.bean.support;

import com.gf.summer.formework.bean.config.GPBeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GPBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";
    //将配置文件读入到内存中
    public GPBeanDefinitionReader(String... locations){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));
        try {
            config.load(is);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null != is){
                try {
                    is.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String scanPackage) {
        URL url = this.getClass().getResource("/"+scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for(File file:classPath.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage + "."+file.getName());
            }else {
                if(!file.getName().endsWith(".class"))continue;
                String className = (scanPackage + "."+file.getName().replace(".class",""));
                registyBeanClasses.add(className);
            }
        }
    }
    public Properties getConfig(){
        return this.config;
    }

    //将配置信息转换为GPBeanDefinitiond对象
    public List<GPBeanDefinition> loadBeanDefinitions(){
        List<GPBeanDefinition> result = new ArrayList<GPBeanDefinition>();
        try {
            for (String className :registyBeanClasses){
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()) continue;
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()),beanClass.getName()));
                result.add(doCreateBeanDefinition(beanClass.getName(),beanClass.getName()));
                Class<?> [] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    //如果是多个实现类，只能覆盖
                    //为什么？因为Spring没那么智能，就是这么傻
                    //这个时候，可以自定义名字
                    System.out.println(i.getName());
                    System.out.println(beanClass.getName());
                    result.add(doCreateBeanDefinition(i.getName(),beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private GPBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName) {
        GPBeanDefinition beanDefinition = new GPBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }
    private String toLowerFirstCase(String simpleName){
        char [] chars = simpleName.toCharArray();
        chars[0] +=32;
        return String.valueOf(chars);
    }


}
