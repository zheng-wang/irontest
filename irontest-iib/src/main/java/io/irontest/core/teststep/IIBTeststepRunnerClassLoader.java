package io.irontest.core.teststep;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class IIBTeststepRunnerClassLoader extends URLClassLoader {
    private ClassLoader parentClassLoader;

    protected IIBTeststepRunnerClassLoader(URL[] urls, ClassLoader parentClassLoader) {
        super(urls, null);
        this.parentClassLoader = parentClassLoader;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        //  check whether the class has already been loaded by this class loader
        Class c = findLoadedClass(name);

        if (c == null) {
            try {
                //  use this class loader's classpath (URLs above) to search the class
                c = findClass(name);
            } catch(ClassNotFoundException e) {
                //  do nothing
            }
        }

        //  the class is not in this class loader's classpath
        if (c == null) {
            if (name.startsWith("io.irontest.core.teststep.IIB")) {
                //  use parent class loader to get the class bytes, but use this class loader to load it
                InputStream in = null;
                try {
                    in = parentClassLoader.getResourceAsStream(name.replaceAll("\\.", "/") + ".class");
                    if (in == null) {
                        throw new ClassNotFoundException("Parent class loader can not find class bytes for " + name);
                    }

                    byte[] cBytes = IOUtils.toByteArray(in);
                    c = defineClass(name, cBytes, 0, cBytes.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException("Failed to load class " + name, e);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            } else {
                // use parent class loader to load the class
                c = parentClassLoader.loadClass(name);
            }
        }

        if (resolve) {
            resolveClass(c);
        }

        return c;
    }
}