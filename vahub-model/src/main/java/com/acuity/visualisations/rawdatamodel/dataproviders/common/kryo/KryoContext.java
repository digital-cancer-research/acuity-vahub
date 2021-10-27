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
