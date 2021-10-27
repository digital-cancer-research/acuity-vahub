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

package com.acuity.visualisations.rawdatamodel.aspect;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * This is used to validate chart options passed to services
 *
 * @author knml167
 */
@Aspect
@Component
@Order(30)
@Slf4j
public class ValidateChartOptionsAspect {

    public ValidateChartOptionsAspect() {
        log.debug("ValidateChartOptionsAspect");
    }

    @Around(value = "@within(com.acuity.visualisations.rawdatamodel.aspect.ValidateChartOptions) || "
            + "@annotation(com.acuity.visualisations.rawdatamodel.aspect.ValidateChartOptions)")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        final ChartGroupBySetting[] requiredOptions = method.getAnnotation(ValidateChartOptions.class).required();
        final ChartGroupBySetting[] optionalOptions = method.getAnnotation(ValidateChartOptions.class).optional();

        for (Object arg : newArrayList(joinPoint.getArgs())) {
            ChartGroupByOptions<?, ?> chartGroupByOptions = null;

            if (arg instanceof ChartGroupByOptions) {
                chartGroupByOptions = ((ChartGroupByOptions) arg);
            }

            if (arg instanceof ChartGroupByOptionsFiltered) {
                chartGroupByOptions = ((ChartGroupByOptionsFiltered) arg).getSettings();
            }

            if (chartGroupByOptions != null) {
                validateRequiredOptions(chartGroupByOptions, Arrays.asList(requiredOptions));
                validateAllOptions(chartGroupByOptions, Stream.concat(
                        Stream.of(requiredOptions),
                        Stream.of(optionalOptions)
                ).collect(Collectors.toList()));
                break;
            }
        }

        return joinPoint.proceed();
    }

    private void validateAllOptions(ChartGroupByOptions<?, ?> options, Collection<ChartGroupBySetting> settings) {
        for (ChartGroupBySetting setting : options.getOptions().keySet()) {
            Validate.isTrue(settings.contains(setting), String.format("Unexpected option %s is provided", setting.name()));
        }
    }

    private void validateRequiredOptions(ChartGroupByOptions<?, ?> options, Collection<ChartGroupBySetting> settings) {
        for (ChartGroupBySetting setting : settings) {
            Validate.isTrue(options.getOptions().containsKey(setting), String.format("Required option %s is not provided", setting.name()));
            Validate.isTrue(options.getOptions().get(setting).getGroupByOption() == null
                    || options.getOptions().get(setting).getAttribute() != null, String.format("Required option %s is provided as null", setting.name()));
        }
    }


}
