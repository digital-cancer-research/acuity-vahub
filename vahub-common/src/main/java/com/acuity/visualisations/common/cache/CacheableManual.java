package com.acuity.visualisations.common.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the method to resolve cached information manually.
 *
 * See {@link RefreshableCacheResolver} and {@link com.acuity.visualisations.common.filter.AAutoFilterRepositoryCached} for usage examples.
 *
 * @author adavliatov.
 * @since 20.02.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheableManual {
}
