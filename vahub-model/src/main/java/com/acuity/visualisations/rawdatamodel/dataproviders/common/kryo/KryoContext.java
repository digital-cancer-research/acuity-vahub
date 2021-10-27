package com.acuity.visualisations.rawdatamodel.dataproviders.common.kryo;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderException;
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
            throw new DataProviderException(e);
        } finally {
            synchronized (pool) {
                pool.release(kryo);
            }
        }
    }
}
