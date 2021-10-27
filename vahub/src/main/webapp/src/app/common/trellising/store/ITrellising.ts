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

import {List, Map, Record} from 'immutable';

import {GroupByOptions} from './actions/TrellisingActionCreator';
import TrellisedScatterPlot = InMemory.TrellisedScatterPlot;
import OutputBarChartData = Request.OutputBarChartData;
import OutputOvertimeData = InMemory.OutputOvertimeData;
import OutputOvertimeLineChartData = InMemory.OutputOvertimeLineChartData;
import OutputRangeChartEntry = Request.OutputRangeChartEntry;
import OutputBoxplotEntry = Request.OutputBoxplotEntry;
import OutputHeatMapData = InMemory.OutputHeatMapData;

export const MAX_NUMBER = 1000000000000000;

// Number used in calculating plot ranges to show values close to plot edges
// because max and min are both based on the plot data
export const RANGE_DELTA = 1.1;
export const MINIMUM_ZOOM_PERCENTAGE = 0.001;

// Biomarkers constants used to limit the display number of values by x and y
// because it doesn't make sense do display more than specified
export const BIOMARKERS_X_MAX = 100;
export const BIOMARKERS_Y_MAX = 50;

export const MAX_BARS_ALL_PRIOR_THERAPIES = 35;

export const MODIFIED_MUTATION_COLOR = '#00FF00';
export const NO_DATA_PLACEHOLDER = 'N/A';
export const EMPTY = '(Empty)';
export const NO_AVAILABLE_PARAMETERS = 'No available parameters';
export const DEFAULT_PERCENTAGE_CHORD_WIDTH = 10;
export const ZOOM_STEP = 0.1;
export const ZOOM_MARGIN_CONSTRAINT = 6;

// Fields in interfaces, which extend Map<string, any>, are only markers. Some components have OnPush change detection
// strategy, so these interfaces should be immutable. Use get and set methods from immutable map.
// plotSettings.get('trellisedBy') and plotSettings.trellisedBy are different methods.
export interface IZoom extends Map<string, any> {
    absMin: number;
    absMax: number;
    zoomMin: number;
    zoomMax: number;
}

export interface ZoomRanges {
    x: {
        min: number;
        max: number;
    };
    y: {
        min: number;
        max: number;
    };
}


export interface NoData {
    tabId: TabId;
    noData: boolean;
}

export interface DetailsOnDemand {
    tabId: TabId;
    detailsOnDemand: any;
}

export interface IXAxis {
    tabId: TabId;
    option?: DynamicAxis;
    options?: List<DynamicAxis>;
    zoom?: IZoom;
    xAxis: boolean;
}

export interface IYAxis {
    tabId: TabId;
    option?: string;
    options?: List<string>;
    zoom?: IZoom;
    yAxis: boolean;
}

export interface ITrellisingOptions {
    tabId: TabId;
    trellising: List<ITrellises>;
    onGoing?: boolean;
}

export interface PlotSettings extends Map<string, any> {
    trellisOptions: Map<string, any>;
    trellisedBy: string;
    errorBars: Map<string, boolean>;
}

export interface PlotSettingsWithTab {
    tabId: TabId;
    plotSettings: PlotSettings;
}

// export interface BasePlotSettings {
//     tabId: TabId;
//     basePlotSettings: List<ITrellises>;
// }

export interface IPlots {
    tabId: TabId;
    plots: List<IPlot>;
}

export interface IDetail {
    tabId: TabId;
    detail: ISelectionDetail | IMultiSelectionDetail;
}

export const ZoomRecord = Record({
    absMax: 0,
    absMin: 0,
    zoomMax: 0,
    zoomMin: 0
});

export enum AeDetailLevel {
    PER_INCIDENCE = 'PER_INCIDENCE',
    PER_SEVERITY_CHANGE = 'PER_SEVERITY_CHANGE'
}

export enum BiomarkerSettings {
    NUMBER_OF_SUBJECTS_SHOWN_IN_ZOOM = 'NUMBER_OF_SUBJECTS_SHOWN_IN_ZOOM',
    NUMBER_OF_GENES_SHOWN_IN_ZOOM = 'NUMBER_OF_GENES_SHOWN_IN_ZOOM',
}

export enum ExposureSettings {
    LINE_AGGREGATION = 'LINE_AGGREGATION',
    SCALE = 'SCALE',
    ERROR_BARS = 'ERROR_BARS'
}

export enum AesChordSettings {
    TERM_LEVEL = 'TERM_LEVEL',
    DAYS_CONSTITUTE_LINK = 'DAYS_CONSTITUTE_LINK',
    NUMBER_OF_VISIBLE_LINKS = 'NUMBER_OF_VISIBLE_LINKS'
}

export interface YAxis extends Map<string, any> {
    zoom: IZoom;
    option: string;
    options: List<string>;
}

export interface XAxis extends Map<string, any> {
    zoom: IZoom;
    option: DynamicAxis;
    options: List<DynamicAxis>;
}

export enum TimeScaleOptions {
    VISIT_NUMBER = 'VISIT_NUMBER',
    STUDY_DEFINED_WEEK = 'STUDY_DEFINED_WEEK',
    WEEKS_SINCE = 'WEEKS_SINCE',
    DAYS_SINCE = 'DAYS_SINCE'
}

export enum UnitOptions {
    COUNT = 'COUNT',
    PERCENT_OF_TOTAL = 'PERCENT_OF_TOTAL',
    PERCENT_WITHIN_PLOT = 'PERCENT_WITHIN_PLOT'
}

export enum MeasureNumberOptions {
    SUBJECTS = 'SUBJECTS',
    EVENTS = 'EVENTS'
}

export type TableType = 'events' | 'subjects';

export enum AEsYAxisOptions {
    COUNT_OF_SUBJECTS = 'COUNT_OF_SUBJECTS',
    PERCENTAGE_OF_ALL_SUBJECTS = 'PERCENTAGE_OF_ALL_SUBJECTS',
    PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT = 'PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT',
    COUNT_OF_EVENTS = 'COUNT_OF_EVENTS',
    PERCENTAGE_OF_ALL_EVENTS = 'PERCENTAGE_OF_ALL_EVENTS',
    PERCENTAGE_OF_EVENTS_WITHIN_PLOT = 'PERCENTAGE_OF_EVENTS_WITHIN_PLOT'
}

export enum XAxisOptions {
    VISIT_NUMBER = 'VISIT_NUMBER',
    VISIT_DESCRIPTION = 'VISIT_DESCRIPTION',
    ASSESSMENT_WEEK_WITH_BASELINE = 'ASSESSMENT_WEEK_WITH_BASELINE',
    DATE = 'DATE',
    STUDY_DEFINED_WEEK = 'STUDY_DEFINED_WEEK',
    WEEKS_SINCE_FIRST_TREATMENT = 'WEEKS_SINCE_FIRST_TREATMENT',
    WEEKS_SINCE_RANDOMISATION = 'WEEKS_SINCE_RANDOMISATION',
    WEEKS_SINCE_FIRST_DOSE = 'WEEKS_SINCE_FIRST_DOSE',
    WEEKS_SINCE_FIRST_DOSE_OF_DRUG = 'WEEKS_SINCE_FIRST_DOSE_OF_DRUG',
    DAYS_SINCE_FIRST_TREATMENT = 'DAYS_SINCE_FIRST_TREATMENT',
    DAYS_SINCE_RANDOMISATION = 'DAYS_SINCE_RANDOMISATION',
    DAYS_SINCE_FIRST_DOSE_OF_DRUG = 'DAYS_SINCE_FIRST_DOSE_OF_DRUG',
    DAYS_SINCE_FIRST_DOSE = 'DAYS_SINCE_FIRST_DOSE'
}

export enum YAxisOptions {
    ABSOLUTE_CHANGE_FROM_BASELINE = 'ABSOLUTE_CHANGE_FROM_BASELINE',
    PERCENTAGE_CHANGE_FROM_BASELINE = 'PERCENTAGE_CHANGE_FROM_BASELINE',
    TIMES_LOWER_REF_VALUE = 'TIMES_LOWER_REF_VALUE',
    TIMES_UPPER_REF_VALUE = 'TIMES_UPPER_REF_VALUE',
    REF_RANGE_NORM_VALUE = 'REF_RANGE_NORM_VALUE',
    ACTUAL_VALUE = 'ACTUAL_VALUE',
    RESULT_VALUE = 'RESULT_VALUE',
    COUNT_INCLUDING_DURATION = 'COUNT_INCLUDING_DURATION',
    COUNT_START_DATES_ONLY = 'COUNT_START_DATES_ONLY'
}

export interface DynamicAxis extends Map<string, any> {
    value: string;
    intarg: number;
    stringarg: string;
}

export const DynamicAxisRecord = Record({
    value: null,
    intarg: null,
    stringarg: null
});

export const AxisRecord = Record({
    zoom: ZoomRecord,
    option: null,
    options: List<string>()
});

export interface IPage extends Map<string, any> {
    limit: number;
    offset: number;
    value: number;
}

export const PageRecord = Record({
    limit: 1,
    offset: 1,
    value: 1
});

export interface ITrellises extends Map<string, any> {
    category: TrellisCategory;
    trellisOptions: List<string>;
    trellisedBy: string;
}

export const TrellisesRecord = Record({
    trellisOptions: List<string>(),
    trellisedBy: null,
    category: null
});

export interface ITrellis extends Map<string, any> {
    category: TrellisCategory;
    trellisOption: string;
    trellisedBy: string;
}

export const TrellisRecord = Record({
    trellisOption: null,
    trellisedBy: null,
    category: null
});

export interface IDetailsOnDemand extends Map<string, any> {
    summary: string;
    tableData: any[];
    expandedGroups: any[];
    isOpen: boolean;
    isExpanded: null;
}

export const DetailsOnDemandRecord = Record({
    summary: null,
    tableData: null,
    expandedGroups: null,
    isOpen: null,
    isExpanded: null
});

export class TrellisDesign {
    static CONTINUOUS_OVER_TIME = 'CONTINUOUS_OVER_TIME';
    static CATEGORICAL_COUNTS_AND_PERCENTAGES = 'CATEGORICAL_COUNTS_AND_PERCENTAGES';
    static CATEGORICAL_OVER_TIME = 'CATEGORICAL_OVER_TIME';
    static VARIABLE_Y_CONST_X = 'VARIABLE_Y_CONST_X';
    static VARIABLE_Y_VARIABLE_X = 'VARIABLE_Y_VARIABLE_X';
    static CATEGORICAL_Y_CATEGORICAL_X = 'CATEGORICAL_Y_CATEGORICAL_X';
    static NO_AXIS = 'NO_AXIS';
}

export enum TrellisCategory {
    MANDATORY_HIGHER_LEVEL = 'MANDATORY_HIGHER_LEVEL',
    MANDATORY_TRELLIS = 'MANDATORY_TRELLIS',
    NON_MANDATORY_TRELLIS = 'NON_MANDATORY_TRELLIS',
    NON_MANDATORY_SERIES = 'NON_MANDATORY_SERIES'
}

export enum AggregationType {
    SUBJECT_CYCLE = 'SUBJECT_CYCLE',
    SUBJECT = 'SUBJECT',
    ANALYTE = 'ANALYTE',
    DOSE = 'DOSE',
    VISIT = 'VISIT',
    CYCLE = 'CYCLE',
    DOSE_PER_VISIT = 'DOSE_PER_VISIT',
    DOSE_PER_CYCLE = 'DOSE_PER_CYCLE'
}

export enum ErrorBarsType {
    STANDARD_DEVIATION = 'STANDARD_DEVIATION'
}

export enum TherapiesType {
    MOST_RECENT_THERAPY = 'MOST_RECENT_THERAPY',
    ALL_PRIOR_THERAPIES = 'ALL_PRIOR_THERAPIES'
}

export enum TermLevelType {
    PT = 'PT',
    HLT = 'HLT',
    SOC = 'SOC'
}

export enum AnalyteTrellises {
    CYCLE = 'CYCLE',
    SUBJECT = 'SUBJECT',
    ANALYTE = 'ANALYTE',
    DOSE = 'DOSE',
    VISIT = 'VISIT',
    DOSE_PER_VISIT = 'DOSE_PER_VISIT',
    DOSE_PER_CYCLE = 'DOSE_PER_CYCLE',
    DAY = 'DAY',
    NONE = 'NONE'
}

export enum PlotType {
    BOXPLOT = 'BOXPLOT',
    LINECHART = 'LINECHART',
    STACKED_BARCHART = 'STACKED_BARCHART',
    RANGEPLOT = 'RANGEPLOT',
    JOINEDRANGEPLOT = 'JOINEDRANGEPLOT',
    SCATTERPLOT = 'SCATTERPLOT',
    ERRORPLOT = 'ERRORPLOT',
    BARLINECHART = 'BARLINECHART',
    GROUPED_BARCHART = 'GROUPED_BARCHART',
    SIMPLE_LINEPLOT = 'SIMPLE_LINEPLOT',
    HEATMAP = 'HEATMAP',
    WATERFALL = 'WATERFALL',
    COLUMNRANGE = 'COLUMNRANGE',
    CHORD = 'CHORD'
}

export enum ModalAnswer {
    CANCEL = 'CANCEL',
    YES = 'YES',
    NO = 'NO'
}

export enum ColorByLabels {
    DATE_OF_DIAGNOSIS = 'EVENT',
    TIME_ON_COMPOUND = 'TIME_ON_COMPOUND',
    PRIOR_THERAPY = 'PRIOR_THERAPY'
}

export interface IModalAnswer extends Map<string, any> {
    answer: ModalAnswer;
    doNotShowAgain: boolean;
}

export interface IModalMessage {
    msg: string;
    sessionStorageVarName: string;
    isVisible: boolean;
}

// TODO in fact, this interface doesn't limit almost anything. please fix it.
export interface IPlot extends Map<string, any> {
    plotType: PlotType;
    trellising: List<ITrellis>;
    series: List<ITrellises>;
    data: IBoxPlot | IBarChart | IRangePlot | IScatterPlot | IErrorPlot | IBarLineChart | ILineChart | IHeatMap;
}

export interface IHeatMap extends Map<string, any> {
    data: OutputHeatMapData;
}

export const PlotRecord = Record({
    plotType: null,
    trellising: List<ITrellis>(),
    series: List<ITrellises>(),
    data: null
});

export interface ISelection extends Map<string, any> {
    selections: List<IChartSelection>;
    detail: ISelectionDetail;
}

export interface ISelectionRange extends Map<string, any> {
    trellising: List<ITrellis>;
    range: List<IContinuousSelection>;
}

export interface SelectedBar extends Map<string, any> {
    category: string;
    series: string;
}

export interface SelectedColumn extends Map<string, any> {
    start: any;
    end: any;
    series?: string;
    category?: string;
    x?: string;
}

export interface IChartSelection extends Map<string, any> {
    trellising: Array<ITrellis>;
    series: Array<ITrellises>[];
    range: Array<IContinuousSelection>[];
    bars: Array<SelectedBar>[];
    columns?: Array<SelectedColumn>;
}

export const SelectionDetailRecord = Record({
    eventIds: null,
    subjectIds: null,
    totalSubjects: null,
    totalEvents: null
});

export const SelectionRecord = Record({
    selections: List<IChartSelection>(),
    detail: new SelectionDetailRecord()
});

export interface IContinuousSelection {
    xMin: number;
    xMax: number;
    yMin: number;
    yMax: number;
}

export interface ICategoricalSelection {
    categories: string[];
    yMin: number;
    yMax: number;
}

export interface ISelectionDetail {
    eventIds: any[];
    subjectIds: string[];
    totalSubjects: number;
    totalEvents: number;
    eventCount?: number;
}

export interface IMultiSelectionDetail {
    eventIds: Map<string, string[]>;
    subjectIds: string[];
    totalSubjects: number;
    totalEvents: Map<string, number>;
}

export interface IBoxPlot extends Map<string, any> {
    data?: Array<OutputBoxplotEntry>;
    categories?: Array<number>;
}

export interface IBarLineChart extends Map<string, any> {
    data?: OutputOvertimeData;
}

export interface ILineChart extends Map<string, any> {
    data?: Array<OutputOvertimeLineChartData>;
}

export interface IBarChart extends Map<string, any> {
    data?: Array<OutputBarChartData>;
}

export interface IScatterPlot extends Map<string, any> {
    data?: TrellisedScatterPlot<any, any>;
}

export interface IErrorPlot extends Map<string, any> {
    data?: InMemory.ShiftPlotData;
}

export interface IRangePlot extends Map<string, any> {
    data?: Array<OutputRangeChartEntry>;
}

export const BoxPlotRecord = Record({
    data: null,
    categories: null
});

export interface ILegend {
    title: string;
    entries: ILegendEntry[];
}

export interface ILegendEntry {
    label: string;
    symbol: LegendSymbol;
    color: string;
    className?: string;
}

export interface ITab extends Map<string, any> {
    isInitialised: boolean;
    noData: boolean;
    advancedMeasuredControlRequired: boolean;
    trellisingRequired: boolean;
    colorByRequired: boolean;
    advancedAxisControlRequired: boolean;
    preserveTitle: boolean;
    parentSelectionPlot: TabId; // for plots that contain only data from selection of another plot
    subPlotTabId: TabId;
    xAxis: XAxis;
    yAxis: YAxis;
    trellising: List<ITrellises>;
    baseTrellising: List<ITrellises>;
    previousTrellising: List<ITrellises>;
    plotSettingsRequired: boolean;
    plotSettings: PlotSettings;
    colorByUpdateOnSettingsChangeRequired: boolean;
    filtersUpdateOnYAxisChangeRequired: boolean;
    preserveYAxisIfItsNotPresent: boolean;
    xAxisTextZoomRequired: boolean;
    legendSortingRequired: boolean;
    page: IPage;
    pages: Array<number>;
    columnLimit: number;
    columns: number;
    actualLimit: number;
    plots: IPlot;
    tabId: TabId;
    isLoading: boolean;
    trellisDesign: TrellisDesign;
    selections: List<IChartSelection>;
    detail: ISelectionDetail;
    multiSelectionDetail: IMultiSelectionDetail;
    legend: Array<ILegend>;
    legendDisabled: boolean;
    zoomDisabled: boolean;
    detailsOnDemandDisabled: boolean;
    multiDetailsOnDemandRequired: boolean;
    eventDetailsOnDemandDisabled: boolean;
    eventDetailsOnDemand: IDetailsOnDemand;
    subjectDetailsOnDemand: IDetailsOnDemand;
    multiEventDetailsOnDemand: Map<any, IDetailsOnDemand>;
    allSeries: boolean;
    allTrellising: boolean;
    colorsMap: Map<string, string>;
    jumpToAesFromAeNumberLocation: string;
    groupByOptions: GroupByOptions;
    cBioJumpRequired: boolean;
}

export class ScaleTypes {
    static LINEAR_SCALE = 'linear';
    static LOGARITHMIC_SCALE = 'logarithmic';
    static DATETIME_SCALE = 'datetime';
    static CATEGORY_SCALE = 'category';
}

export enum TimepointConstants {
    CYCLE_DAY = 'CYCLE_DAY',
    VISIT = 'VISIT',
    VISIT_NUMBER = 'VISIT_NUMBER'
}

export enum YAxisParameters {
    MEASUREMENT_TIMEPOINT = 'MEASUREMENT_TIMEPOINT',
    MEASUREMENT = 'MEASUREMENT'
}
export enum YAxisType {
    BEST_RESPONSE = 'BEST_RESPONSE',
    ASSESSMENT_RESPONSE = 'ASSESSMENT_RESPONSE'
}


export const TabRecord = Record({
    isInitialised: false,
    noData: false,
    advancedMeasuredControlRequired: false,
    advancedAxisControlRequired: false,
    preserveTitle: false,
    preserveGlobalTitle: false,
    xAxis: new AxisRecord({
        option: null,
        options: List<DynamicAxis>(),
        zoom: new ZoomRecord({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100})
    }),
    yAxis: new AxisRecord({
        option: null,
        options: List<string>(),
        zoom: new ZoomRecord({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100})
    }),
    trellisingRequired: false,
    colorByRequired: false,
    plotSettingsRequired: false,
    plotSettings: Map<string, any>(),
    colorByUpdateOnSettingsChangeRequired: false,
    filtersUpdateOnYAxisChangeRequired: false,
    preserveYAxisIfItsNotPresent: false,
    parentSelectionPlot: null,
    subPlotTabId: null,
    baseTrellising: List<ITrellises>(),
    trellising: List<ITrellises>(),
    previousTrellising: List<ITrellises>(),
    page: null,
    pages: null,
    columnLimit: 2,
    columns: null,
    actualLimit: null,
    xAxisTextZoomRequired: false,
    legendSortingRequired: true,
    tabId: null,
    isLoading: false,
    trellisDesign: null,
    plots: List<IPlot>(),
    selections: List<IChartSelection>(),
    detail: null,
    multiSelectionDetail: null,
    legend: Array<ILegend>(),
    legendDisabled: false,
    zoomIsHidden: false,
    zoomDisabled: false,
    zoomRangeRequired: false,
    cBioJumpRequired: false,
    detailsOnDemandDisabled: false,
    multiDetailsOnDemandRequired: false,
    eventDetailsOnDemandDisabled: false,
    eventDetailsOnDemand: new DetailsOnDemandRecord(),
    subjectDetailsOnDemand: new DetailsOnDemandRecord(),
    multiEventDetailsOnDemand: Map<any, IDetailsOnDemand>(),
    allSeries: false,
    allTrellising: false,
    colorsMap: null,
    availableScaleTypes: [ScaleTypes.LINEAR_SCALE],
    scaleType: ScaleTypes.LINEAR_SCALE,
    logarithmicScaleRequired: false,
    isAllColoringOptionAvailable: true,
    hasEventFilters: true,
    customControlLabels: null, // null if not required. we consider that if multiple legends required array of custom labels is put here
    jumpToAesFromAeNumberLocation: '',
    groupByOptions: Map(),
    groupBySettings: Map(),
    settingLabelRequired: false,
    zoomMargin: null,
    colorByForAxis: Map<YAxisType, string>()
});

export const TabInfo = Record({
    id: '',
    name: ''
});

// TODO: remove TabName and TabId and use this instead or refactor them somehow
// may be it would be easier to create a method that returns page name by tabId or whatever is needed by ssv
export const TabRecords = Record({
    CARDIAC_BOXPLOT: new TabInfo({id: 'CARDIAC_BOXPLOT', name: 'Cardiac Measurements over Time'}),
    LUNG_FUNCTION_BOXPLOT: new TabInfo({id: 'LUNG_FUNCTION_BOXPLOT', name: 'Lung Function Measurements Over Time'}),
    EXACERBATIONS_OVER_TIME: new TabInfo({id: 'EXACERBATIONS_OVER_TIME', name: 'Exacerbations Over Time'}),
    EXACERBATIONS_COUNTS: new TabInfo({id: 'EXACERBATIONS_COUNTS', name: 'Exacerbations Onset'}),
    EXACERBATIONS_GROUPED_COUNTS: new TabInfo({id: 'EXACERBATIONS_GROUPED_COUNTS', name: 'Exacerbations Counts'}),
    RENAL_LABS_BOXPLOT: new TabInfo({id: 'RENAL_LABS_BOXPLOT', name: 'Creatinine Clearance'}),
    RENAL_CKD_BARCHART: new TabInfo({id: 'RENAL_CKD_BARCHART', name: 'CKD Distribution'}),
    AES_COUNTS_BARCHART: new TabInfo({id: 'AES_COUNTS_BARCHART', name: 'AEs Subject Counts'}),
    AES_TABLE: new TabInfo({id: 'AES_COUNTS_BARCHART', name: 'AEs Table'}),
    AES_OVER_TIME: new TabInfo({id: 'AES_OVER_TIME', name: 'AEs Over Time'}),
    AE_SUMMARIES: new TabInfo({id: 'AE_SUMMARIES', name: 'AE Summaries'}),
    CVOT_ENDPOINTS_COUNTS: new TabInfo({
        id: 'CVOT_ENDPOINTS_COUNTS',
        name: 'Additional CVOT Suspected Endpoint Counts'
    }),
    CVOT_ENDPOINTS_OVER_TIME: new TabInfo({
        id: 'CVOT_ENDPOINTS_OVER_TIME',
        name: 'Additional Suspected CVOT Endpoints Over Time'
    }),
    CEREBROVASCULAR_COUNTS: new TabInfo({id: 'CEREBROVASCULAR_COUNTS', name: 'Cerebrovascular Event Counts'}),
    CEREBROVASCULAR_EVENTS_OVER_TIME: new TabInfo({
        id: 'CEREBROVASCULAR_EVENTS_OVER_TIME',
        name: 'Cerebrovascular Events Over Time'
    }),
    CI_EVENT_COUNTS: new TabInfo({id: 'CI_EVENT_COUNTS', name: 'CI Event Counts'}),
    CI_EVENT_OVERTIME: new TabInfo({id: 'CI_EVENT_OVERTIME', name: 'CI Events Over Time'}),
    VITALS_BOXPLOT: new TabInfo({id: 'VITALS_BOXPLOT', name: 'Vitals Measurements over Time'}),
    LAB_LINEPLOT: new TabInfo({id: 'LAB_LINEPLOT', name: 'Line Plot'}),
    LAB_BOXPLOT: new TabInfo({id: 'LAB_BOXPLOT', name: 'Box Plot'}),
    LAB_SHIFTPLOT: new TabInfo({id: 'LAB_SHIFTPLOT', name: 'Shift Plot'}),
    LIVER_HYSLAW: new TabInfo({id: 'LIVER_HYSLAW', name: 'Hy\'s Law'}),
    POPULATION_BARCHART: new TabInfo({id: 'POPULATION_BARCHART', name: 'Summary Plot'}),
    POPULATION_TABLE: new TabInfo({id: 'POPULATION_TABLE', name: 'Summary Table'}),
    CONMEDS_BARCHART: new TabInfo({id: 'CONMEDS_BARCHART', name: 'Conmeds Counts'}),
    ANALYTE_CONCENTRATION: new TabInfo({id: 'ANALYTE_CONCENTRATION', name: 'Analyte Concentration Over Time'}),
    DOSE_PROPORTIONALITY_BOX_PLOT: new TabInfo({
        id: 'DOSE_PROPORTIONALITY_BOX_PLOT',
        name: 'Dose Proportionality Exploration'
    }),
    PK_RESULT_OVERALL_RESPONSE: new TabInfo({id: 'PK_RESULT_OVERALL_RESPONSE', name: 'PK-Response Exploration'}),
    SINGLE_SUBJECT_DOSE_TAB: new TabInfo({id: 'SINGLE_SUBJECT_DOSE_TAB', name: 'Dose'}),
    SINGLE_SUBJECT_SUMMARY_TAB: new TabInfo({id: 'SINGLE_SUBJECT_SUMMARY_TAB', name: 'Summary'}),
    SINGLE_SUBJECT_NEW_SUMMARY_TAB: new TabInfo({id: 'SINGLE_SUBJECT_NEW_SUMMARY_TAB', name: 'Detailed Summary'}),
    SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB',
        name: 'Dose Discontinuation'
    }),
    SINGLE_SUBJECT_AE_TAB: new TabInfo({id: 'SINGLE_SUBJECT_AE_TAB', name: 'Adverse Events'}),
    SINGLE_SUBJECT_CONMEDS_TAB: new TabInfo({id: 'SINGLE_SUBJECT_CONMEDS_TAB', name: 'Conmeds'}),
    SINGLE_SUBJECT_DEATH_TAB: new TabInfo({id: 'SINGLE_SUBJECT_DEATH_TAB', name: 'Death'}),
    SINGLE_SUBJECT_EXACERBATIONS_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_EXACERBATIONS_TAB',
        name: 'Respiratory Exacerbations'
    }),
    SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB',
        name: 'Liver Diagnostic Investigation'
    }),
    SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB',
        name: 'Liver Risk Factors'
    }),
    SINGLE_SUBJECT_TIMELINE: new TabInfo({name: 'Timeline'}),
    SINGLE_SUBJECT_MEDICAL_HISTORY_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_MEDICAL_HISTORY_TAB',
        name: 'Medical History'
    }),
    SINGLE_SUBJECT_NICOTINE_TAB: new TabInfo({id: 'SINGLE_SUBJECT_NICOTINE_TAB', name: 'Substance Use - Nicotine'}),
    SINGLE_SUBJECT_SAE_TAB: new TabInfo({id: 'SINGLE_SUBJECT_SAE_TAB', name: 'Serious Adverse Events'}),
    SINGLE_SUBJECT_SURGICAL_HISTORY_TAB: new TabInfo({
        id: 'SINGLE_SUBJECT_SURGICAL_HISTORY_TAB',
        name: 'Surgical History'
    }),
    SINGLE_SUBJECT_ALCOHOL_TAB: new TabInfo({id: 'SINGLE_SUBJECT_ALCOHOL_TAB', name: 'Substance Use - Alcohol'}),
    SINGLE_SUBJECT_LAB_LINEPLOT: new TabInfo({id: 'SINGLE_SUBJECT_LAB_LINEPLOT', name: 'Labs'}),
    SINGLE_SUBJECT_VITALS_LINEPLOT: new TabInfo({id: 'SINGLE_SUBJECT_VITALS_LINEPLOT', name: 'Vitals'}),
    SINGLE_SUBJECT_LIVER_HYSLAW: new TabInfo({id: 'SINGLE_SUBJECT_LIVER_HYSLAW', name: 'Liver'}),
    SINGLE_SUBJECT_RENAL_LINEPLOT: new TabInfo({id: 'SINGLE_SUBJECT_RENAL_LINEPLOT', name: 'Renal'}),
    SINGLE_SUBJECT_CARDIAC_LINEPLOT: new TabInfo({id: 'SINGLE_SUBJECT_CARDIAC_LINEPLOT', name: 'Cardiac'}),
    SINGLE_SUBJECT_LUNG_LINEPLOT: new TabInfo({id: 'SINGLE_SUBJECT_LUNG_LINEPLOT', name: 'Lung Function'}),

    BIOMARKERS_HEATMAP_PLOT: new TabInfo({id: 'BIOMARKERS_HEATMAP_PLOT', name: 'Genomic Profile'}),
    TUMOUR_RESPONSE_WATERFALL_PLOT: new TabInfo({id: 'TUMOUR_RESPONSE_WATERFALL_PLOT', name: 'Waterfall plot (Beta)'}),
    TL_DIAMETERS_PLOT: new TabInfo({id: 'TL_DIAMETERS_PLOT', name: 'TL diameters over time (Beta)'}),
    TUMOUR_RESPONSE_PRIOR_THERAPY: new TabInfo({
        id: 'TUMOUR_RESPONSE_PRIOR_THERAPY',
        name: 'Prior therapy vs. Time on compound (Beta)'
    }),
    AES_ACUITY_SUMMARIES_MODULES: new TabInfo({name: 'ACUITY AE Summaries Modules'}),
    AES_ACUITY_TOLERABILITY_MODULES: new TabInfo({name: 'ACUITY Tolerability Modules'}),
    SPOTFIRE: new TabInfo({name: 'ACUITY Exposure Modules'}),
    RESPIRATORY_ACUITY_RESPIRATORY_MODULES: new TabInfo({name: 'ACUITY Respiratory Modules'}),
    RESPIRATORY_DETECT_RESPIRATORY_MODULES: new TabInfo({name: 'DETECT Respiratory Modules'}),
    ONCOLOGY_SPORTFIRE: new TabInfo({name: 'ACUITY Oncology Modules'}),
    TL_DIAMETERS_PER_SUBJECT_PLOT: new TabInfo({id: 'TL_DIAMETERS_PER_SUBJECT_PLOT'}),
    CTDNA_PLOT: new TabInfo({id: 'CTDNA_PLOT', name: 'ctDNA'}),
    AES_CHORD_DIAGRAM: new TabInfo({id: 'AES_CHORD_DIAGRAM', name: 'AEs Chord Diagram'}),
    QT_PROLONGATION: new TabInfo({id: 'QT_PROLONGATION', name: 'QT Prolongation'})
});

export class TabId {
    static CARDIAC_BOXPLOT = 'CARDIAC_BOXPLOT';

    // Check paths in ExacerbationsComponent.routing.ts#exacerbationsRoutes
    static LUNG_FUNCTION_BOXPLOT = 'LUNG_FUNCTION_BOXPLOT';
    static EXACERBATIONS_OVER_TIME = 'EXACERBATIONS_OVER_TIME';
    static EXACERBATIONS_COUNTS = 'EXACERBATIONS_COUNTS';
    static EXACERBATIONS_GROUPED_COUNTS = 'EXACERBATIONS_GROUPED_COUNTS';

    static RENAL_LABS_BOXPLOT = 'RENAL_LABS_BOXPLOT';
    static RENAL_CKD_BARCHART = 'RENAL_CKD_BARCHART';

    static AES_COUNTS_BARCHART = 'AES_COUNTS_BARCHART';
    static AES_TABLE = 'AES_COUNTS_BARCHART';
    static AES_OVER_TIME = 'AES_OVER_TIME';
    static AE_SUMMARIES = 'AE_SUMMARIES';
    static AES_CHORD_DIAGRAM = 'AES_CHORD_DIAGRAM';

    static CVOT_ENDPOINTS_COUNTS = 'CVOT_ENDPOINTS_COUNTS';
    static CVOT_ENDPOINTS_OVER_TIME = 'CVOT_ENDPOINTS_OVER_TIME';

    static CEREBROVASCULAR_COUNTS = 'CEREBROVASCULAR_COUNTS';
    static CEREBROVASCULAR_EVENTS_OVER_TIME = 'CEREBROVASCULAR_EVENTS_OVER_TIME';

    static CI_EVENT_COUNTS = 'CI_EVENT_COUNTS';
    static CI_EVENT_OVERTIME = 'CI_EVENT_OVERTIME';

    static VITALS_BOXPLOT = 'VITALS_BOXPLOT';

    static LAB_LINEPLOT = 'LAB_LINEPLOT';
    static LAB_BOXPLOT = 'LAB_BOXPLOT';
    static LAB_SHIFTPLOT = 'LAB_SHIFTPLOT';

    static LIVER_HYSLAW = 'LIVER_HYSLAW';

    static POPULATION_BARCHART = 'POPULATION_BARCHART';
    static POPULATION_TABLE = 'POPULATION_TABLE';

    static CONMEDS_BARCHART = 'CONMEDS_BARCHART';

    static ANALYTE_CONCENTRATION = 'ANALYTE_CONCENTRATION';
    static DOSE_PROPORTIONALITY_BOX_PLOT = 'DOSE_PROPORTIONALITY_BOX_PLOT';
    static PK_RESULT_OVERALL_RESPONSE = 'PK_RESULT_OVERALL_RESPONSE';

    /*tabs with table only*/
    static SINGLE_SUBJECT_DOSE_TAB = 'SINGLE_SUBJECT_DOSE_TAB';
    static SINGLE_SUBJECT_SUMMARY_TAB = 'SINGLE_SUBJECT_SUMMARY_TAB';
    static SINGLE_SUBJECT_NEW_SUMMARY_TAB = 'SINGLE_SUBJECT_NEW_SUMMARY_TAB';
    static SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB = 'SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB';
    static SINGLE_SUBJECT_AE_TAB = 'SINGLE_SUBJECT_AE_TAB';
    static SINGLE_SUBJECT_CONMEDS_TAB = 'SINGLE_SUBJECT_CONMEDS_TAB';
    static SINGLE_SUBJECT_DEATH_TAB = 'SINGLE_SUBJECT_DEATH_TAB';
    static SINGLE_SUBJECT_EXACERBATIONS_TAB = 'SINGLE_SUBJECT_EXACERBATIONS_TAB';
    static SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB = 'SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB';
    static SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB = 'SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB';
    static SINGLE_SUBJECT_MEDICAL_HISTORY_TAB = 'SINGLE_SUBJECT_MEDICAL_HISTORY_TAB';
    static SINGLE_SUBJECT_NICOTINE_TAB = 'SINGLE_SUBJECT_NICOTINE_TAB';
    static SINGLE_SUBJECT_SAE_TAB = 'SINGLE_SUBJECT_SAE_TAB';
    static SINGLE_SUBJECT_SURGICAL_HISTORY_TAB = 'SINGLE_SUBJECT_SURGICAL_HISTORY_TAB';
    static SINGLE_SUBJECT_ALCOHOL_TAB = 'SINGLE_SUBJECT_ALCOHOL_TAB';

    static SINGLE_SUBJECT_LAB_LINEPLOT = 'SINGLE_SUBJECT_LAB_LINEPLOT';
    static SINGLE_SUBJECT_VITALS_LINEPLOT = 'SINGLE_SUBJECT_VITALS_LINEPLOT';
    static SINGLE_SUBJECT_LIVER_HYSLAW = 'SINGLE_SUBJECT_LIVER_HYSLAW';
    static SINGLE_SUBJECT_RENAL_LINEPLOT = 'SINGLE_SUBJECT_RENAL_LINEPLOT';
    static SINGLE_SUBJECT_CARDIAC_LINEPLOT = 'SINGLE_SUBJECT_CARDIAC_LINEPLOT';
    static SINGLE_SUBJECT_LUNG_LINEPLOT = 'SINGLE_SUBJECT_LUNG_LINEPLOT';

    static BIOMARKERS_HEATMAP_PLOT = 'BIOMARKERS_HEATMAP_PLOT';
    static CTDNA_PLOT = 'CTDNA_PLOT';
    static TUMOUR_RESPONSE_WATERFALL_PLOT = 'TUMOUR_RESPONSE_WATERFALL_PLOT';

    static TL_DIAMETERS_PLOT = 'TL_DIAMETERS_PLOT';
    static TL_DIAMETERS_PER_SUBJECT_PLOT = 'TL_DIAMETERS_PER_SUBJECT_PLOT';
    static TUMOUR_RESPONSE_PRIOR_THERAPY = 'TUMOUR_RESPONSE_PRIOR_THERAPY';

    static QT_PROLONGATION = 'QT_PROLONGATION';
}

export class LegendSymbol {
    static CIRCLE = 'CIRCLE';
    static SQUARE = 'SQUARE';
    static DIAMOND = 'DIAMOND';
    static TRIANGLE_RIGHT = 'TRIANGLE_RIGHT';
    static LEFT_ARROW = 'LEFT_ARROW';
}

export class FilterId {
    static CARDIAC = 'CARDIAC';
    static CONMEDS = 'CONMEDS';
    static COHORT = 'COHORT';
    static DOSE = 'DOSE';
    static LAB = 'LAB';
    static AES = 'AES';
    static SAE = 'SAE';
    static RENAL = 'RENAL';
    static VITALS = 'VITALS';
    static PATIENT_REPORTED_DATA = 'PATIENT_REPORTED_DATA';
    static LUNG_FUNCTION = 'LUNG_FUNCTION';
    static EXACERBATIONS = 'EXACERBATIONS';
    static RECIST = 'RECIST';
    static LIVER = 'LIVER';
    static POPULATION = 'POPULATION';
    static SINGLE_SUBJECT = 'SINGLE_SUBJECT';
    static DEATH = 'DEATH';
    static DOSE_DISCONTINUATION = 'DOSE_DISCONTINUATION';
    static MEDICAL_HISTORY = 'MEDICAL_HISTORY';
    static LIVER_DIAGNOSTIC_INVESTIGATION = 'LIVER_DIAGNOSTIC_INVESTIGATION';
    static LIVER_RISK_FACTORS = 'LIVER_RISK_FACTORS';
    static ALCOHOL = 'ALCOHOL';
    static SURGICAL_HISTORY = 'SURGICAL_HISTORY';
    static NICOTINE = 'NICOTINE';
    static CIEVENTS = 'CIEVENTS';
    static CEREBROVASCULAR = 'CEREBROVASCULAR';
    static BIOMARKERS = 'BIOMARKERS';
    static CVOT = 'CVOT';
    static EXPOSURE = 'EXPOSURE';
    static TUMOUR_RESPONSE = 'TUMOUR_RESPONSE';
    static CTDNA = 'CTDNA';
    static DOSE_PROPORTIONALITY = 'DOSE_PROPORTIONALITY';
    static PK_RESULT_OVERALL_RESPONSE = 'PK_RESULT_OVERALL_RESPONSE';
    static QT_PROLONGATION: 'QT_PROLONGATION';
}

export interface TabStore extends Map<string, any> {
    tabs: Map<TabId, ITab>;
    height: number;
}

export interface IJumpLink {
    originTab: string;
    label: string;
    filtersToReset: string[];
    destinationFilterKey: FilterId;
    destinationUrl: string;
    metadataFilterKey: string;
    filterKey: string;
}

export interface DashboardRow {
    rowName: string;
    rowValue: DashboardCell[];
}

export interface DashboardCell {
    rowName: string;
    columnName: string;
    countValue: any;
}

export interface Header {
    columnName: string;
    total: number;
}

export interface Dashboard {
    table: DashboardRow[];
    tableCopy: DashboardRow[];
    tableHeaders: Header[];
    highlightedCells: string[];
    selectedData: string[];
    columnSubHeader: string;
}

export class TabName {
    static AES_SUBJECT_COUNTS = 'AEs Subject Counts';
    static AES_OVER_TIME = 'AEs Over Time';
    static AES_TABLE = 'AEs Table';
    static AE_SUMMARIES = 'AE Summaries';
    static AES_CHORD_DIAGRAM = 'AEs Chord Diagram';

    static CI_EVENT_COUNTS = 'CI Event Counts';
    static CI_EVENT_OVERTIME = 'CI Events Over Time';

    static CEREBROVASCULAR_COUNTS = 'Cerebrovascular Event Counts';
    static CEREBROVASCULAR_EVENTS_OVER_TIME = 'Cerebrovascular Events Over Time';

    static CVOT_ENDPOINTS_COUNTS = 'Additional CVOT Suspected Endpoint Counts';
    static CVOT_ENDPOINTS_OVER_TIME = 'Additional Suspected CVOT Endpoints Over Time';

    static AES_ACUITY_SUMMARIES_MODULES = 'ACUITY AE Summaries Modules';
    static AES_ACUITY_TOLERABILITY_MODULES = 'ACUITY Tolerability Modules';
    static CARDIAC_MEASUREMENTS_OVER_TIME = 'Cardiac Measurements over Time';
    static CONMEDS_COUNTS = 'Conmeds Counts';
    static SPOTFIRE = 'ACUITY Exposure Modules';
    static ANALYTE_CONCENTRATION_OVER_TIME = 'Analyte Concentration Over Time';
    static DOSE_PROPORTIONALITY_BOX_PLOT = 'Dose Proportionality Exploration';
    static PK_RESULT_OVERALL_RESPONSE = 'PK-Response Exploration';
    static RENAL_CREATININE_CLEARANCE = 'Creatinine Clearance';
    static RENAL_CKD_DISTRIBUTION = 'CKD Distribution';
    static VITALS_MEASUREMENTS_OVER_TIME = 'Vitals Measurements over Time';
    static EXACERBATIONS_COUNTS = 'Exacerbations Counts';
    static RESPIRATORY_LUNG_FUNCTION_MEASUREMENTS_OVER_TIME = 'Lung Function Measurements Over Time';
    static EXACERBATIONS_OVER_TIME = 'Exacerbations Over Time';
    static EXACERBATIONS_ONSET = 'Exacerbations Onset';
    static RESPIRATORY_ACUITY_RESPIRATORY_MODULES = 'ACUITY Respiratory Modules';
    static RESPIRATORY_DETECT_RESPIRATORY_MODULES = 'DETECT Respiratory Modules';
    static LIVER_HYS_LAW = 'Hy\'s Law';
    static POPULATION_SUMMARY_PLOT = 'Summary Plot';
    static POPULATION_SUMMARY_TABLE = 'Summary Table';
    static LABS_BOX_PLOT = 'Box Plot';
    static LABS_SHIFT_PLOT = 'Shift Plot';
    static LABS_LINE_PLOT = 'Line Plot';
    static ONCOLOGY_SPORTFIRE = 'ACUITY Oncology Modules';
    static TUMOUR_RESPONSE_WATERFALL = 'Waterfall plot (Beta)';
    static TUMOUR_RESPONSE_TL_DIAMETERS = 'TL diameters over time (Beta)';
    static TUMOUR_RESPONSE_PRIOR_THERAPY = 'Prior therapy vs. Time on compound (Beta)';
    static BIOMARKERS_GENOMIC_PROFILE = 'Genomic Profile';
    static CTDNA = 'ctDNA';
    static QT_PROLONGATION = 'QT Prolongation';

    static SINGLE_SUBJECT_SUMMARY = 'Summary';
    static SINGLE_SUBJECT_NEW_SUMMARY = 'New Summary (Beta)';
    static SINGLE_SUBJECT_TIMELINE = 'Timeline';
    static SINGLE_SUBJECT_AES = 'Adverse Events';
    static SINGLE_SUBJECT_SAES = 'Serious Adverse Events';
    static SINGLE_SUBJECT_CONMEDS = 'Conmeds';
    static SINGLE_SUBJECT_VITALS = 'Vitals';
    static SINGLE_SUBJECT_LABS = 'Labs';
    static SINGLE_SUBJECT_CARDIAC = 'Cardiac';
    static SINGLE_SUBJECT_RENAL = 'Renal';
    static SINGLE_SUBJECT_LIVER = 'Liver';
    static SINGLE_SUBJECT_LUNG_FUNCTION = 'Lung Function';
    static SINGLE_SUBJECT_RESPIRATORY = 'Respiratory Exacerbations';
    static SINGLE_SUBJECT_DEATH = 'Death';
    static SINGLE_SUBJECT_DOSE = 'Dose';
    static SINGLE_SUBJECT_DOSE_DISCONTINUATION = 'Dose Discontinuation';
    static SINGLE_SUBJECT_MEDICAL_HISTORY = 'Medical History';
    static SINGLE_SUBJECT_LIVER_DIAGNOSTIC_INVESTIGATION = 'Liver Diagnostic Investigation';
    static SINGLE_SUBJECT_LIVER_RISK_FACTORS = 'Liver Risk Factors';
    static SINGLE_SUBJECT_ALCOHOL = 'Substance Use - Alcohol';
    static SINGLE_SUBJECT_SURGICAL_HISTORY = 'Surgical History';
    static SINGLE_SUBJECT_NICOTINE = 'Substance Use - Nicotine';
}

export const NEW_APPROACH_TAB_LIST = List<TabId>([
    TabId.POPULATION_BARCHART,
    TabId.AES_COUNTS_BARCHART,
    TabId.AES_OVER_TIME,
    TabId.AE_SUMMARIES,
    TabId.CI_EVENT_COUNTS,
    TabId.CI_EVENT_OVERTIME,
    TabId.CEREBROVASCULAR_COUNTS,
    TabId.CEREBROVASCULAR_EVENTS_OVER_TIME,
    TabId.CVOT_ENDPOINTS_COUNTS,
    TabId.CVOT_ENDPOINTS_OVER_TIME,
    TabId.LAB_SHIFTPLOT,
    TabId.LAB_BOXPLOT,
    TabId.LAB_LINEPLOT,
    TabId.SINGLE_SUBJECT_LAB_LINEPLOT,
    TabId.TUMOUR_RESPONSE_WATERFALL_PLOT,
    TabId.TUMOUR_RESPONSE_PRIOR_THERAPY,
    TabId.TL_DIAMETERS_PLOT,
    TabId.TL_DIAMETERS_PER_SUBJECT_PLOT,
    TabId.ANALYTE_CONCENTRATION,
    TabId.DOSE_PROPORTIONALITY_BOX_PLOT,
    TabId.PK_RESULT_OVERALL_RESPONSE,
    TabId.BIOMARKERS_HEATMAP_PLOT,
    TabId.CTDNA_PLOT,
    TabId.AES_CHORD_DIAGRAM,
    TabId.QT_PROLONGATION,
    TabId.EXACERBATIONS_GROUPED_COUNTS,
    TabId.EXACERBATIONS_COUNTS,
    TabId.RENAL_LABS_BOXPLOT,
    TabId.RENAL_CKD_BARCHART,
    TabId.LUNG_FUNCTION_BOXPLOT,
    TabId.EXACERBATIONS_OVER_TIME,
    TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT,
    TabId.SINGLE_SUBJECT_LUNG_LINEPLOT,
    TabId.CONMEDS_BARCHART,
    TabId.CARDIAC_BOXPLOT,
    TabId.SINGLE_SUBJECT_RENAL_LINEPLOT,
    TabId.VITALS_BOXPLOT,
    TabId.SINGLE_SUBJECT_VITALS_LINEPLOT
]);
