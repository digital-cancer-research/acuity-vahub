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
