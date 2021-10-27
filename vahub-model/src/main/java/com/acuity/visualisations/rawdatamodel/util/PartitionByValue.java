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
public class PartitionByValue<T, V> {
    
    private T key;
    private V value;
}
