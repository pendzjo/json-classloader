//"Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements; and to You under the Apache License, Version 2.0. "
package com.github.pendzjo.jsonclassloader.uri;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author johnnp
 */
public class ClassLoaderURI {

    private final String schema;

    private final URI uri;

    private Path basePath = null;

    public ClassLoaderURI(URI u) {
        this.uri = u;

        String str = u.toString();
        if (str.endsWith("/")) {
            this.schema = "dir";
        } else {
            this.schema = str.substring(str.lastIndexOf(".") + 1);
        }
    }

    public String getSchema() {
        return this.schema;
    }

    public ClassLoaderURI(String str) throws URISyntaxException {
        this(new URI(str));
    }

    public Path getBasePath() {
        return this.basePath;
    }

    public URI getURI() {
        if (this.basePath != null && ("jar".equals(this.schema) ||
                "json".equals(this.schema))) {

            Path uriPath = Paths.get(this.basePath.toString(),
                    this.uri.getPath());
            return uriPath.normalize().toUri();
        }
        return this.uri;
    }

    public URL toURL() throws MalformedURLException {
        return getURI().toURL();
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.schema, this.uri);
    }

    public void setBasePath(Path fileLocation) {
        this.basePath = fileLocation;
    }
}
