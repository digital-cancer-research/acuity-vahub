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
