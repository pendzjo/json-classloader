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
public class MemoryClassLoader implements AutoCloseable {

    private final URLClassLoader GLOBAL_BASE_JSON_CLASS_LOADER =
            URLClassLoader.newInstance(new URL[0], null);

    private final Map<Path, URLClassLoader> CLASSLOADER_MEMORY =
            new ConcurrentHashMap<>();

    public URLClassLoader produce(Path p) throws
            MalformedURLException {
        return produce(JsonClassLoader.createJsonClassLoader(p));
    }

    public URLClassLoader produce(
            JsonClassLoader jcl) throws
            MalformedURLException {
        if (jcl.getFileLocation() != null && CLASSLOADER_MEMORY.containsKey(
                jcl.getFileLocation())) {
            System.out.printf(
                    "Seen this jcl [%s] before going to return ClassLoader for it %n",
                    jcl);
            return CLASSLOADER_MEMORY.get(jcl.getFileLocation());
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
                URLClassLoader tmp = CLASSLOADER_MEMORY.get(other);
                if (tmp == null) {
                    tmp = produce(other);
                    cls.addAll(Lists.newArrayList(tmp));
                }
            }
            Set<URL> urls = new HashSet<>();
            for (URLClassLoader ucl : cls) {
                if (ucl != null && ucl.getURLs() != null) {
                    for (URL url : ucl.getURLs()) {
                        urls.add(url);
                    }
                }
            }
            for (URL url : jcl.getClasses()) {
                urls.add(url);
            }

            CLASSLOADER_MEMORY.put(jcl.getFileLocation(), new URLClassLoader(
                    urls.toArray(
                            new URL[urls.size()]), parent));
            return CLASSLOADER_MEMORY.get(jcl.getFileLocation());
        }

    }

    @Override
    public void close() {
        for (URLClassLoader ucl : CLASSLOADER_MEMORY.values()) {
            try {
                ucl.close();
            } catch (Exception ex) {

            }
        }
        CLASSLOADER_MEMORY.clear();
    }

}
