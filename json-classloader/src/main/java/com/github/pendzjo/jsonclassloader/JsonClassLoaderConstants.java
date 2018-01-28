///"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader;

import com.github.pendzjo.jsonclassloader.uri.ClassLoaderURI;
import com.github.pendzjo.jsonclassloader.uri.ClassLoaderURISerializerDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author johnnp
 */
public class JsonClassLoaderConstants {

    public final static Gson gson = (new GsonBuilder()).setPrettyPrinting().
            registerTypeAdapter(ClassLoaderURI.class,
                    new ClassLoaderURISerializerDeserializer()).create();

}
