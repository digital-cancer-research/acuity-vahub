/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Injectable} from '@angular/core';
import {isNil} from 'lodash';
import {fromJS, Map} from 'immutable';

import {
    AggregationType,
    ColorByLabels,
    FilterId,
    ScaleTypes,
    TabId,
    TabRecord,
    TabStore,
    TermLevelType,
    TherapiesType,
    TrellisDesign,
    XAxisOptions,
    YAxisType,
    ZOOM_MARGIN_CONSTRAINT
} from '../ITrellising';
import {PageName} from '../../../../plugins/timeline/store/ITimeline';

@Injectable()
export class TabStoreUtils {
    static buildInitialStore(): TabStore {
        return <TabStore> fromJS({
            height: 600,
            tabs: {
                CARDIAC_BOXPLOT: new TabRecord({
                    tabId: TabId.CARDIAC_BOXPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    colorByRequired: false,
                    trellisingRequired: true
                }),
                SINGLE_SUBJECT_CARDIAC_LINEPLOT: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    legendDisabled: true,
                    detailsOnDemandDisabled: true,
                    trellisingRequired: true
                }),
                AES_COUNTS_BARCHART: new TabRecord({
                    tabId: TabId.AES_COUNTS_BARCHART,
                    advancedMeasuredControlRequired: true,
                    xAxisTextZoomRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    trellisingRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                AES_OVER_TIME: new TabRecord({
                    tabId: TabId.AES_OVER_TIME,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    trellisingRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                AES_CHORD_DIAGRAM: new TabRecord({
                    tabId: TabId.AES_CHORD_DIAGRAM,
                    trellisDesign: TrellisDesign.NO_AXIS,
                    hasEventFilters: true,
                    zoomIsHidden: true,
                    zoomDisabled: true,
                    plotSettingsRequired: true,
                    colorByRequired: true,
                    isAllColoringOptionAvailable: false,
                    plotSettings: Map({
                        trellisedBy: TermLevelType.PT,
                        trellisOptions: Map([['timeFrame', 0], ['percentageOfLinks', 10]])
                    })
                }),
                CVOT_ENDPOINTS_COUNTS: new TabRecord({
                    tabId: TabId.CVOT_ENDPOINTS_COUNTS,
                    advancedMeasuredControlRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cvot',
                    legendSortingRequired: false,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CVOT_ENDPOINTS_OVER_TIME: new TabRecord({
                    tabId: TabId.CVOT_ENDPOINTS_OVER_TIME,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cvot',
                    legendSortingRequired: true,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CI_EVENT_COUNTS: new TabRecord({
                    tabId: TabId.CI_EVENT_COUNTS,
                    advancedMeasuredControlRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cievents',
                    legendSortingRequired: false,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CI_EVENT_OVERTIME: new TabRecord({
                    tabId: TabId.CI_EVENT_OVERTIME,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cievents',
                    legendSortingRequired: true,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CEREBROVASCULAR_COUNTS: new TabRecord({
                    tabId: TabId.CEREBROVASCULAR_COUNTS,
                    advancedMeasuredControlRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cerebrovascular',
                    legendSortingRequired: false,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CEREBROVASCULAR_EVENTS_OVER_TIME: new TabRecord({
                    tabId: TabId.CEREBROVASCULAR_EVENTS_OVER_TIME,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    jumpToAesFromAeNumberLocation: 'cerebrovascular',
                    legendSortingRequired: true,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                CONMEDS_BARCHART: new TabRecord({
                    tabId: TabId.CONMEDS_BARCHART,
                    xAxisTextZoomRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                LAB_BOXPLOT: new TabRecord({
                    tabId: TabId.LAB_BOXPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    colorByRequired: false,
                    trellisingRequired: true,
                    preserveTitle: true
                }),
                LAB_LINEPLOT: new TabRecord({
                    tabId: TabId.LAB_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    colorByRequired: true,
                    trellisingRequired: true,
                    preserveTitle: true
                }),
                EXACERBATIONS_OVER_TIME: new TabRecord({
                    tabId: TabId.EXACERBATIONS_OVER_TIME,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    legendSortingRequired: true
                }),
                EXACERBATIONS_GROUPED_COUNTS: new TabRecord({
                    tabId: TabId.EXACERBATIONS_GROUPED_COUNTS,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    advancedMeasuredControlRequired: true,
                    legendSortingRequired: true
                }),
                EXACERBATIONS_COUNTS: new TabRecord({
                    tabId: TabId.EXACERBATIONS_COUNTS,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                SINGLE_SUBJECT_LAB_LINEPLOT: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_LAB_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    detailsOnDemandDisabled: true,
                    trellisingRequired: true,
                    colorByRequired: true,
                    preserveTitle: true
                }),
                SINGLE_SUBJECT_VITALS_LINEPLOT: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_VITALS_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    trellisingRequired: true,
                    legendDisabled: true,
                    detailsOnDemandDisabled: true
                }),
                SINGLE_SUBJECT_LUNG_LINEPLOT: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_LUNG_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    legendDisabled: true,
                    detailsOnDemandDisabled: true,
                    trellisingRequired: true
                }),
                LUNG_FUNCTION_BOXPLOT: new TabRecord({
                    tabId: TabId.LUNG_FUNCTION_BOXPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    trellisingRequired: true
                }),
                RENAL_LABS_BOXPLOT: new TabRecord({
                    tabId: TabId.RENAL_LABS_BOXPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    trellisingRequired: true,
                    colorByRequired: false
                }),
                SINGLE_SUBJECT_RENAL_LINEPLOT: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_RENAL_LINEPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    trellisingRequired: true,
                    detailsOnDemandDisabled: true,
                    colorByRequired: true
                }),
                RENAL_CKD_BARCHART: new TabRecord({
                    tabId: TabId.RENAL_CKD_BARCHART,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    trellisingRequired: true,
                    colorByRequired: true
                }),
                VITALS_BOXPLOT: new TabRecord({
                    tabId: TabId.VITALS_BOXPLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    colorByRequired: false,
                    trellisingRequired: true
                }),
                LIVER_HYSLAW: new TabRecord({
                    tabId: TabId.LIVER_HYSLAW,
                    trellisDesign: TrellisDesign.VARIABLE_Y_CONST_X
                }),
                SINGLE_SUBJECT_LIVER_HYSLAW: new TabRecord({
                    tabId: TabId.SINGLE_SUBJECT_LIVER_HYSLAW,
                    trellisDesign: TrellisDesign.VARIABLE_Y_CONST_X,
                    legendDisabled: true,
                    detailsOnDemandDisabled: true
                }),
                LAB_SHIFTPLOT: new TabRecord({
                    tabId: TabId.LAB_SHIFTPLOT,
                    trellisDesign: TrellisDesign.VARIABLE_Y_VARIABLE_X,
                    colorByRequired: false,
                    trellisingRequired: true,
                    preserveTitle: true
                }),
                POPULATION_BARCHART: new TabRecord({
                    tabId: TabId.POPULATION_BARCHART,
                    columnLimit: 1,
                    eventDetailsOnDemandDisabled: true,
                    legendSortingRequired: true,
                    colorByRequired: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                POPULATION_TABLE: new TabRecord({
                    tabId: TabId.POPULATION_TABLE,
                    eventDetailsOnDemandDisabled: true,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES
                }),
                ANALYTE_CONCENTRATION: new TabRecord({
                    tabId: TabId.ANALYTE_CONCENTRATION,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    logarithmicScaleRequired: true,
                    availableScaleTypes: [ScaleTypes.LINEAR_SCALE, ScaleTypes.LOGARITHMIC_SCALE],
                    colorByRequired: true,
                    plotSettingsRequired: true,
                    trellisingRequired: true,
                    settingLabelRequired: true,
                    legendDisabled: false,
                    plotSettings: Map({
                        trellisOptions: [],
                        trellisedBy: AggregationType.SUBJECT_CYCLE,
                        errorBars: Map({STANDARD_DEVIATION: false})
                    }),
                    isAllColoringOptionAvailable: false,
                    colorByUpdateOnSettingsChangeRequired: true
                }),
                DOSE_PROPORTIONALITY_BOX_PLOT: new TabRecord({
                    tabId: TabId.DOSE_PROPORTIONALITY_BOX_PLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    logarithmicScaleRequired: true,
                    availableScaleTypes: [ScaleTypes.LINEAR_SCALE, ScaleTypes.LOGARITHMIC_SCALE],
                    trellisingRequired: true,
                    hasEventFilters: true,
                    advancedAxisControlRequired: true,
                    preserveTitle: true,
                    preserveGlobalTitle: true,
                    preserveYAxisIfItsNotPresent: true
                }),
                PK_RESULT_OVERALL_RESPONSE: new TabRecord({
                    tabId: TabId.PK_RESULT_OVERALL_RESPONSE,
                    trellisDesign: TrellisDesign.CATEGORICAL_OVER_TIME,
                    multiDetailsOnDemandRequired: true,
                    trellisingRequired: true,
                    logarithmicScaleRequired: true,
                    availableScaleTypes: [ScaleTypes.LINEAR_SCALE, ScaleTypes.LOGARITHMIC_SCALE],
                    plotSettingsRequired: true,
                    hasEventFilters: true,
                    advancedAxisControlRequired: true,
                    preserveTitle: true,
                    preserveGlobalTitle: true,
                    preserveYAxisIfItsNotPresent: true
                }),
                TL_DIAMETERS_PLOT: new TabRecord({
                    tabId: TabId.TL_DIAMETERS_PLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    hasEventFilters: false,
                }),
                BIOMARKERS_HEATMAP_PLOT: new TabRecord({
                    tabId: TabId.BIOMARKERS_HEATMAP_PLOT,
                    trellisDesign: TrellisDesign.VARIABLE_Y_VARIABLE_X,
                    zoomRangeRequired: true,
                    plotSettingsRequired: true,
                    cBioJumpRequired: true,
                    colorByRequired: true,
                    isAllColoringOptionAvailable: false,
                }),
                CTDNA_PLOT: new TabRecord({
                    tabId: TabId.CTDNA_PLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    multiDetailsOnDemandRequired: true,
                    plotSettingsRequired: true,
                    availableScaleTypes: [ScaleTypes.LINEAR_SCALE, ScaleTypes.LOGARITHMIC_SCALE],
                    scaleType: ScaleTypes.LOGARITHMIC_SCALE,
                    logarithmicScaleRequired: true,
                    isAllColoringOptionAvailable: false,
                    hasEventFilters: true,
                    colorByRequired: true
                }),
                TUMOUR_RESPONSE_PRIOR_THERAPY: new TabRecord({
                    tabId: TabId.TUMOUR_RESPONSE_PRIOR_THERAPY,
                    trellisDesign: TrellisDesign.CATEGORICAL_OVER_TIME,
                    isAllColoringOptionAvailable: true,
                    customControlLabels: [ColorByLabels.TIME_ON_COMPOUND, ColorByLabels.PRIOR_THERAPY, ColorByLabels.DATE_OF_DIAGNOSIS],
                    eventDetailsOnDemandDisabled: true,
                    colorByRequired: true,
                    legendSortingRequired: false,
                    hasEventFilters: true,
                    plotSettingsRequired: true,
                    plotSettings: Map({
                        trellisOptions: [],
                        trellisedBy: TherapiesType.MOST_RECENT_THERAPY
                    })
                }),
                TUMOUR_RESPONSE_WATERFALL_PLOT: new TabRecord({
                    tabId: TabId.TUMOUR_RESPONSE_WATERFALL_PLOT,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    detailsOnDemandDisabled: false,
                    eventDetailsOnDemandDisabled: true,
                    hasEventFilters: false,
                    colorByRequired: true,
                    filtersUpdateOnYAxisChangeRequired: true,
                    colorByUpdateOnSettingsChangeRequired: true,
                    isAllColoringOptionAvailable: false,
                    subPlotTabId: TabId.TL_DIAMETERS_PER_SUBJECT_PLOT,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
                TL_DIAMETERS_PER_SUBJECT_PLOT: new TabRecord({
                    tabId: TabId.TL_DIAMETERS_PER_SUBJECT_PLOT,
                    trellisDesign: TrellisDesign.CONTINUOUS_OVER_TIME,
                    hasEventFilters: false,
                    eventDetailsOnDemandDisabled: true,
                    detailsOnDemandDisabled: true,
                    parentSelectionPlot: TabId.TUMOUR_RESPONSE_WATERFALL_PLOT,
                    zoomIsHidden: true,
                    zoomDisabled: true
                }),
                QT_PROLONGATION: new TabRecord({
                    tabId: TabId.QT_PROLONGATION,
                    trellisDesign: TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES,
                    colorByRequired: true,
                    hasEventFilters: false,
                    isAllColoringOptionAvailable: false,
                    zoomMargin: ZOOM_MARGIN_CONSTRAINT
                }),
            }
        });
    }

    static paginationLimit(tabId: TabId): number {
        switch (tabId) {
            case TabId.POPULATION_BARCHART:
                return 1;
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
                return 2;
            default:
                return 4;
        }
    }

    static trellisDesign(tabId: TabId, xAxisValue: string, binSize: number): TrellisDesign {
        switch (tabId) {
            case TabId.CARDIAC_BOXPLOT:
            case TabId.VITALS_BOXPLOT:
            case TabId.RENAL_LABS_BOXPLOT:
            case TabId.LUNG_FUNCTION_BOXPLOT:
            case TabId.LAB_BOXPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
                switch (XAxisOptions[xAxisValue]) {
                    case XAxisOptions.STUDY_DEFINED_WEEK:
                    case XAxisOptions.WEEKS_SINCE_FIRST_TREATMENT:
                    case XAxisOptions.WEEKS_SINCE_RANDOMISATION:
                    case XAxisOptions.WEEKS_SINCE_FIRST_DOSE:
                    case XAxisOptions.WEEKS_SINCE_FIRST_DOSE_OF_DRUG:
                    case XAxisOptions.DAYS_SINCE_FIRST_TREATMENT:
                    case XAxisOptions.DAYS_SINCE_RANDOMISATION:
                    case XAxisOptions.DAYS_SINCE_FIRST_DOSE_OF_DRUG:
                    case XAxisOptions.DAYS_SINCE_FIRST_DOSE:
                        if (isNil(binSize) || binSize === 1) {
                            return TrellisDesign.CONTINUOUS_OVER_TIME;
                        } else {
                            return TrellisDesign.CATEGORICAL_OVER_TIME;
                        }
                    case XAxisOptions.VISIT_DESCRIPTION:
                    case XAxisOptions.VISIT_NUMBER:
                    case XAxisOptions.DATE:
                        return TrellisDesign.CATEGORICAL_OVER_TIME;
                    default:
                        return TrellisDesign.CONTINUOUS_OVER_TIME;
                }
            case TabId.CTDNA_PLOT:
                switch (XAxisOptions[xAxisValue]) {
                    case XAxisOptions.DAYS_SINCE_FIRST_DOSE:
                        return TrellisDesign.CONTINUOUS_OVER_TIME;
                    default:
                        return TrellisDesign.CATEGORICAL_OVER_TIME;
                }
            case TabId.LIVER_HYSLAW:
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
                return TrellisDesign.VARIABLE_Y_CONST_X;
            case TabId.EXACERBATIONS_OVER_TIME:
            case TabId.EXACERBATIONS_COUNTS:
            case TabId.CVOT_ENDPOINTS_COUNTS:
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
            case TabId.CI_EVENT_COUNTS:
            case TabId.CI_EVENT_OVERTIME:
            case TabId.AES_COUNTS_BARCHART:
            case TabId.CEREBROVASCULAR_COUNTS:
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
            case TabId.CONMEDS_BARCHART:
            case TabId.POPULATION_BARCHART:
            case TabId.POPULATION_TABLE:
            case TabId.AES_OVER_TIME:
            case TabId.RENAL_CKD_BARCHART:
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
            case TabId.QT_PROLONGATION:
                return TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES;
            case TabId.LAB_SHIFTPLOT:
            case TabId.BIOMARKERS_HEATMAP_PLOT:
                return TrellisDesign.VARIABLE_Y_VARIABLE_X;
            case TabId.TUMOUR_RESPONSE_PRIOR_THERAPY:
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return TrellisDesign.CATEGORICAL_OVER_TIME;
            case TabId.TL_DIAMETERS_PLOT:
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return XAxisOptions[xAxisValue] === XAxisOptions.ASSESSMENT_WEEK_WITH_BASELINE
                    ? TrellisDesign.CATEGORICAL_OVER_TIME : TrellisDesign.CONTINUOUS_OVER_TIME;
            case TabId.AES_CHORD_DIAGRAM:
                return TrellisDesign.NO_AXIS;
            default:
                return TrellisDesign.CONTINUOUS_OVER_TIME;
        }
    }

    static relatedSubTabs(tabId: TabId): TabId[] {
        switch (tabId) {
            case TabId.LAB_BOXPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.LAB_SHIFTPLOT:
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
                return [TabId.LAB_BOXPLOT, TabId.LAB_LINEPLOT, TabId.LAB_SHIFTPLOT, TabId.SINGLE_SUBJECT_LAB_LINEPLOT];
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
            case TabId.LIVER_HYSLAW:
                return [TabId.SINGLE_SUBJECT_LIVER_HYSLAW, TabId.LIVER_HYSLAW];
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            case TabId.VITALS_BOXPLOT:
                return [TabId.SINGLE_SUBJECT_VITALS_LINEPLOT, TabId.VITALS_BOXPLOT];
            case TabId.AES_OVER_TIME:
            case TabId.AES_COUNTS_BARCHART:
            case TabId.AES_CHORD_DIAGRAM:
                return [TabId.AES_OVER_TIME, TabId.AES_COUNTS_BARCHART, TabId.AES_CHORD_DIAGRAM];
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
            case TabId.CVOT_ENDPOINTS_COUNTS:
                return [TabId.CVOT_ENDPOINTS_OVER_TIME, TabId.CVOT_ENDPOINTS_COUNTS];
            case TabId.CI_EVENT_COUNTS:
            case TabId.CI_EVENT_OVERTIME:
                return [TabId.CI_EVENT_COUNTS, TabId.CI_EVENT_OVERTIME];
            case TabId.CEREBROVASCULAR_COUNTS:
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                return [TabId.CEREBROVASCULAR_COUNTS, TabId.CEREBROVASCULAR_EVENTS_OVER_TIME];
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            case TabId.CARDIAC_BOXPLOT:
                return [TabId.CARDIAC_BOXPLOT, TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT];
            case TabId.EXACERBATIONS_COUNTS:
            case TabId.EXACERBATIONS_OVER_TIME:
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
                return [TabId.EXACERBATIONS_COUNTS, TabId.EXACERBATIONS_GROUPED_COUNTS, TabId.EXACERBATIONS_OVER_TIME];
            case TabId.RENAL_LABS_BOXPLOT:
            case TabId.RENAL_CKD_BARCHART:
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                return [TabId.RENAL_LABS_BOXPLOT, TabId.RENAL_CKD_BARCHART, TabId.SINGLE_SUBJECT_RENAL_LINEPLOT];
            case TabId.LUNG_FUNCTION_BOXPLOT:
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
                return [TabId.LUNG_FUNCTION_BOXPLOT, TabId.SINGLE_SUBJECT_LUNG_LINEPLOT];
            case TabId.ANALYTE_CONCENTRATION:
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return [TabId.ANALYTE_CONCENTRATION, TabId.DOSE_PROPORTIONALITY_BOX_PLOT, TabId.PK_RESULT_OVERALL_RESPONSE];
            case TabId.BIOMARKERS_HEATMAP_PLOT:
            case TabId.CTDNA_PLOT:
                return [TabId.BIOMARKERS_HEATMAP_PLOT, TabId.CTDNA_PLOT];
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
            case TabId.TL_DIAMETERS_PLOT:
                return [TabId.TUMOUR_RESPONSE_WATERFALL_PLOT, TabId.TL_DIAMETERS_PLOT];
            default:
                return [tabId];
        }
    }

    static tabsRelatedToFilter(filterId: FilterId): TabId[] {
        switch (filterId) {
            case FilterId.AES:
                return [TabId.AES_COUNTS_BARCHART, TabId.AES_OVER_TIME, TabId.AES_TABLE, TabId.AES_CHORD_DIAGRAM];
            case FilterId.CIEVENTS:
                return [TabId.CI_EVENT_COUNTS, TabId.CI_EVENT_OVERTIME];
            case FilterId.CEREBROVASCULAR:
                return [TabId.CEREBROVASCULAR_COUNTS];
            case FilterId.CONMEDS:
                return [TabId.CONMEDS_BARCHART];
            case FilterId.CVOT:
                return [TabId.CVOT_ENDPOINTS_COUNTS, TabId.CVOT_ENDPOINTS_OVER_TIME];
            case FilterId.VITALS:
                return [TabId.VITALS_BOXPLOT, TabId.SINGLE_SUBJECT_VITALS_LINEPLOT];
            case FilterId.LAB:
                return [TabId.LAB_BOXPLOT, TabId.LAB_LINEPLOT, TabId.LAB_SHIFTPLOT, TabId.SINGLE_SUBJECT_LAB_LINEPLOT];
            case FilterId.LUNG_FUNCTION:
                return [TabId.LUNG_FUNCTION_BOXPLOT, TabId.SINGLE_SUBJECT_LUNG_LINEPLOT];
            case FilterId.EXACERBATIONS:
                return [TabId.EXACERBATIONS_OVER_TIME, TabId.EXACERBATIONS_COUNTS, TabId.EXACERBATIONS_GROUPED_COUNTS];
            case FilterId.RENAL:
                return [TabId.RENAL_LABS_BOXPLOT, TabId.RENAL_CKD_BARCHART, TabId.SINGLE_SUBJECT_RENAL_LINEPLOT];
            case FilterId.CARDIAC:
                return [TabId.CARDIAC_BOXPLOT, TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT];
            case FilterId.POPULATION:
                return [TabId.POPULATION_BARCHART, TabId.POPULATION_TABLE];
            case FilterId.LIVER:
                return [TabId.SINGLE_SUBJECT_LIVER_HYSLAW, TabId.LIVER_HYSLAW];
            case FilterId.BIOMARKERS:
                return [TabId.BIOMARKERS_HEATMAP_PLOT];
            case FilterId.CTDNA:
                return [TabId.CTDNA_PLOT];
            case FilterId.RECIST:
                return [TabId.TUMOUR_RESPONSE_WATERFALL_PLOT];
            case FilterId.TUMOUR_RESPONSE:
                return [TabId.TUMOUR_RESPONSE_PRIOR_THERAPY];
            case FilterId.SINGLE_SUBJECT:
                return [
                    TabId.SINGLE_SUBJECT_LIVER_HYSLAW,
                    TabId.SINGLE_SUBJECT_LAB_LINEPLOT,
                    TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT,
                    TabId.SINGLE_SUBJECT_VITALS_LINEPLOT,
                    TabId.SINGLE_SUBJECT_RENAL_LINEPLOT,
                    TabId.SINGLE_SUBJECT_LUNG_LINEPLOT
                ];
            default:
                return [];
        }
    }

    static getPageNameFromTabId(tabId: TabId): any {
        switch (tabId) {
            case TabId.AES_COUNTS_BARCHART:
            case TabId.AES_TABLE:
            case TabId.AES_OVER_TIME:
            case TabId.AE_SUMMARIES:
                return PageName.AES;
            case TabId.AES_CHORD_DIAGRAM:
                return PageName.LINKED_AES;
            case TabId.CI_EVENT_COUNTS:
            case TabId.CI_EVENT_OVERTIME:
                return PageName.CIEVENTS;
            case TabId.CEREBROVASCULAR_COUNTS:
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                return PageName.CEREBROVASCULAR;
            case TabId.CVOT_ENDPOINTS_COUNTS:
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
                return PageName.CVOT;
            case TabId.CONMEDS_BARCHART:
                return PageName.CONMEDS;
            case TabId.VITALS_BOXPLOT:
                return PageName.VITALS;
            case TabId.LAB_BOXPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.LAB_SHIFTPLOT:
                return PageName.LABS;
            case TabId.LIVER_HYSLAW:
                return PageName.LIVER;
            case TabId.EXACERBATIONS_OVER_TIME:
            case TabId.EXACERBATIONS_COUNTS:
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
                return PageName.EXACERBATIONS;
            case TabId.LUNG_FUNCTION_BOXPLOT:
                return PageName.LUNG_FUNCTION;
            case TabId.RENAL_LABS_BOXPLOT:
            case TabId.RENAL_CKD_BARCHART:
                return PageName.RENAL;
            case TabId.CARDIAC_BOXPLOT:
                return PageName.CARDIAC;
            case TabId.BIOMARKERS_HEATMAP_PLOT:
            case TabId.CTDNA_PLOT:
                return PageName.GENOMIC_PROFILE;
            case TabId.TL_DIAMETERS_PLOT:
                return PageName.TL_DIAMETERS;
            case TabId.ANALYTE_CONCENTRATION:
                return PageName.DOSING_EXPOSURE;
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
                return PageName.DOSE_PROPORTIONALITY;
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return PageName.PK_PARAMETERS_RESPONSE;
            case TabId.QT_PROLONGATION:
                return PageName.QT_PROLONGATION;
            default:
                return '';
        }
    }

    static isBoxPlotTab(tabId: TabId): boolean {
        return [TabId.LAB_BOXPLOT,
                TabId.VITALS_BOXPLOT,
                TabId.CARDIAC_BOXPLOT,
                TabId.RENAL_LABS_BOXPLOT,
                TabId.DOSE_PROPORTIONALITY_BOX_PLOT,
                TabId.PK_RESULT_OVERALL_RESPONSE,
                TabId.LUNG_FUNCTION_BOXPLOT].indexOf(tabId.toString()) !== -1;
    }

    static isRequiredToSetDefaultColorByOnOptionChange(tabId: TabId): boolean {
        switch (tabId) {
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                return true;
            default:
                return false;
        }
    }

    static transformedYAxisForColorBy(chartId: TabId, colorBy: string) {
        switch (chartId) {
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                if (colorBy.toLowerCase().includes('week')) {
                    return YAxisType.ASSESSMENT_RESPONSE;
                } else {
                    return YAxisType.BEST_RESPONSE;
                }
            default:
                return colorBy;
        }
    }
}
