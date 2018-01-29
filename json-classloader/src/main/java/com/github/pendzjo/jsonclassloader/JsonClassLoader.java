//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import com.github.pendzjo.jsonclassloader.uri.ClassLoaderURI;
import com.google.common.base.MoreObjects;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author johnnp
 */
public class JsonClassLoader {

    private String id;

    private Path fileLocation;

    private boolean relativePath;

    private ClassLoaderURI parentURI;

    private ClassLoaderURI classes[];

    public static JsonClassLoader createJsonClassLoader(Path toJson) {
        JsonClassLoader jcl = null;
        if (toJson != null && Files.isReadable(toJson)) {
            try (BufferedReader br = Files.newBufferedReader(toJson,
                    StandardCharsets.UTF_8)) {
                jcl = JsonClassLoaderConstants.gson.fromJson(br,
                        JsonClassLoader.class);
                jcl.setFileLocation(toJson.getParent());
                return jcl;
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        return jcl;

    }

    private void setFileLocation(Path p) {
        this.fileLocation = p.normalize();

        for (ClassLoaderURI clu : classes) {
            clu.setBasePath(this.fileLocation);
        }

        if (this.parentURI != null) {
            this.parentURI.setBasePath(this.fileLocation);
        }
    }

    public JsonClassLoader() {
    }

    private JsonClassLoader generate(ClassLoaderURI uri) {
        if (uri != null && "json".equals(uri.getSchema())) {
            return JsonClassLoader.createJsonClassLoader(Paths.get(
                    uri.getURI()));
        }
        return null;
    }

    public JsonClassLoader getParent() {
        return generate(this.parentURI);
    }

    public URI getParentURI() {
        if (this.parentURI != null) {
            return this.parentURI.getURI();
        }
        return null;
    }

    public URL[] getClasses() throws MalformedURLException {
        List<URL> urls = new ArrayList<>();
        for (ClassLoaderURI uri : this.classes) {
            if ("jar".equals(uri.getSchema())) {
                urls.add(uri.toURL());
            }
        }
        return urls.toArray(new URL[urls.size()]);
    }

    public ClassLoaderURI[] getRawClasses() {
        return this.classes.clone();
    }

    public JsonClassLoader[] getClassLoaders() {
        List<JsonClassLoader> loaders = new ArrayList<>();
        for (ClassLoaderURI uri : this.classes) {
            JsonClassLoader gen = generate(uri);
            if (gen != null) {
                loaders.add(gen);
            }
        }

        return loaders.toArray(new JsonClassLoader[loaders.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof JsonClassLoader)) {
            return false;
        }
        return o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, relativePath, fileLocation, parentURI,
                classes);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("id", id).
                add("relativePath", relativePath).
                add("fileLocation", fileLocation).
                add("parentURI", parentURI).
                add("classes",
                        classes).toString();
    }

    /**
     *
     * @return
     */
    public Path getFileLocation() {
        return this.fileLocation;
    }

}
