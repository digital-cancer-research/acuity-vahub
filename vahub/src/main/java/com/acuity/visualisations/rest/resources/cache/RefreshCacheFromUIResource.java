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

package com.acuity.visualisations.rest.resources.cache;

import com.acuity.visualisations.common.cache.RefreshCacheService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/resources/cache/", consumes = {APPLICATION_JSON_VALUE, ALL_VALUE}, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class RefreshCacheFromUIResource {

    @Autowired
    private RefreshCacheService cacheService;

    @ApiOperation(
            value = "Clear all caches",
            nickname = "clearAllCache",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/clear/all", method = GET)
    public ResponseEntity clearAllCache() {

        Callable fn = () -> cacheService.clearAllCaches();
        return cacheService.tryLock(fn);
    }
}
