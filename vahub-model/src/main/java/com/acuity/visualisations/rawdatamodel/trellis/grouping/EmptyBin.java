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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;

public final class EmptyBin extends Bin {

    private EmptyBin() {
    }

    protected static EmptyBin newBin() {
        return new EmptyBin();
    }

    @Override
    public Comparable getStart() {
        return null;
    }

    @Override
    public Comparable getEnd() {
        return null;
    }

    @Override
    protected String getOneArgString() {
        return DEFAULT_EMPTY_VALUE;
    }

    @Override
    protected String getTwoArgsString() {
        return DEFAULT_EMPTY_VALUE;
    }

    @Override
    public Bin getNextBin() {
        return this;
    }

    @Override
    public int getSize() {
        return 1;
    }
}
