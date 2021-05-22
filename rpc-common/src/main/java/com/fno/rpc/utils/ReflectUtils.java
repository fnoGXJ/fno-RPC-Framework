package com.fno.rpc.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class ReflectUtils {
    public static String getMainClass() {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        return stackTrace[stackTrace.length - 1].getClassName();
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        String packagePath = packageName.replace(".", "/");
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packagePath);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    addClassByFile(packageName, filePath, classes);
                } else if ("jar".equals(protocol)) {
                }
            }
        } catch (IOException e) {

        }
        return classes;
    }

    private static void addClassByFile(String packageName, String packagePath, Set<Class<?>> classes) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] dirFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().endsWith(".class");
            }
        });
        for (File file : dirFiles) {
            if (file.isDirectory()) {
                addClassByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void addClassByJar(String packagePath, Set<Class<?>> classes) {

    }
}
