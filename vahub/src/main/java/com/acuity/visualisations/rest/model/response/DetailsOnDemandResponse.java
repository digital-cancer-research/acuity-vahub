package com.acuity.visualisations.rest.model.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailsOnDemandResponse implements Serializable {
    private List<Map<String, String>> dodData;
}
