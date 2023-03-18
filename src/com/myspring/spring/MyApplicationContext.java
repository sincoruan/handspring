package com.myspring.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MyApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap();
    private ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap();

    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

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
                                if(BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessorList.add(beanPostProcessor);
                                }
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
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
                beanMap.put(beanName, createBean(beanDefinition, beanName));
            }
            return beanMap.get(beanName);
        }
        else{
            return createBean(beanDefinition, beanName);
        }
    }
    private Object createBean(BeanDefinition beanDefinition, String beanName) {
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
            if(object instanceof BeanNameAware) {
                ((BeanNameAware) object).setBeanName(beanName);
            }
            if(object instanceof InitializingBean) {
                ((InitializingBean) object).afterPropertiesSet();
            }
            for(BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                beanPostProcessor.postBeanInitialize(object, beanName);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return object;
    }
}
