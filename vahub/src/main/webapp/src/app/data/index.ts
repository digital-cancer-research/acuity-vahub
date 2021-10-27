import {
    CardiacBoxPlotHttpService,
    CardiacHttpService,
    CardiacSingleSubjectRangePlotHttpService
} from './cardiac/index';

import {
    LabsBoxPlotHttpService,
    LabsHttpService,
    LabsRangePlotHttpService,
    LabsShiftPlotHttpService,
    LabsSingleSubjectRangePlotHttpService
} from './labs/index';

import {
    AesBarChartHttpService,
    AesBarLineChartHttpService,
    AesChordHttpService,
    AesHttpService,
    AesTableHttpService
} from './aes/index';

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

import {
    TumourRespPriorTherapyHttpService,
    TumourRespTLDiameterHttpService,
    TumourRespTLDPerSubjectHttpService,
    TumourRespWaterfallHttpService
} from './tumourresponse/index';

import {
    CerebrovascularBarChartHttpService,
    CerebrovascularBarLineChartHttpService,
    CerebrovascularHttpService
} from './cerebrovascular/index';

import {CvotBarLineChartHttpService, CvotGroupedBarChartHttpService, CvotHttpService} from './cvot/index';
import {CIEventsBarLineChartHttpService} from './cievents/CIEventsBarLineChartHttpService';
import {QtProlongationBarChartHttpService} from './machineinsights/QtProlongationBarChartHttpService';

export {ConmedsHttpService} from './conmeds/ConmedsHttpService';
export {ConmedsBarChartHttpService} from './conmeds/ConmedsBarChartHttpService';
export {PopulationHttpService} from './population/PopulationHttpService';
export {ExposureHttpService} from './exposure/ExposureHttpService';
export {DoseProportionalityHttpService} from './exposure/DoseProportionalityHttpService';
export {PkOverallResponseHttpService} from './exposure/PkOverallResponseHttpService';
export {BiomarkersHttpService} from './biomarkers/BiomarkersHttpService';
export {CtDnaHttpService} from './biomarkers/CtDnaHttpService';


export const labsServices = [
    LabsHttpService,
    LabsShiftPlotHttpService,
    LabsBoxPlotHttpService,
    LabsRangePlotHttpService,
    LabsSingleSubjectRangePlotHttpService
];

export const machineInsightsServices = [
  QtProlongationBarChartHttpService
];

export const liverServices = [
    LiverHttpService,
    LiverScatterPlotHttpService,
    LiverSingleSubjectScatterPlotHttpService
];

export const aesServices = [
    AesBarChartHttpService,
    AesHttpService,
    AesBarLineChartHttpService,
    AesTableHttpService,
    AesChordHttpService
];

export const cardiacServices = [
    CardiacBoxPlotHttpService,
    CardiacSingleSubjectRangePlotHttpService,
    CardiacHttpService
];

export const exacerbationsServices = [
    ExacerbationsHttpService,
    ExacerbationsLineChartHttpService,
    ExacerbationsGroupedBarChartHttpService,
    ExacerbationsBarLineChartHttpService
];

export const renalServices = [
    RenalBoxPlotHttpService,
    RenalHttpService,
    RenalBarChartHttpService,
    RenalSingleSubjectRangePlotHttpService
];

export const respiratoryServices = [
    RespiratoryHttpService,
    RespiratoryBoxPlotHttpService,
    RespiratorySingleSubjectRangePlotHttpService
];

export const vitalsServices = [
    VitalsHttpService,
    VitalsBoxPlotHttpService,
    VitalsSingleSubjectRangePlotHttpService
];

export const cieventsServices = [
    CIEventsBarLineChartHttpService,
    CIEventsBarChartHttpService,
    CIEventsHttpService
];

export const tumorRespServices = [
    TumourRespWaterfallHttpService,
    TumourRespTLDiameterHttpService,
    TumourRespTLDPerSubjectHttpService,
    TumourRespPriorTherapyHttpService
];

export const cveServices = [
    CerebrovascularHttpService,
    CerebrovascularBarChartHttpService,
    CerebrovascularBarLineChartHttpService
];

export const cvotServices = [
    CvotHttpService,
    CvotGroupedBarChartHttpService,
    CvotBarLineChartHttpService
];
