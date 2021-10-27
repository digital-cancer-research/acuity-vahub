package com.acuity.visualisations.rawdatamodel.aspect;

import com.acuity.visualisations.rawdatamodel.filters.UsedInTflFilters;
import com.acuity.va.security.acl.domain.Datasets;
import static com.google.common.collect.Lists.newArrayList;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * This is used to apply addUsedInTflFilter on detect methods
 *
 * @author ksnd199
 */
@Aspect
@Component
@Order(20) // after caching at 10.
@Slf4j
public class UsedInTflAspect {

    public UsedInTflAspect() {
        log.debug("UsedInTflAspect");
    }

    @Around(value = "@within(com.acuity.visualisations.rawdatamodel.aspect.ApplyUsedInTflFilter) || "
            + "@annotation(com.acuity.visualisations.rawdatamodel.aspect.ApplyUsedInTflFilter)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {

        UsedInTflFilters usedInTflFilters = null;
        Datasets datasets = null;

        for (Object arg : newArrayList(joinPoint.getArgs())) {

            if (arg instanceof UsedInTflFilters) {
                usedInTflFilters = ((UsedInTflFilters) arg);
            }
            if (arg instanceof Datasets) {
                datasets = ((Datasets) arg);
            }
        }

        if (datasets != null && usedInTflFilters != null && datasets.isDetectType()) {
            
            String classAndMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();
            log.info("Applying addUsedInTflFilter to {} for {} in {}", usedInTflFilters.getClass().getSimpleName(), datasets, classAndMethod);
            
            usedInTflFilters.addUsedInTflFilter();
        }

        return joinPoint.proceed();
    }
}
