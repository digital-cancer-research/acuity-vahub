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
import {
    BiomarkersHttpService,
    DoseProportionalityHttpService,
    ExposureHttpService,
    PkOverallResponseHttpService,
    PopulationHttpService,
} from './index';

import {
    LabsBoxPlotHttpService,
    LabsHttpService,
    LabsRangePlotHttpService,
    LabsShiftPlotHttpService,
    LabsSingleSubjectRangePlotHttpService
} from './labs/index';

import {AesBarChartHttpService, AesBarLineChartHttpService, AesChordHttpService, AesHttpService} from './aes/index';

import {CerebrovascularBarChartHttpService, CerebrovascularBarLineChartHttpService} from './cerebrovascular/index';

import {CardiacBoxPlotHttpService, CardiacHttpService, CardiacSingleSubjectRangePlotHttpService} from './cardiac/index';

import {
    ExacerbationsBarLineChartHttpService,
    ExacerbationsGroupedBarChartHttpService,
    ExacerbationsHttpService,
    ExacerbationsLineChartHttpService
} from './exacerbations/index';

import {LiverHttpService, LiverScatterPlotHttpService, LiverSingleSubjectScatterPlotHttpService} from './liver/index';

import {
    RenalBarChartHttpService,
    RenalBoxPlotHttpService,
    RenalHttpService,
    RenalSingleSubjectRangePlotHttpService
} from './renal/index';

import {
    RespiratoryBoxPlotHttpService,
    RespiratoryHttpService,
    RespiratorySingleSubjectRangePlotHttpService
} from './respiratory/index';

import {VitalsBoxPlotHttpService, VitalsHttpService, VitalsSingleSubjectRangePlotHttpService} from './vitals/index';

import {CIEventsBarChartHttpService, CIEventsHttpService} from './cievents/index';

import {FilterId, TabId} from '../common/trellising/store/ITrellising';
import {BaseChartsHttpService} from './BaseChartsHttpService';
import {CvotBarLineChartHttpService, CvotGroupedBarChartHttpService, CvotHttpService} from './cvot/index';

import {
    TumourRespPriorTherapyHttpService,
    TumourRespTLDiameterHttpService,
    TumourRespWaterfallHttpService
} from './tumourresponse/index';
import {IFiltersServices} from './IFiltersServices';
import {CerebrovascularHttpService} from './cerebrovascular/CerebrovascularHttpService';
import {CIEventsBarLineChartHttpService} from './cievents/CIEventsBarLineChartHttpService';
import {TumourRespTLDPerSubjectHttpService} from './tumourresponse/TumourRespTLDPerSubjectHttpService';
import {CtDnaHttpService} from './biomarkers/CtDnaHttpService';
import {QtProlongationBarChartHttpService} from './machineinsights/QtProlongationBarChartHttpService';
import {ConmedsBarChartHttpService} from './conmeds/ConmedsBarChartHttpService';

@Injectable()
export class HttpServiceFactory {
    constructor(private cardiacBoxPlotHttpService: CardiacBoxPlotHttpService,
                private cardiacSingleSubjectRangePlotHttpService: CardiacSingleSubjectRangePlotHttpService,
                private cardiacHttpService: CardiacHttpService,
                private labs: LabsHttpService,
                private labsShiftPlotHttpService: LabsShiftPlotHttpService,
                private labsBoxPlotHttpService: LabsBoxPlotHttpService,
                private labsRangePlotHttpService: LabsRangePlotHttpService,
                private labsSingleSubjectRangePlotHttpService: LabsSingleSubjectRangePlotHttpService,
                private lungFunction: RespiratoryHttpService,
                private respiratoryBoxPlotHttpService: RespiratoryBoxPlotHttpService,
                private respiratorySingleSubjectRangePlotHttpService: RespiratorySingleSubjectRangePlotHttpService,
                private exacerbations: ExacerbationsHttpService,
                private exacerbationsLineChartHttpService: ExacerbationsLineChartHttpService,
                private exacerbationsGroupedBarChartHttpService: ExacerbationsGroupedBarChartHttpService,
                private exacerbationsBarLineChartHttpService: ExacerbationsBarLineChartHttpService,
                private renal: RenalHttpService,
                private renalBoxPlotHttpService: RenalBoxPlotHttpService,
                private renalBarChartHttpService: RenalBarChartHttpService,
                private renalSingleSubjectRangePlotHttpService: RenalSingleSubjectRangePlotHttpService,
                private exposure: ExposureHttpService,
                private doseProportionalityHttpService: DoseProportionalityHttpService,
                private exposureOverallResponseService: PkOverallResponseHttpService,
                private aes: AesHttpService,
                private aesBarChartHttpService: AesBarChartHttpService,
                private aesBarLineChartHttpService: AesBarLineChartHttpService,
                private aesChordHttpService: AesChordHttpService,
                private ciEventsHttpService: CIEventsHttpService,
                private cieventsBarChartHttpService: CIEventsBarChartHttpService,
                private conmeds: ConmedsBarChartHttpService,
                private vitals: VitalsHttpService,
                private vitalsBoxPlotHttpService: VitalsBoxPlotHttpService,
                private vitalsSingleSubjectRangePlotHttpService: VitalsSingleSubjectRangePlotHttpService,
                private liver: LiverHttpService,
                private liverScatterPlotHttpService: LiverScatterPlotHttpService,
                private liverSingleSubjectScatterPlotHttpService: LiverSingleSubjectScatterPlotHttpService,
                private population: PopulationHttpService,
                private biomarkers: BiomarkersHttpService,
                private ctDnaHttpService: CtDnaHttpService,
                private cerebrovascular: CerebrovascularHttpService,
                private cerebrovascularBarChartHttpService: CerebrovascularBarChartHttpService,
                private tlDiametersHttpService: TumourRespTLDiameterHttpService,
                private tlDPerSubjectHttpService: TumourRespTLDPerSubjectHttpService,
                private tumourRespPriorTherapyHttpService: TumourRespPriorTherapyHttpService,
                private tumourRespWaterfallService: TumourRespWaterfallHttpService,
                private cvotHttpService: CvotHttpService,
                private cvotGroupedBarChartHttpService: CvotGroupedBarChartHttpService,
                private cvotBarLineChartHttpService: CvotBarLineChartHttpService,
                private cieventsBarLineChartHttpService: CIEventsBarLineChartHttpService,
                private cveOverTimeBarChartHttpService: CerebrovascularBarLineChartHttpService,
                private qtProlongationBarChartHttpService: QtProlongationBarChartHttpService) {
    }


    public getHttpService(tabId: TabId): BaseChartsHttpService {
        switch (tabId) {
            case TabId.CARDIAC_BOXPLOT:
                return this.cardiacBoxPlotHttpService;
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
                return this.cardiacSingleSubjectRangePlotHttpService;
            case TabId.LAB_BOXPLOT:
                return this.labsBoxPlotHttpService;
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
                return this.respiratorySingleSubjectRangePlotHttpService;
            case TabId.LUNG_FUNCTION_BOXPLOT:
                return this.respiratoryBoxPlotHttpService;
            case TabId.EXACERBATIONS_OVER_TIME:
                return this.exacerbationsBarLineChartHttpService;
            case TabId.EXACERBATIONS_COUNTS:
                return this.exacerbationsLineChartHttpService;
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
                return this.exacerbationsGroupedBarChartHttpService;
            case TabId.RENAL_LABS_BOXPLOT:
                return this.renalBoxPlotHttpService;
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                return this.renalSingleSubjectRangePlotHttpService;
            case TabId.RENAL_CKD_BARCHART:
                return this.renalBarChartHttpService;
            case TabId.AES_COUNTS_BARCHART:
                return this.aesBarChartHttpService;
            case TabId.CI_EVENT_COUNTS:
                return this.cieventsBarChartHttpService;
            case TabId.CI_EVENT_OVERTIME:
                return this.cieventsBarLineChartHttpService;
            case TabId.CONMEDS_BARCHART:
                return this.conmeds;
            case TabId.VITALS_BOXPLOT:
                return this.vitalsBoxPlotHttpService;
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
                return this.vitalsSingleSubjectRangePlotHttpService;
            case TabId.LAB_LINEPLOT:
                return this.labsRangePlotHttpService;
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
                return this.labsSingleSubjectRangePlotHttpService;
            case TabId.LIVER_HYSLAW:
                return this.liverScatterPlotHttpService;
            case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
                return this.liverSingleSubjectScatterPlotHttpService;
            case TabId.LAB_SHIFTPLOT:
                return this.labsShiftPlotHttpService;
            case TabId.POPULATION_BARCHART:
            case TabId.POPULATION_TABLE:
                return this.population;
            case TabId.AES_OVER_TIME:
                return this.aesBarLineChartHttpService;
            case TabId.AES_CHORD_DIAGRAM:
                return this.aesChordHttpService;
            case TabId.ANALYTE_CONCENTRATION:
                return this.exposure;
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
                return this.doseProportionalityHttpService;
            case TabId.PK_RESULT_OVERALL_RESPONSE:
                return this.exposureOverallResponseService;
            case TabId.BIOMARKERS_HEATMAP_PLOT:
                return this.biomarkers;
            case TabId.CTDNA_PLOT:
                return this.ctDnaHttpService;
            case TabId.CVOT_ENDPOINTS_COUNTS:
                return this.cvotGroupedBarChartHttpService;
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
                return this.cvotBarLineChartHttpService;
            case TabId.TL_DIAMETERS_PLOT:
                return this.tlDiametersHttpService;
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return this.tlDPerSubjectHttpService;
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                return this.tumourRespWaterfallService;
            case TabId.TUMOUR_RESPONSE_PRIOR_THERAPY:
                return this.tumourRespPriorTherapyHttpService;
            case TabId.CEREBROVASCULAR_COUNTS:
                return this.cerebrovascularBarChartHttpService;
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                return this.cveOverTimeBarChartHttpService;
            case TabId.QT_PROLONGATION:
                return this.qtProlongationBarChartHttpService;
            case '':
                console.warn('HTTP service is requested for empty tab');
                return;
            default:
                console.error('HTTP service is not defined for tab' + tabId);
                return;
        }
    }

    public getHttpServiceForFilter(filterId: FilterId): IFiltersServices {
        switch (filterId) {
            case FilterId.CARDIAC:
                return this.cardiacHttpService;
            case FilterId.LAB:
                return this.labs;
            case FilterId.LUNG_FUNCTION:
                return this.lungFunction;
            case FilterId.EXACERBATIONS:
                return this.exacerbations;
            case FilterId.RENAL:
                return this.renal;
            case FilterId.AES:
                return this.aes;
            case FilterId.VITALS:
                return this.vitals;
            case FilterId.LIVER:
                return this.liver;
            case FilterId.CONMEDS:
                return this.conmeds;
            case FilterId.RECIST:
                return this.tumourRespWaterfallService;
            case FilterId.TUMOUR_RESPONSE:
                return this.tumourRespPriorTherapyHttpService;
            case FilterId.POPULATION:
                return this.population;
            case FilterId.CIEVENTS:
                return this.ciEventsHttpService;
            case FilterId.CEREBROVASCULAR:
                return this.cerebrovascular;
            case FilterId.CVOT:
                return this.cvotHttpService;
            case FilterId.CTDNA:
                return this.ctDnaHttpService;
            case FilterId.BIOMARKERS:
                return this.biomarkers;
            case FilterId.EXPOSURE:
                return this.exposure;
            case FilterId.DOSE_PROPORTIONALITY:
                return this.doseProportionalityHttpService;
            case FilterId.PK_RESULT_OVERALL_RESPONSE:
                return this.exposureOverallResponseService;
            case '':
                console.warn('HTTP service is requested for filter id');
                return;
            default:
                console.error('HTTP service is not defined for filter' + filterId);
                return;
        }
    }
}
