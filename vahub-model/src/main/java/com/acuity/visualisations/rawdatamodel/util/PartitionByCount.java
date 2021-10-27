package com.acuity.visualisations.rawdatamodel.util;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author ksnd199
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class PartitionByCount<T> {
    
    private T key;
    private Long count;
}
