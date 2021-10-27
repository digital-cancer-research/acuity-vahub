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

import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.StopWatch;

/**
 * This adds the execution time to the raw sql from mybatis repositories
 *
 * @author ksnd199
 */
@Intercepts({
    @Signature(
            type = Executor.class,
            method = "query",
            args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class TimeMePlugin extends TimeMeLog implements Interceptor {

    private static final int MAPPED_STATEMENT_INDEX = 0;
    private static final int PARAMETER_INDEX = 1;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object[] invocationArgs = invocation.getArgs();
        MappedStatement ms = (MappedStatement) invocationArgs[MAPPED_STATEMENT_INDEX];
        Object queryObject = (Object) invocationArgs[PARAMETER_INDEX];

        List<Object> queryArgs = newArrayList();
        if (queryObject instanceof Map) {
            queryArgs = newArrayList(((Map) queryObject).values());
        } else if (queryObject instanceof List) {
            queryArgs = (List) queryObject;
        } else {
            queryArgs = newArrayList(queryObject);
        }
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = invocation.proceed();

        logExecutionTime(ms.getId(), stopWatch, queryArgs);

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // default implementation ignored
    }
}
