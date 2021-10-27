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

import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * QueryFactory does have a builder pattern and only allows a collection from
 * 
 * QueryFactory.and(q1, q1, otherQueries);
 * 
 * So this is a class to create a builder pattern for adding queries in a 'and' statement
 */
public class CombinedQueryBuilder<T> {

    private List<Query<T>> allQueries = newArrayList();
    private Class clazz;

    public CombinedQueryBuilder(Class clazz) {
        this.clazz = clazz;
    }

    public CombinedQueryBuilder<T> add(Query<T> query) {
        if (query != null) {
            allQueries.add(query);
        }

        return this;
    }

    public Query<T> build() {
        if (allQueries.isEmpty()) {
            return QueryFactory.all(clazz); //returns all the List of applied QueryFactory.none(clazz) returns nothing
        }

        if (allQueries.size() == 1) {
            return allQueries.remove(0);
        }

        if (allQueries.size() == 2) {
            return QueryFactory.and(allQueries.get(0), allQueries.get(1));
        }

        // 3 or more
        Query<T> first = allQueries.remove(0);
        Query<T> second = allQueries.remove(0);

        return QueryFactory.and(first, second, allQueries);
    }
}
