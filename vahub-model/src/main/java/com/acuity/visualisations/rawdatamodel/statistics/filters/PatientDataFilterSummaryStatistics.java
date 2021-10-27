package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;

public class PatientDataFilterSummaryStatistics implements FilterSummaryStatistics<PatientData> {

    private PatientDataFilters patientDataFilters = new PatientDataFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        PatientData exposure = (PatientData) value;

        patientDataFilters.getMeasurementName().completeWithValue(exposure.getEvent().getMeasurementName());
        patientDataFilters.getValue().completeWithValue(exposure.getEvent().getValue());
        patientDataFilters.getUnit().completeWithValue(exposure.getEvent().getUnit());
        patientDataFilters.getMeasurementDate().completeWithValue(exposure.getEvent().getMeasurementDate());
        patientDataFilters.getReportDate().completeWithValue(exposure.getEvent().getReportDate());
        patientDataFilters.getSourceType().completeWithValue(exposure.getEvent().getSourceType());
        patientDataFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<PatientData> other) {

        PatientDataFilterSummaryStatistics otherExposure = (PatientDataFilterSummaryStatistics) other;
        count += otherExposure.count;

        patientDataFilters.getMeasurementName().complete(otherExposure.patientDataFilters.getMeasurementName());
        patientDataFilters.getValue().complete(otherExposure.patientDataFilters.getValue());
        patientDataFilters.getUnit().complete(otherExposure.patientDataFilters.getUnit());
        patientDataFilters.getMeasurementDate().complete(otherExposure.patientDataFilters.getMeasurementDate());
        patientDataFilters.getReportDate().complete(otherExposure.patientDataFilters.getReportDate());
        patientDataFilters.getSourceType().complete(otherExposure.patientDataFilters.getSourceType());
        patientDataFilters.setMatchedItemsCount(count);
    }

    @Override
    public final PatientDataFilters getFilters() {
        return patientDataFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PatientDataFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<PatientData> newStatistics() {
        return new PatientDataFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{patientDataFilters=%s}",
                this.getClass().getSimpleName(),
                this.patientDataFilters.toString());
    }
}
