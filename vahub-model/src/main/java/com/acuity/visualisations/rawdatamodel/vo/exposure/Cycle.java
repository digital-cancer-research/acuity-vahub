package com.acuity.visualisations.rawdatamodel.vo.exposure;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class Cycle implements Serializable {
    private String treatmentCycle;
    private String analyte;
    private Integer visit;
    private Date drugAdministrationDate;
    private Boolean isNotAllDrugDatesEmpty;

    @Getter(lazy = true)
    private final String asString = isNotAllDrugDatesEmpty
            ? String.format("%s, %s, drug administration date %s", treatmentCycle, analyte,
            drugAdministrationDate == null ? null : DaysUtil.toString(drugAdministrationDate))
            : String.format("%s, %s, visit %d", treatmentCycle, analyte, visit);

    @Override
    public String toString() {
        return getAsString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof Cycle)) {
            return false;
        }
        Cycle other = (Cycle) o;

        return (isNotAllDrugDatesEmpty ? Objects.equals(this.drugAdministrationDate, other.drugAdministrationDate)
                : Objects.equals(this.visit, other.visit))
                && Objects.equals(this.treatmentCycle, other.treatmentCycle)
                && Objects.equals(this.analyte, other.analyte);
    }

    @Override
    public int hashCode() {
        final int prime = 59;
        final int nullArg = 43;
        int result = 1;
        if (isNotAllDrugDatesEmpty) {
            result = result * prime + (drugAdministrationDate == null ? nullArg : drugAdministrationDate.hashCode());
        } else {
            result = result * prime + (visit == null ? nullArg : visit.hashCode());
        }
        result = result * prime + (treatmentCycle == null ? nullArg : treatmentCycle.hashCode());
        result = result * prime + (analyte == null ? nullArg : analyte.hashCode());
        return result;
    }
}
