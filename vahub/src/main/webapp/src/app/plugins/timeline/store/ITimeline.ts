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

import {List, Map, OrderedMap, Record} from 'immutable';
import {DynamicAxis, DynamicAxisRecord} from '../../../common/trellising/store/ITrellising';

export const TrackRecord = Record({
    name: null,
    expansionLevel: 0,
    selected: false,
    order: 0,
    data: null,
    changed: true
});

export const TrackTimePointRecord = Record({
    date: -1,
    dayHour: -1,
    dayHourAsString: null,
    studyDayHourAsString: null,
    doseDayHour: -1
});

export const TrackDataPointRecord = Record({
    start: null,
    end: null,
    metadata: null
});

export const SubjectRecord = Record({
    subjectId: null,
    id: null,
    tracks: null
});

export const PageRecord = Record({
    limit: 0,
    offset: 0
});

export const ZoomRecord = Record({
    absMax: 0,
    absMin: 0,
    zoomMax: 0,
    zoomMin: 0,
    zoomed: false
});

export const InitialOpeningStateRecord = Record({
    tracks: List<ITrack>()
});

export const CheckBoxItemRecord = Record({
    label: null,
    selected: null,
    available: true
});

export const EcgWarningsRecord = Record({
    qtcfMale: <CheckBoxItem> new CheckBoxItemRecord({
        label: 'QTcF male',
        selected: true,
        available: true
    }),
    qtcfFemale: <CheckBoxItem> new CheckBoxItemRecord({
        label: 'QTcF female',
        selected: true,
        available: true
    }),
    qtcfChange: <CheckBoxItem> new CheckBoxItemRecord({
        label: 'QTcF change from baseline',
        selected: true,
        available: true
    }),
    abnormal: <CheckBoxItem> new CheckBoxItemRecord({
        label: 'Abnormal',
        selected: true,
        available: true
    }),
    significant: <CheckBoxItem> new CheckBoxItemRecord({
        label: 'Significant',
        selected: true,
        available: true
    })
});

export const DayZeroOptionRecord = Record({
    displayName: null,
    serverName: null
});

export const ECG_WARNING_OPTIONS: any[] = [
    {label: 'QTcF male', key: 'qtcfMale'},
    {label: 'QTcF female', key: 'qtcfFemale'},
    {label: 'QTcF change from baseline', key: 'qtcfChange'},
    {label: 'Abnormal', key: 'abnormal'},
    {label: 'Significant', key: 'significant'}
];

export class TimelineId {
    static COMPARE_SUBJECTS = 'compareSubjects';
    static SUBJECT_PROFILE = 'subjectProfile';
}

export interface InitializedTimeline {
    timelineId: TimelineId;
    isInitialized: boolean;
}

export interface CheckBoxItem extends Map<string, any> {
    label: string;
    selected: boolean;
    available: boolean;
}

export interface EcgWarnings extends Map<string, any> {
    qtcfMale: CheckBoxItem;
    qtcfFemale: CheckBoxItem;
    qtcfChange: CheckBoxItem;
    abnormal: CheckBoxItem;
    significant: CheckBoxItem;
}

export enum EcgWarningKeys {
    QTCF_MALE = 'qtcfMale',
    QTCF_FEMALE = 'qtcfFemale',
    QTCF_CHANGE = 'qtcfChange',
    ABNORMAL = 'abnormal',
    SIGNIFICANT = 'significant'
}

export enum FilterName {
    AES = 'Adverse events',
    DOSE = 'Dose',
    SUMMARY = 'Status summary',
    CONMEDS = 'Conmeds',
    EXACERBATION = 'Exacerbation ',
    HEALTHCARE_ENCOUNTERS = 'Healthcare encounters',
    SPIROMETRY = 'Spirometry',
    LABS = 'Lab measurements',
    VITALS = 'Vitals',
    POPULATION = 'Population',
    ECG = 'ECG',
    PRD = 'Patient data'
}

export enum TrackName {
    AES = 'Adverse events',
    DOSE = 'Dose',
    SUMMARY = 'Status summary',
    CONMEDS = 'Conmeds',
    EXACERBATION = 'Exacerbation',
    HEALTHCARE_ENCOUNTERS = 'Healthcare encounters',
    SPIROMETRY = 'Spirometry',
    LABS = 'Lab measurements',
    VITALS = 'Vitals',
    ECG = 'ECG',
    PRD = 'Patient data'
}

export enum PageName {
    QT_PROLONGATION = 'QT Prolongation',
    POPULATION = 'Population Summary',
    AES = 'Adverse Events',
    LINKED_AES = 'Linked Adverse Events',
    CEREBROVASCULAR = 'Cerebrovascular Events',
    SAES = 'Serious Adverse Events',
    CVOT = 'Additional Suspected CVOT Endpoint',
    DOSING_EXPOSURE = 'Analyte Concentration',
    CONMEDS = 'Conmeds',
    LABS = 'Labs',
    VITALS = 'Vital Signs',
    CARDIAC = 'Cardiac Function',
    RENAL = 'Renal Function',
    LIVER = 'Liver Function',
    RESPIRATORY = 'Respiratory Function',
    EXACERBATIONS = 'Exacerbations',
    LUNG_FUNCTION = 'Lung Function',
    TIMELINE = 'Timeline',
    SINGLE_SUBJECT = 'Single Subject',
    RECIST = 'RECIST',
    CIEVENTS = 'CI Events',
    GENOMIC_PROFILE = 'Genomic Profile',
    COHORT_EDITOR = 'Cohort Editor',
    TUMOUR_RESPONSE = 'Tumour response',
    TL_DIAMETERS = 'RECIST (Beta)',
    CTDNA = 'ctDNA',
    DOSE_PROPORTIONALITY = 'Dose Proportionality',
    PK_RESPONSE = 'PK-Response',
    PK_PARAMETERS_RESPONSE = 'PK Parameters (Response)'
}

export class DayZero {
    static DAYS_SINCE_FIRST_DOSE = <any> 'DAYS_SINCE_FIRST_DOSE';
    static DAYS_SINCE_RANDOMISATION = <any> 'DAYS_SINCE_RANDOMISATION';
    static DAYS_SINCE_FIRST_TREATMENT = <any> 'DAYS_SINCE_FIRST_TREATMENT';
    static STUDY_DAY = <any> 'STUDY_DAY';
}

export enum LabsYAxisValue {
    RAW = 'Raw values',
    CHANGE_FROM_BASELINE = 'Change from baseline',
    PERCENT_CHANGE_FROM_BASELINE = '% change from baseline',
    REF_RANGE_NORM = 'Ref. Range Norm. Value',
    TIMES_UPPER_REF = 'Times Upper Ref. Value',
    TIMES_LOWER_REF = 'Times Lower Ref. Value'
}

export enum SpirometryYAxisValue {
    RAW = 'Raw values',
    CHANGE_FROM_BASELINE = 'Change from baseline',
    PERCENT_CHANGE_FROM_BASELINE = '% change from baseline'
}

export enum EcgYAxisValue {
    RAW = 'Raw values',
    CHANGE_FROM_BASELINE = 'Change from baseline',
    PERCENT_CHANGE_FROM_BASELINE = '% change from baseline'
}

export enum VitalsYAxisValue {
    RAW = 'Raw values',
    CHANGE_FROM_BASELINE = 'Change from baseline',
    PERCENT_CHANGE_FROM_BASELINE = '% change from baseline'
}

export enum SortBy {
    ACTUAL_ARM = 'Actual arm',
    AGE = 'Age',
    SEX = 'Sex'
}

export enum SortOrder {
    ASC = 'ASC',
    DESC = 'DESC',
    UNSORTED = 'UNSORTED'
}

export enum SortDirection {
    ASC = 'Asc',
    DESC = 'Desc'
}

export const TimelineRecord = Record({
    id: null,
    isInitialized: false,
    performedJumpToTimeline: false,
    showPagination: true,
    loading: false,
    tracks: List<ITrack>(),
    subjects: Map<string, ISubject>(),
    page: null,
    displayedSubjects: List<string>(),
    zoom: null,
    dayZero: new DynamicAxisRecord({
        value: DayZero.DAYS_SINCE_FIRST_DOSE,
        intarg: null,
        stringarg: null
    }),
    dayZeroOptions: List<DynamicAxis>(),
    labsYAxisValue: LabsYAxisValue.RAW,
    spirometryYAxisValue: SpirometryYAxisValue.RAW,
    ecgYAxisValue: EcgYAxisValue.RAW,
    ecgWarnings: <EcgWarnings> new EcgWarningsRecord(),
    vitalsYAxisValue: VitalsYAxisValue.RAW,
    subjectSort: null,
    plotBands: List<IHighlightedPlotArea>()
});

export interface ITrackTimePoint extends Map<string, any> {
    date: number;
    dayHour: number;
    studyDayHourAsString: string;
    dayHourAsString: string;
    doseDayHour: number;
}

export interface ITrackDataPoint extends Map<string, any> {
    start: ITrackTimePoint;
    end?: ITrackTimePoint;
    metadata?: any;
}

export interface ITrack extends Map<string, any> {
    name: TrackName;
    expansionLevel: number;
    selected: boolean;
    order?: number;
    data?: List<ITrackDataPoint>;
    changed: boolean;
}

export interface ISubject extends Map<string, any> {
    subjectId: string;
    id?: string;
    tracks: List<ITrack>;
}

export interface ITimelinePage extends Map<string, any> {
    limit: number;
    offset: number;
}

export interface IZoom extends Map<string, any> {
    absMin: number;
    absMax: number;
    zoomMin: number;
    zoomMax: number;
    zoomed: boolean;
}

export interface ISort extends Map<string, any> {
    sortTerm: SortBy;
    direction: SortDirection;
}

export interface ITimeline extends Map<string, any> {
    id: TimelineId;
    isInitialized: boolean;
    performedJumpToTimeline: boolean;
    showPagination: boolean;
    loading: boolean;
    tracks: List<ITrack>;
    subjects: OrderedMap<string, ISubject>;
    page: ITimelinePage;
    displayedSubjects: List<string>;
    zoom: IZoom;
    dayZero: DynamicAxis;
    dayZeroOptions: List<DynamicAxis>;
    labsYAxisValue: LabsYAxisValue;
    spirometryYAxisValue: SpirometryYAxisValue;
    ecgYAxisValue: EcgYAxisValue;
    ecgWarnings: EcgWarnings;
    vitalsYAxisValue: VitalsYAxisValue;
    subjectSort: List<ISort>;
    plotBands: List<IHighlightedPlotArea>;
}

export interface IInitialOpeningState extends Map<string, any> {
    tracks: List<ITrack>;
}

export interface AppStore extends Map<string, any> {
    initialOpeningState: Map<TimelineId, IInitialOpeningState>;
    timelines: Map<TimelineId, ITimeline>;
}

export interface IHighlightedPlotArea {
    from: number;
    to: number;
}

export interface PossibleSubjects {
    timelineId: TimelineId;
    subjects: any;
}

export interface PossibleTracks {
    timelineId: TimelineId;
    tracks: ITrack[];
}

export const PlotBandRecord = Record({
    from: null,
    to: null
});

export const TrackSelectionRecord = Record({
    track: null,
    selected: false,
    changed: false,
    order: null
});
