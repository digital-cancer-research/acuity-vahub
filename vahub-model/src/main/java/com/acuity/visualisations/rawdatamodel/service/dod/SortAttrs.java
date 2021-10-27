package com.acuity.visualisations.rawdatamodel.service.dod;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortAttrs implements Serializable {

    private String sortBy;
    /**
     * false corresponds to ASC, true -- to DESC
     */
    private boolean reversed;

}
