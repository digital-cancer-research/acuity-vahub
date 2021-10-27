package com.acuity.visualisations.rawdatamodel.filters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MultiValueIntRangeSetFilter extends MultiValueSetFilter<Integer> {

    public MultiValueIntRangeSetFilter(Collection<Integer> values) {
        super(values);
        setFromAndTo();
    }

    public MultiValueIntRangeSetFilter(Collection<Integer> values, Boolean includeEmptyValues) {
        super(values);
        setFromAndTo();
        setIncludeEmptyValues(includeEmptyValues);
    }

    @JsonCreator
    public MultiValueIntRangeSetFilter(@JsonProperty("from") Integer from, @JsonProperty("to") Integer to) {
        this.from = from;
        this.to = to;

        setGeneratedValues(from, to);
        setIncludeEmptyValues(false);
    }

    private void setFromAndTo() {
        if (CollectionUtils.isNotEmpty(getValues())) {
            List<Integer> sortedValues = getSortedValues();
            sortedValues.remove(null);
            if (!sortedValues.isEmpty()) {
                from = sortedValues.get(0);
                to = sortedValues.get(sortedValues.size() - 1);
            }
        }
    }

    private void setGeneratedValues(Integer from, Integer to) {
        if (CollectionUtils.isEmpty(getValues()) && from != null && to != null) {
            getValues().addAll(IntStream.rangeClosed(from, to).boxed().collect(Collectors.toList()));
        }
    }

    @Getter
    private Integer from;

    @Getter
    private Integer to;

    @JsonIgnore
    @Override
    public List<Integer> getSortedValues() {
        return super.getSortedValues();
    }

    @JsonIgnore
    @Override
    public void setSortedValues(List<Integer> sorted) {
        super.setSortedValues(sorted);
    }

    @Override
    public void completeWithValues(Collection<Integer> values) {
        super.completeWithValues(values);
        setFromAndTo();
    }
}
