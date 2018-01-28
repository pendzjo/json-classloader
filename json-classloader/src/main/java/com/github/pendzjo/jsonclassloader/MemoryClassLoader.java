//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import com.google.common.collect.Lists;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author johnnp
 */
public class MemoryClassLoader {

    private final static URLClassLoader GLOBAL_BASE_JSON_CLASS_LOADER =
            URLClassLoader.newInstance(new URL[0], null);

    private final static Map<JsonClassLoader, URLClassLoader> CLASSLOADER_MEMORY =
            new ConcurrentHashMap<>();

    public static URLClassLoader produce(Path p) throws
            MalformedURLException {
        return produce(JsonClassLoader.createJsonClassLoader(p));
    }

    public static URLClassLoader produce(
            JsonClassLoader jcl) throws
            MalformedURLException {
        if (CLASSLOADER_MEMORY.containsKey(jcl)) {
            return CLASSLOADER_MEMORY.get(jcl);
        }
        synchronized (CLASSLOADER_MEMORY) {
            URLClassLoader parent = GLOBAL_BASE_JSON_CLASS_LOADER;
            JsonClassLoader jclParent = jcl.getParent();
            if (jclParent != null) {
                parent = CLASSLOADER_MEMORY.get(jclParent);
                if (parent == null) {
                    parent = produce(jclParent);
                }
            }

            Set<URLClassLoader> cls = new HashSet<>();
            for (JsonClassLoader other : jcl.getClassLoaders()) {
                System.out.printf("Loading other JsonClassLoader %s %n",
                        other);
                URLClassLoader tmp = CLASSLOADER_MEMORY.get(other);
                if (tmp == null) {
                    tmp = produce(other);
                    cls.addAll(Lists.newArrayList(tmp));
                }
            }
            Set<URL> urls = new HashSet<>();
            for (URLClassLoader ucl : cls) {
                for (URL url : ucl.getURLs()) {
                    urls.add(url);
                }
            }
            for (URL url : jcl.getClasses()) {
                urls.add(url);
            }

            System.out.printf("creating %s with parent %s", jcl, jclParent);
            CLASSLOADER_MEMORY.put(jcl, new URLClassLoader(urls.toArray(
                    new URL[urls.size()]), parent));
            return CLASSLOADER_MEMORY.get(jcl);
        }

    }

}
