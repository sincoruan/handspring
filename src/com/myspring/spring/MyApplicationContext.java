package com.myspring.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap();

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
                                beanDefinition.setType(clazz);

                                if(clazz.isAnnotationPresent(Scope.class)){
                                    beanDefinition.setScope(clazz.getAnnotation(Scope.class).value());
                                }
                                else{
                                    beanDefinition.setScope("singleton");
                                }
                                String beanName = clazz.getAnnotation(Component.class).value();
                                if("".equals(beanName)) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
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
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if("singleton".equals(beanDefinition.getScope()) || beanDefinition.getScope() == null){
            if(!beanMap.containsKey(beanName)) {
                beanMap.put(beanName, createBean(beanDefinition));
            }
            return beanMap.get(beanName);
        }
        else{
            return createBean(beanDefinition);
        }
    }
    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        Object object;
        try {
            object = clazz.getConstructor().newInstance();
            for(Field field : clazz.getDeclaredFields()) {
                if(field.isAnnotationPresent(Autowire.class)){
                    field.setAccessible(true);
                    field.set(object, getBean(field.getName()));
                }
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
