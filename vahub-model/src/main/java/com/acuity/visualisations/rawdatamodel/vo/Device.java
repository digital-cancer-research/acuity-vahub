package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 2)
public class Device implements HasStringId {
    private String id;
    private String name;
    private String version;
    private String type;

}
