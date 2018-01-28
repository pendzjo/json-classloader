//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader.uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 *
 * @author johnnp
 */
public class ClassLoaderURISerializerDeserializer implements
        JsonDeserializer<ClassLoaderURI>, JsonSerializer<ClassLoaderURI> {

    @Override
    public ClassLoaderURI deserialize(JsonElement json, Type type,
            JsonDeserializationContext jdc) throws JsonParseException {

        ClassLoaderURI classLoaderURI = null;

        String uri = json.getAsString();
        try {
            if (uri != null && !String.valueOf(uri).equals("null")) {
                classLoaderURI = new ClassLoaderURI(uri);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return classLoaderURI;
    }

    @Override
    public JsonElement serialize(ClassLoaderURI t, Type type,
            JsonSerializationContext jsc) {
        JsonElement j = JsonNull.INSTANCE;
        if (t != null) {
            j = new JsonPrimitive(String.valueOf(t.getURI()));
        }
        return j;
    }

}
