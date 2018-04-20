package com.jd.jvm.jmonitor.core.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * <P>Title: ClassUtil.java</P>
 * <P>Description: 类操作工具类</P>
 * @author wb
 * @version V1.0
 * @date 2016-12-29 17:10
 */
public class ClassUtils {
	
    private static final Logger logger = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 加载类
     * @param className
     * @param isInitialized
     * @return
     */
    public static Class<?> loadClass(ClassLoader classLoader, String className,Boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className,isInitialized,classLoader);
        } catch (ClassNotFoundException e) {
            logger.error("load class failure",e);
           throw  new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下的所有类
     * @param packageName
     * @return
     */
    public static Set<Class<?>> getClassSet(ClassLoader classLoader, String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        //包名转换成文件目录地址 举例com.alibaba.taobao 转换为 com/alibaba/taobao
        try {
            Enumeration<URL> urls = classLoader.getResources(packageName.replace(".", "/"));
            while (urls.hasMoreElements()){
                URL url =  urls.nextElement();
                if(null != url){
                    String  protocol = url.getProtocol();
                    if(protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20","");//去空格
                        addClass(classLoader, classSet,packagePath,packageName);
                    }else if(protocol.equals("jar")){
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if(null != jarURLConnection){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while (jarEntries.hasMoreElements()){
                                JarEntry jarEntry = jarEntries.nextElement();
                                String jarEntryName = jarEntry.getName();
                                if(jarEntryName.endsWith(".class") && 
                                		jarEntryName.startsWith(packageName.replace(".", "/"))){
                                    String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                    doAddClass(classLoader, classSet, className);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("get class set failure",e);
        }
        return classSet;
    }

    private static void addClass(ClassLoader classLoader, Set<Class<?>> classSet, String packagePath, String packageName) {

        final File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && file.getName().endsWith(".class") || file.isDirectory());
            }
        });
        for(File f : files){//遍历当前的目录的所有文件如果是class文件则直接放入set中，否则继续遍历
            String name = f.getName();
            if (f.isFile()){
                String className = name.substring(0,name.lastIndexOf("."));
                if(StringUtils.isNotEmpty(packageName)){
                    className = packageName + "." + className;
                }
                doAddClass(classLoader, classSet, className);
            }else {//递归调用一直到文件目录最后一级
                String subPackagePath = name;
                if(StringUtils.isNotEmpty(subPackagePath)){
                    subPackagePath = packagePath + "/" + subPackagePath;
                }
                String subPackageName = name;
                if (StringUtils.isNotEmpty(packageName)) {
                    subPackageName = packageName + "." + subPackageName;
                }
                addClass(classLoader, classSet, subPackagePath, subPackageName);
            }
        }
    }

    private static void doAddClass(ClassLoader classLoader, Set<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(classLoader, className, false);
        classSet.add(cls);
    }
}