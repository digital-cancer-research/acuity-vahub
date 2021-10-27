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

package com.acuity.visualisations.rawdatamodel.vo;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.support.MultiValueFunction;
import com.googlecode.cqengine.query.QueryFactory;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@Getter
@RequiredArgsConstructor
public abstract class EntityAttribute<T> {


    private static final class SimpleEntityAttribute<T> extends EntityAttribute<T> {
        private SimpleEntityAttribute(String name, Function<T, ?> function) {
            super(name);
            this.function = function;
        }

        @NonNull
        private Function<T, ?> function;

        @Override
        protected Attribute<T, ?> getCqEngineAttrInst()  {
            return QueryFactory.nullableAttribute(getName(), (T t) -> function.apply(t));
        }

        @Override
        public Function<T, ?> getFunction() {
            return function;
        }
    }

    private static final class MultiValueEntityAttribute<O, A, I extends Iterable<A>> extends EntityAttribute<O> {
        private MultiValueEntityAttribute(Class<A> attributeType, String name, MultiValueFunction<O, A, I> function) {
            super(name);
            this.function = function;
            this.attributeType = attributeType;
        }

        @NonNull
        private MultiValueFunction<O, A, I> function;
        @NonNull
        private Class<A> attributeType;

        @Override
        protected Attribute<O, ?> getCqEngineAttrInst()  {
            return QueryFactory.nullableAttribute(attributeType, getName(), function);
        }

        @Override
        public Function<O, ?> getFunction() {
            return (Function<O, Object>) o -> function.apply(o);
        }
    }

    @NonNull
    private String name;

    @Getter(lazy = true)
    private final Attribute<T, ?> cqEngineAttr = getCqEngineAttrInst();

    public static <T> EntityAttribute<T> attribute(String name, Function<T, ?> function) {
        return new EntityAttribute.SimpleEntityAttribute<T>(name, function);
    }

    public static <O, A, I extends Iterable<A>> EntityAttribute<O> attribute(String name, MultiValueFunction<O, A, I> function, Class<A> attributeType) {
        return new MultiValueEntityAttribute<>(attributeType, name, function);
    }

    protected abstract Attribute<T, ?> getCqEngineAttrInst();
    public abstract Function<T, ?> getFunction();
}
