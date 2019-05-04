package com.gf.summer.formework.bean.support;

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
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for(File file:classPath.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage + "."+file.getName());
            }else {
                if(!file.getName().endsWith(".class"))continue;
            }
        }
    }


}
