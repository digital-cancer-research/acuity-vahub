package com.acuity.visualisations.common.cache.kryo;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.pool.KryoPool;
import java.util.function.Function;

public class KryoContext {

    private final KryoPool pool;

    public KryoContext(KryoPool pool) {
        this.pool = pool;
    }

    public <T> T borrow(Function<Kryo, T> consumer) {
        Kryo kryo;

        synchronized (pool) {
            kryo = pool.borrow();
        }

        try {
            return consumer.apply(kryo);
        } catch (Exception e) {
            throw new KryoContextException(e);
        } finally {
            synchronized (pool) {
                pool.release(kryo);
            }
        }
    }
}
