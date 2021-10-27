package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.common.cache.DatasetsKey;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.SneakyThrows;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * To be used instead of the default spring KeyGenerator SimpleKeyGenerator.
 *
 * @author ksnd199
 */
@Service
class DataProviderKeyGenerator implements KeyGenerator {

    @Override
    @SneakyThrows
    public Object generate(Object target, Method method, Object... params) {
        Datasets datasets = null;
        Class clazz = null;
        Integer version = null;
        final Annotation[][] parameterAnnotations;
        parameterAnnotations = (DataProvider.class).getDeclaredMethod("getData", Class.class, Dataset.class, Function.class).getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation a : parameterAnnotations[i]) {
                if (a instanceof Param) {
                    switch (((Param) a).value()) {
                        case DATASET:
                            datasets = new Datasets((Dataset) params[i]);
                            break;
                        case CLASS:
                            clazz = (Class) params[i];
                            version = DataProvider.getEntityClassVersion(clazz);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        return new DatasetsKey(datasets, datasets, clazz, version);
    }

    public enum Params {
        DATASET, CLASS
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    public @interface Param {
        Params value();
    }


}
