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

package com.acuity.visualisations.common.aspect;

import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.google.common.collect.Range;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import static java.util.stream.Collectors.toList;

/**
 * Util for logging 
 * 
 * @author ksnd199
 */
@Slf4j
public abstract class TimeMeLog {
    private static final int MAX_TIME_BEFORE_LOG_ARGS = 2000; // 2 secs
    private static final int MAX_COLLECTION_SIZE_TO_LOG = 100;
    private static final String LOG_MESSAGE_FORMAT = "%s%s execution time: %dms (Time range: %s)";
    private static final String LOG_SLOW_MESSAGE_FORMAT = "%s%s slow execution time: %dms (Time range: %s), args %s";

    private static final Range<Long> RANGE0_2 = Range.closedOpen(0L, 2000L);
    private static final Range<Long> RANGE2_3 = Range.closedOpen(2000L, 3000L);
    private static final Range<Long> RANGE3_5 = Range.closedOpen(3000L, 5000L);
    private static final Range<Long> RANGE5_10 = Range.closedOpen(5000L, 10000L);
    private static final Range<Long> RANGE10_PLUS = Range.closedOpen(10000L, Long.MAX_VALUE);

    protected void logExecutionTime(String classAndMethod, StopWatch stopWatch, List<Object> queryArgs) {
        stopWatch.stop();
        long executionTime = stopWatch.getTotalTimeMillis();

        Optional<Datasets> datasetsOptional = findDatasets(queryArgs);
        String extraInfo = "";
        if (datasetsOptional.isPresent()) {
            Datasets datasets = datasetsOptional.get();
            extraInfo = "(" + datasets.getShortNameByType() + ":" + datasets.getIdsAsString() + ")";
        }

        String range = findTimeRange(executionTime);

        if (stopWatch.getTotalTimeMillis() < MAX_TIME_BEFORE_LOG_ARGS) {
            String logMessage = String.format(LOG_MESSAGE_FORMAT, classAndMethod, extraInfo, executionTime, range);
            log.debug(logMessage);
        } else {
            List<?> truncatedQueryArgs = truncateLargeCollections(queryArgs);

            String logSlowMessage = String.format(LOG_SLOW_MESSAGE_FORMAT, classAndMethod, extraInfo, executionTime, range, truncatedQueryArgs);
            log.warn(logSlowMessage);
        }
    }

    protected String findTimeRange(long totalTime) {

        if (RANGE0_2.contains(totalTime)) {
            return "0-2 secs";
        }
        if (RANGE2_3.contains(totalTime)) {
            return "2-3 secs";
        }
        if (RANGE3_5.contains(totalTime)) {
            return "3-5 secs";
        }
        if (RANGE5_10.contains(totalTime)) {
            return "5-10 secs";
        }
        if (RANGE10_PLUS.contains(totalTime)) {
            return "10+ secs";
        }

        return "10+ secs";
    }

    protected Optional<Datasets> findDatasets(List<Object> queryArgs) {
        for (Object arg : queryArgs) {
            if (arg != null) {
                if (arg instanceof DatasetsRequest) {
                    return Optional.of(((DatasetsRequest) arg).getDatasetsObject());
                }

                if (arg instanceof Datasets) {
                    return Optional.of(((Datasets) arg));
                }

                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private List<?> truncateLargeCollections(List<Object> queryArgs) {
        return queryArgs.stream()
                .map(arg -> {
                    if (arg instanceof Collection) {
                        return ((Collection<?>) arg).stream().limit(MAX_COLLECTION_SIZE_TO_LOG).collect(toList());
                    } else {
                        return arg;
                    }
                })
                .collect(toList());
    }
}
