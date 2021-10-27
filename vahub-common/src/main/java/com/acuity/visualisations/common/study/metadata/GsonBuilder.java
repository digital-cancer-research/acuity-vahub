package com.acuity.visualisations.common.study.metadata;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;

/**
 *
 * @author ksnd199
 */
public final class GsonBuilder {

    private GsonBuilder() {
    }
    
    public static final Gson GSON = new com.google.gson.GsonBuilder().
        setPrettyPrinting().
        serializeNulls().
        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).
        create();
}
