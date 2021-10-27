package com.acuity.visualisations.rawdatamodel.trellis;

public enum TrellisCategories {
    MANDATORY_HIGHER_LEVEL, MANDATORY_TRELLIS, NON_MANDATORY_TRELLIS, NON_MANDATORY_SERIES;

    public boolean is(String name) {
        return this.name().equals(name);
    }
}
