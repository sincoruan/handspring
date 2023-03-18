package com.myspring.spring;

import java.io.File;
import java.net.URL;

public class MyApplicationContext {
    private Class configClass;
    public MyApplicationContext(Class configClass) {
        this.configClass = configClass;

        //扫描
        if(configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScan.value();//packagename:com.myspring.service
            path = path.replace(".", "/");

            ClassLoader classLoader = MyApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);

            File file = new File(resource.getFile());

            System.out.println(file);
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                for(File f: files) {
                    String fileName = f.getAbsolutePath();
                    if(fileName.endsWith(".class")) {
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        className = className.replace("/",".");
                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if(clazz.isAnnotationPresent(Component.class)){

                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }
    public Object getBean(String beanName) {
        return null;
    }
}
