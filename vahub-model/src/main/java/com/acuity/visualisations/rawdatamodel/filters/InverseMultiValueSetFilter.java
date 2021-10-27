package com.acuity.visualisations.rawdatamodel.filters;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

/**
 * Type of SetFilter, that filters objects not by specific field's single value, but by field that contains list of values. For example, if subject has an
 * associated list of visit numbers, this filter allows retrieve subjects based on which visits they have attended. E.g., filtering to just visit 7, 8 and 9
 * would include all subjects who attended any of thee specified visits.
 *
 * This is the same as MultiValueSetFilter but its the inverse of the subjects that would be for MultiValueSetFilter, as for study specific filters for detect,
 * if a subject a as ssf 1 and 2 and subject b has 1 InverseMultiValueSetFilter = 2 would return subject 1, all subjects that dont have 2 in its list.
 *
 */
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public final class InverseMultiValueSetFilter<T extends Comparable<T>> extends MultiValueSetFilter<T> {

    public InverseMultiValueSetFilter(Collection<T> values) {
        super(values);
    }
}
