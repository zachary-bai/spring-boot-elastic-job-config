package com.zachary.elasticjob.util;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author zachary
 */
public class ClassUtil {

    public static Object getInstance(String className) throws Exception {
        Class clazz = Class.forName(className);
        return getInstance(clazz);
    }

    /**
     * 获取传入类型的一个实例
     *
     * @param clazz
     * @return
     * @throws Exception
     */
    public static Object getInstance(Class clazz) throws Exception {
        //得到所有构造器
        Constructor[] cons = null;
        try {
            cons = clazz.getConstructors();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalAccessException("构造器错误！");
        }
        if (cons == null || cons.length < 1) {
            throw new IllegalAccessException("没有默认构造方法！");
        }
        //如果上面没错，就有构造方法

        //得到默认构造器,第0个是默认构造器，无参构造方法
        Constructor defCon = cons[0];
        return defCon.newInstance();
    }

    public static Set<Class> getAllParentInterfaces(Class clazz) {
        Set<Class> interfacesSet = new LinkedHashSet<>();
        Class[] interfaces = clazz.getInterfaces();
        if (interfaces == null || interfaces.length <= 0) {
            return Collections.emptySet();
        }
        for (Class each : interfaces) {
            interfacesSet.add(each);
            interfacesSet.addAll(getAllParentInterfaces(each));
        }

        return interfacesSet;
    }


}
