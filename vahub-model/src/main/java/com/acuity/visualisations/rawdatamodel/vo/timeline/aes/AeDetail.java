package com.acuity.visualisations.rawdatamodel.vo.timeline.aes;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(exclude = {"events"})
public class AeDetail implements Serializable {
    private String pt;
    private String soc;
    private String hlt;

    private List<AeDetailEvent> events;
}
