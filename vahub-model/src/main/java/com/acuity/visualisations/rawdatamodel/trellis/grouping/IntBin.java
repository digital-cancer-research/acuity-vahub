package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.Validate;

@Getter
@EqualsAndHashCode(callSuper = false, of = {"start", "end"})
public final class IntBin extends Bin<Integer> {


    private Integer start;
    private Integer end;


    private IntBin(@NonNull Integer start, @NonNull Integer end) {
        this.start = start;
        this.end = end;
    }

    static IntBin newInstance(Integer value, Integer binSize) {
        if (binSize == null || binSize == 1) {
            return new IntBin(value, value);
        }
        Validate.isTrue(binSize != 0, "Bin size must not be 0");

        return new IntBin(
                (int) (Math.floor(value * 1.0 / binSize) * binSize),
                (int) (Math.floor(value * 1.0 / binSize) * binSize + binSize - 1));
    }

    @Override
    @JsonIgnore
    protected String getOneArgString() {
        return getEnd().toString();
    }

    @Override
    @JsonIgnore
    protected String getTwoArgsString() {
        return String.format("%d - %d", getStart(), getEnd());
    }

    @Override
    @JsonIgnore
    public Bin<Integer> getNextBin() {
        return new IntBin(getEnd() + 1, getEnd() + getSize());
    }

    @Override
    @JsonIgnore
    public int getSize() {
        return getEnd() - getStart() + 1;
    }
}
