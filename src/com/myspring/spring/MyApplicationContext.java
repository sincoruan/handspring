package com.myspring.spring;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> concurrentHashMap = new ConcurrentHashMap();
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
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz.getClass());

                                if(clazz.isAnnotationPresent(Scope.class)){
                                    beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                                }
                                else{
                                    beanDefinition.setScope("singleton");
                                }
                                String beanName = clazz.getAnnotation(Component.class).value();
                                concurrentHashMap.put(beanName, beanDefinition);
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
