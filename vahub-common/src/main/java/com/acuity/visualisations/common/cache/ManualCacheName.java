package com.acuity.visualisations.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the field in {@link CacheableManual} to represent cache name.
 *
 * @author adavliatov.
 * @since 20.02.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ManualCacheName {
}
