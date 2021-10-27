/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.util;

import lombok.experimental.UtilityClass;
import one.util.streamex.StreamEx;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@UtilityClass
public final class ClassScanner {

    public static List<Class<?>> getAllClassesFromPackage(String packageName)
            throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        return StreamEx.<URL>produce(action -> {
            boolean hasNext = resources.hasMoreElements();
            if (hasNext) {
                action.accept(resources.nextElement());
            }
            return hasNext;
        })
                .map(URL::getFile)
                .map(File::new)
                .flatMap(file -> findClasses(file, packageName).stream())
                .toList();
    }

    private static List<Class<?>> findClasses(File directory, String packageName) {
        List<Class<?>> classes = new LinkedList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : Objects.requireNonNull(files)) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                appendLoadedClassFromFileInPackage(classes, file, packageName);
            }
        }
        return classes;
    }

    private static void appendLoadedClassFromFileInPackage(List<Class<?>> classes, File file, String packageName) {
        try {
            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
