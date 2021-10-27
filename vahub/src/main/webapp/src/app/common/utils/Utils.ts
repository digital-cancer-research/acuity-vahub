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

import {isObject, isString, isUndefined} from 'lodash';
import {Map, Set} from 'immutable';

import {FilterId, TabId, TabName} from '../trellising';
import {PageName} from '../../plugins/timeline/store/ITimeline';
import {isNumeric} from 'rxjs/util/isNumeric';

// /resources/
export const ENDPOINT_PREFIX = '/resources';

// Acronyms that should not be made lowercase anywhere
export const knownAcronyms = ['ID', 'AST', 'ALT', 'PT', 'SOC', 'HLT', 'C-G', 'EGFR',
    'ACUITY', 'AE', 'CKD', 'RECIST', 'TLD', 'ctDNA', 'QT'];

/**
 * Generates a standard url template path from the arguments
 * with information from the the current selected currentDatasets
 * e.g. calling:
 *      getServerPath('a', 'b')
 * will return:
 *      '/resources/a/b
 */
export function getServerPath(...args): string {
    return [ENDPOINT_PREFIX, ...args].join('/');
}

/**
 * Get plugin summary for the currently opened page
 * @returns options {any}
 */
export function getCurrentPluginSummary(): any {
    const [pluginUrl, tabUrl] = window.location.hash.slice(10).split('/');
    return getPluginSummary(pluginUrl, tabUrl ? tabUrl.split('?')[0] : '');
}

/**
 * generate URL for SSV table data endpoint based on TabId
 */
export function getSingleSubjectViewTabEndpoint(tabId): string {
    let urlFromTabId: string;
    switch (tabId) {
        case TabId.SINGLE_SUBJECT_DOSE_TAB:
            urlFromTabId = 'dose/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB:
            urlFromTabId = 'dose-disc/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_AE_TAB:
            urlFromTabId = 'aes/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_CONMEDS_TAB:
            urlFromTabId = 'conmeds/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_DEATH_TAB:
            urlFromTabId = 'death/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_SAE_TAB:
            urlFromTabId = 'sae/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_ALCOHOL_TAB:
            urlFromTabId = 'alcohol/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB:
            urlFromTabId = 'liver-risk/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB:
            urlFromTabId = 'liver-diag/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_NICOTINE_TAB:
            urlFromTabId = 'nicotine/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_SURGICAL_HISTORY_TAB:
            urlFromTabId = 'surgical-history/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_MEDICAL_HISTORY_TAB:
            urlFromTabId = 'medicalhistory/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_EXACERBATIONS_TAB:
            urlFromTabId = 'respiratory/exacerbation/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            urlFromTabId = 'cardiac/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            urlFromTabId = 'vitals/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
            urlFromTabId = 'labs/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
            urlFromTabId = 'renal/single-subject';
            break;
        case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            urlFromTabId = 'respiratory/lung-function/single-subject';
            break;
        default:
            urlFromTabId = '';
    }
    return `/resources/${urlFromTabId}`;
}

/**
 * Get plugin summary by pageName and tabName
 * @param page {string}
 * @param tab {string}
 * @returns options {any}
 */
export function getPluginSummary(page, tab): any {
    tab = tab ? tab.split('?')[0] : '';
    const options: any = {
        eventWidgetName: '',
        showPopulationFilter: true,
        showEventFilter: true,
        showSettings: false,
        showTimelineFilter: false,
        eventFiltersName: '',
        settingsName: '',
        filterId: null,
        pageName: ''
    };

    let tabName: string;

    const urlToTabNameMap = {
        'aes': {
            'subject-counts': TabName.AES_SUBJECT_COUNTS,
            'overtime': TabName.AES_OVER_TIME,
            'table': TabName.AES_TABLE,
            'summary': TabName.AE_SUMMARIES,
            'chord-diagram': TabName.AES_CHORD_DIAGRAM,
            'spotfire': TabName.AES_ACUITY_SUMMARIES_MODULES,
            'spotfireTolerability': TabName.AES_ACUITY_TOLERABILITY_MODULES
        },
        'cievents': {
            'event-counts': TabName.CI_EVENT_COUNTS
        },
        'cvot': {
            'event-counts': TabName.CVOT_ENDPOINTS_COUNTS,
            'overtime': TabName.CVOT_ENDPOINTS_OVER_TIME
        },
        'cerebrovascular': {
            'counts': TabName.CEREBROVASCULAR_COUNTS,
            'overtime': TabName.CEREBROVASCULAR_EVENTS_OVER_TIME
        },
        'cardiac': {
            'boxplot': TabName.CARDIAC_MEASUREMENTS_OVER_TIME
        },
        'conmeds': {
            'plot': TabName.CONMEDS_COUNTS
        },
        'exposure': {
            'spotfire': TabName.SPOTFIRE,
            'analyte-concentration': TabName.ANALYTE_CONCENTRATION_OVER_TIME,
        },
        'dose-proportionality': TabName.DOSE_PROPORTIONALITY_BOX_PLOT,
        'pk-overall-response': TabName.PK_RESULT_OVERALL_RESPONSE,
        'spotfire': TabName.SPOTFIRE,
        'labs': {
            'box-plot': TabName.LABS_BOX_PLOT,
            'shift-plot': TabName.LABS_SHIFT_PLOT,
            'line-plot': TabName.LABS_LINE_PLOT
        },
        'liver': {
            'hyslaw': TabName.LIVER_HYS_LAW
        },
        'oncology': {
            'spotfire': TabName.ONCOLOGY_SPORTFIRE
        },
        'tumour-response': {
            'waterfall': TabName.TUMOUR_RESPONSE_WATERFALL,
        },
        'tumour-lesion': {
            'target-lesion-diameters': TabName.TUMOUR_RESPONSE_TL_DIAMETERS
        },
        'tumour-therapy': {
            'prior-therapy': TabName.TUMOUR_RESPONSE_PRIOR_THERAPY
        },
        'population-summary': {
            'summary-plot': TabName.POPULATION_SUMMARY_PLOT,
            'summary-table': TabName.POPULATION_SUMMARY_TABLE
        },
        'renal': {
            'creatinine': TabName.RENAL_CREATININE_CLEARANCE,
            'ckdstage': TabName.RENAL_CKD_DISTRIBUTION
        },
        'exacerbations': {
            'exacerbations-counts': TabName.EXACERBATIONS_COUNTS,
            'exacerbations-over-time': TabName.EXACERBATIONS_OVER_TIME,
            'exacerbations-onset': TabName.EXACERBATIONS_ONSET
        },
        'respiratory': {
            'lung-function-box-plot': TabName.RESPIRATORY_LUNG_FUNCTION_MEASUREMENTS_OVER_TIME,
            'acuity-spotfire': TabName.RESPIRATORY_ACUITY_RESPIRATORY_MODULES,
            'detect-spotfire': TabName.RESPIRATORY_DETECT_RESPIRATORY_MODULES
        },
        'vitals': {
            'box-plot': TabName.VITALS_MEASUREMENTS_OVER_TIME
        },
        'biomarker': {
            'plot': TabName.BIOMARKERS_GENOMIC_PROFILE
        },
        'ctdna': {
            'ctDNA-plot': TabName.CTDNA
        },
        'machine-insights' : {
            'qt-prolongation': TabName.QT_PROLONGATION
        }
    };

    switch (page) {
        case 'population-summary':
            options.showEventFilter = false;
            options.filterId = FilterId.POPULATION;
            options.pageName = PageName.POPULATION;
            break;
        case 'aes':
            options.eventWidgetName = 'Adverse Events';
            options.eventFiltersName = 'Adverse Event Filters';
            options.filterId = FilterId.AES;
            options.pageName = PageName.AES;
            switch (tab) {
                case 'summary':
                    options.showEventFilter = false;
                    options.showPopulationFilter = false;
                    break;
                case 'chord-diagram':
                    options.showPopulationFilter = true;
                    options.showEventFilter = true;
                    options.showSettings = true;
                    options.settingsName = 'AEs Chord Diagram Settings';
                    break;
                default:
                    options.showEventFilter = true;
                    options.showPopulationFilter = true;
                    break;
            }
            break;
        case 'cerebrovascular':
            options.eventWidgetName = 'Cerebrovascular Events';
            options.eventFiltersName = 'Cerebrovascular Events Filters';
            options.filterId = FilterId.CEREBROVASCULAR;
            options.pageName = PageName.CEREBROVASCULAR;
            break;
        case 'cvot':
            options.eventWidgetName = 'CVOT Endpoint';
            options.eventFiltersName = 'CVOT Endpoint Filters';
            options.filterId = FilterId.CVOT;
            options.pageName = PageName.CVOT;
            break;
        case 'cievents':
            options.eventWidgetName = 'CI Events';
            options.eventFiltersName = 'CI Event Filters';
            options.filterId = FilterId.CIEVENTS;
            options.pageName = PageName.CIEVENTS;
            break;
        case 'machine-insights':
            options.showPopulationFilter = true;
            options.showEventFilter = false;
            break;
        case 'exposure':
            options.pageName = PageName.DOSING_EXPOSURE;
            switch (tab) {
                case 'spotfire':
                    options.showEventFilter = false;
                    options.showPopulationFilter = false;
                    break;
                default:
                    options.showEventFilter = true;
                    options.eventFiltersName = 'Analyte Concentration Filters';
                    options.eventWidgetName = 'Analyte Concentration';
                    options.showPopulationFilter = true;
                    options.showSettings = true;
                    options.settingsName = 'Analyte Concentration Settings';
                    options.filterId = FilterId.EXPOSURE;
                    break;
            }
            break;
        case 'dose-proportionality':
            options.pageName = PageName.DOSE_PROPORTIONALITY;
            options.showPopulationFilter = true;
            options.showEventFilter = true;
            options.eventWidgetName = 'Dose Proportionality';
            options.eventFiltersName = 'Dose Proportionality Filters';
            options.filterId = FilterId.DOSE_PROPORTIONALITY;
            options.showSettings = true;
            options.settingsName = 'Dose Proportionality Settings';
            break;
        case 'pk-overall-response':
            options.pageName = PageName.PK_RESPONSE;
            options.showPopulationFilter = true;
            options.showEventFilter = true;
            options.eventWidgetName = 'PK-Response';
            options.eventFiltersName = 'PK-Response Filters';
            options.filterId = FilterId.PK_RESULT_OVERALL_RESPONSE;
            options.showSettings = true;
            options.settingsName = 'PK-Response Settings';
            break;
        case 'conmeds':
            options.eventWidgetName = 'Conmed Counts';
            options.eventFiltersName = 'Conmeds Filters';
            options.filterId = FilterId.CONMEDS;
            options.pageName = PageName.CONMEDS;
            break;
        case 'labs':
            options.eventWidgetName = 'Lab Measurements';
            options.eventFiltersName = 'Lab Filters';
            options.filterId = FilterId.LAB;
            options.pageName = PageName.LABS;
            break;
        case 'liver':
            options.eventWidgetName = 'Liver Function Measurements';
            options.eventFiltersName = 'Liver Filters';
            options.filterId = FilterId.LIVER;
            options.pageName = PageName.LIVER;
            break;
        case 'respiratory':
            options.eventWidgetName = 'Respiratory Measurements';
            options.eventFiltersName = 'Lung Function Filters';
            options.filterId = FilterId.LUNG_FUNCTION;
            options.pageName = PageName.RESPIRATORY;
            break;
        case 'exacerbations':
            options.eventWidgetName = 'Respiratory Measurements';
            options.eventFiltersName = 'Exacerbations Filters';
            options.filterId = FilterId.EXACERBATIONS;
            options.pageName = PageName.RESPIRATORY;
            break;
        case 'vitals':
            options.eventWidgetName = 'Vitals Measurements';
            options.eventFiltersName = 'Vitals Filters';
            options.filterId = FilterId.VITALS;
            options.pageName = PageName.VITALS;
            break;
        case 'renal':
            options.eventWidgetName = 'Renal Function Measurements';
            options.eventFiltersName = 'Renal Filters';
            options.filterId = FilterId.RENAL;
            options.pageName = PageName.RENAL;
            break;
        case 'cardiac':
            options.eventWidgetName = 'Cardiac Function Measurements';
            options.eventFiltersName = 'Cardiac Filters';
            options.filterId = FilterId.CARDIAC;
            options.pageName = PageName.CARDIAC;
            break;
        case 'biomarker':
            options.eventWidgetName = 'Genomic Profile Filters';
            options.eventFiltersName = 'Genomic Profile Filters';
            options.showEventFilter = true;
            options.showSettings = true;
            options.settingsName = 'Genomic Profile Settings';
            options.filterId = FilterId.BIOMARKERS;
            options.pageName = PageName.GENOMIC_PROFILE;
            break;
        case 'ctdna':
            options.eventWidgetName = 'ctDNA Filters';
            options.eventFiltersName = 'ctDNA Filters';
            options.filterId = FilterId.CTDNA;
            options.pageName = PageName.CTDNA;
            options.showSettings = true;
            options.settingsName = 'ctDNA Settings';
            options.showEventFilter = true;
            break;
        case 'timeline':
            options.eventWidgetName = 'Timeline Events';
            options.eventFiltersName = 'Timeline Filters';
            options.filterId = null;
            options.showTimelineFilter = true;
            options.pageName = PageName.TIMELINE;
            break;
        case 'cohort-editor':
            options.showPopulationFilter = false;
            options.showEventFilter = false;
            options.filterId = null;
            options.pageName = PageName.COHORT_EDITOR;
            break;
        case 'tumour-response':
            options.showEventFilter = false;
            options.eventWidgetName = 'RECIST Filters';
            options.eventFiltersName = 'RECIST Filters';
            options.showEventFilter = true;
            options.pageName = PageName.RECIST;
            options.filterId = FilterId.RECIST;
            break;
        case 'tumour-therapy':
            options.eventWidgetName = 'Previous Lines';
            options.eventFiltersName = 'Previous Lines Filters';
            options.showEventFilter = true;
            options.pageName = PageName.TUMOUR_RESPONSE;
            options.filterId = FilterId.TUMOUR_RESPONSE;
            options.showSettings = true;
            options.settingsName = 'Prior Therapy Settings';
            break;
        case 'tumour-lesion':
            options.showEventFilter = false;
            break;
        case 'singlesubject':
            options.pageName = PageName.SINGLE_SUBJECT + ' -> ';

            switch (tab) {
                case 'timeline-tab':
                    options.eventWidgetName = 'Timeline Events';
                    options.eventFiltersName = 'Timeline Filters';
                    options.showTimelineFilter = true;
                    options.filterId = null;
                    options.pageName += TabName.SINGLE_SUBJECT_TIMELINE;
                    break;
                case 'summary-tab':
                    options.showEventFilter = false;
                    options.filterId = FilterId.POPULATION;
                    options.pageName += TabName.SINGLE_SUBJECT_SUMMARY;
                    break;
                case 'new-summary-tab':
                    options.showEventFilter = false;
                    options.filterId = FilterId.POPULATION;
                    options.pageName += TabName.SINGLE_SUBJECT_NEW_SUMMARY;
                    break;
                case 'ae-tab':
                    options.eventWidgetName = 'Adverse Events';
                    options.eventFiltersName = 'Adverse Event Filters';
                    options.filterId = FilterId.AES;
                    options.pageName += TabName.SINGLE_SUBJECT_AES;
                    break;
                case 'sae-tab':
                    options.eventWidgetName = 'Serious Adverse Events';
                    options.eventFiltersName = 'Serious Ae Filters';
                    options.filterId = FilterId.SAE;
                    options.pageName += TabName.SINGLE_SUBJECT_SAES;
                    break;
                case 'conmeds-tab':
                    options.eventWidgetName = 'Conmed Counts';
                    options.eventFiltersName = 'Conmeds Filters';
                    options.filterId = FilterId.CONMEDS;
                    options.pageName += TabName.SINGLE_SUBJECT_CONMEDS;
                    break;
                case 'vitals-tab':
                    options.eventWidgetName = 'Vitals Measurements';
                    options.eventFiltersName = 'Vitals Filters';
                    options.filterId = FilterId.VITALS;
                    options.pageName += TabName.SINGLE_SUBJECT_VITALS;
                    break;
                case 'lab-tab':
                    options.eventWidgetName = 'Lab Measurements';
                    options.eventFiltersName = 'Lab Filters';
                    options.filterId = FilterId.LAB;
                    options.pageName += TabName.SINGLE_SUBJECT_LABS;
                    break;
                case 'cardiac-tab':
                    options.eventWidgetName = 'Cardiac Function Measurements';
                    options.eventFiltersName = 'Cardiac Filters';
                    options.filterId = FilterId.CARDIAC;
                    options.pageName += TabName.SINGLE_SUBJECT_CARDIAC;
                    break;
                case 'renal-tab':
                    options.eventWidgetName = 'Renal Function Measurements';
                    options.eventFiltersName = 'Renal Filters';
                    options.filterId = FilterId.RENAL;
                    options.pageName += TabName.SINGLE_SUBJECT_RENAL;
                    break;
                case 'hys-law':
                    options.eventWidgetName = 'Liver Function Measurements';
                    options.eventFiltersName = 'Liver Filters';
                    options.filterId = FilterId.LIVER;
                    options.pageName += TabName.SINGLE_SUBJECT_LIVER;
                    break;
                case 'lungfunction-tab':
                    options.eventWidgetName = 'Lung Function Measurements';
                    options.eventFiltersName = 'Lung Function Filters';
                    options.filterId = FilterId.LUNG_FUNCTION;
                    options.pageName += TabName.SINGLE_SUBJECT_LUNG_FUNCTION;
                    break;
                case 'exacerbations-tab':
                    options.eventWidgetName = 'Respiratory Measurements';
                    options.eventFiltersName = 'Exacerbations Filters';
                    options.filterId = FilterId.EXACERBATIONS;
                    options.pageName += TabName.SINGLE_SUBJECT_RESPIRATORY;
                    break;
                case 'death-tab':
                    options.eventWidgetName = 'Death';
                    options.eventFiltersName = 'Death Filters';
                    options.filterId = FilterId.DEATH;
                    options.pageName += TabName.SINGLE_SUBJECT_DEATH;
                    break;
                case 'dose-tab':
                    options.eventWidgetName = 'Dose';
                    options.eventFiltersName = 'Dose Filters';
                    options.filterId = FilterId.DOSE;
                    options.pageName += TabName.SINGLE_SUBJECT_DOSE;
                    break;
                case 'dose-discontinuation-tab':
                    options.eventWidgetName = 'Dose Discontinuation';
                    options.eventFiltersName = 'Dose Discontinuation Filters';
                    options.filterId = FilterId.DOSE_DISCONTINUATION;
                    options.pageName += TabName.SINGLE_SUBJECT_DOSE_DISCONTINUATION;
                    break;
                case 'medicalhistory-tab':
                    options.eventWidgetName = 'Medical History';
                    options.eventFiltersName = 'Medical History Filters';
                    options.filterId = FilterId.MEDICAL_HISTORY;
                    options.pageName += TabName.SINGLE_SUBJECT_MEDICAL_HISTORY;
                    break;
                case 'liverdiag-tab':
                    options.eventWidgetName = 'Liver Diagnostic Inv.';
                    options.eventFiltersName = 'Liver Diagnostic Inv. Filters';
                    options.filterId = FilterId.LIVER_DIAGNOSTIC_INVESTIGATION;
                    options.pageName += TabName.SINGLE_SUBJECT_LIVER_DIAGNOSTIC_INVESTIGATION;
                    break;
                case 'liver-risk-factors-tab':
                    options.eventWidgetName = 'Liver Risk Factors';
                    options.eventFiltersName = 'Liver Risk Factors Filters';
                    options.filterId = FilterId.LIVER_RISK_FACTORS;
                    options.pageName += TabName.SINGLE_SUBJECT_LIVER_RISK_FACTORS;
                    break;
                case 'alcohol-tab':
                    options.eventWidgetName = 'Substance Use - Alcohol';
                    options.eventFiltersName = 'Subs. Use - Alcohol';
                    options.filterId = FilterId.ALCOHOL;
                    options.pageName += TabName.SINGLE_SUBJECT_ALCOHOL;
                    break;
                case 'surgicalhistory-tab':
                    options.eventWidgetName = 'Surgical History';
                    options.eventFiltersName = 'Surgical History Filters';
                    options.filterId = FilterId.SURGICAL_HISTORY;
                    options.pageName += TabName.SINGLE_SUBJECT_SURGICAL_HISTORY;
                    break;
                case 'nicotine-tab':
                    options.eventWidgetName = 'Substance Use - Nicotine';
                    options.eventFiltersName = 'Subs. Use - Nicotine';
                    options.filterId = FilterId.NICOTINE;
                    options.pageName += TabName.SINGLE_SUBJECT_NICOTINE;
                    break;
                default:
                    break;
            }
            break;
        default:
            options.showEventFilter = false;
            options.showPopulationFilter = false;
            options.eventFiltersModel = null;
            options.eventFiltersName = '';
            break;
    }

    /*
     Check if property by key page is an object then get value by tab name
     if it's a string - get immediate value
     if none - return an empty string
     */
    tabName = isObject(urlToTabNameMap[page]) ? urlToTabNameMap[page][tab] :
        isString(urlToTabNameMap[page]) ? urlToTabNameMap[page] : '';

    options.pageName += tabName ? ` -> ${tabName}` : '';

    return options;
}

/**
 * Downloads cvs data
 */
export function downloadData(fileName: string, data: any): void {
    const blob = new Blob([data], {type: 'text/csv'});
    const elem = window.document.createElement('a');
    elem.href = window.URL.createObjectURL(blob);
    elem.download = fileName;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
}


/**
 * Downloads .doc file
 */
export function downloadDoc(fileName: string, data: any): void {
    const blob = new Blob([data], {type: 'application/msword'});
    const elem = window.document.createElement('a');
    elem.href = window.URL.createObjectURL(blob);
    elem.download = `${fileName}.doc`;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
}

/**
 *
 * @param res - Response from server in json format
 * should be iterable
 * @returns {Map<K, Set<string>>}
 *
 * e.g. using
 *      getColumnNames({
 *          aes: { detailsOnDemandColumns: [] }
 *          population: { },
 *          liver: { detailsOnDemandColumns: ['studyId']}
 *      })
 * will return
 *      Map({
 *          aes: Set.of()
 *          population: Set.of(),
 *          liver: Set.of('studyId')
 *      })
 */
export function getColumnNames(res: any): Map<any, Set<any>> {
    return Map(res)
        .map((metadataValue: any): string[] | undefined => metadataValue.detailsOnDemandColumns)
        .map((columnNames): Set<string> => {
            if (isUndefined(columnNames)) {
                return <Set<string>>Set();
            }

            return Set(columnNames);
        }).toMap(); // Remove once we update the typings from immutable
}

export function hashCodeFromString(str: string): number {
    let hash = 0;

    if (!str || str.length === 0) {
        return hash;
    }

    for (let i = 0; i < str.length; i++) {
        const char = str.charCodeAt(i);
        hash = ((hash * 32) - hash) + char;
    }

    return hash;
}

export function alphaNumSorting(prop: string): any {
    const property = prop;
    return function (objectA: any, objectB: any): number {
        const a = isNaN(Number(objectA[property])) ? objectA[property] : Number(objectA[property]);
        const b = isNaN(Number(objectB[property])) ? objectB[property] : Number(objectB[property]);
        if (a === b) {
            return 0;
        }
        if (typeof a === typeof b) {
            return a < b ? -1 : 1;
        }
        return typeof a < typeof b ? -1 : 1;
    };
}

/**
 * Formats Date() getTimezoneOffset method result from number to string time zone like +00:00
 * @param timezone - current timezone as number
 * @returns {string}
 */
export function getFormattedTimeZone(timezone: number): string {
    const hours = -timezone / 60;
    const absHours = Math.floor(Math.abs(hours));
    const sign = hours >= 0 ? '+' : '-';
    const hoursFormatted = absHours.toString().length === 1 ? `${sign}0${absHours}` : `${sign}${Math.floor(absHours)}`;
    const minutesFormatted = timezone % 60 === 0 ? '00' : Math.abs(timezone) % 60;
    return `${hoursFormatted}:${minutesFormatted}`;
}

/**
 * Iterates over object's fields and parses any string into number if applicable.
 * Handles self-referential objects.
 * Fixes DoD table pivot calculations
 * @param obj DoD or any other object to convert numbers from string to number type.
 */
export function parseNumericalFields(obj: Object): Object {
    const traversed = new WeakSet<Object>();
    return innerParseNumericalFields(obj, traversed);
}

function innerParseNumericalFields(obj: Object, traversed: WeakSet<Object>) {
    if (isNumeric(obj)) {
        return +obj;
    } else if (typeof obj === 'object' && obj !== null && !traversed.has(obj)) {
        traversed = traversed.add(obj); // keep track of traversed objects in case of self reference and avoid infinite recursion
        Object.keys(obj).forEach(key => obj[key] = innerParseNumericalFields(obj[key], traversed));
    }
    return obj;
}
/**
 * Downloads file where blob is response._body
 */

export function downloadFile(fileName: string, type: string, blob: string) {
    const elem = window.document.createElement('a');
    elem.href = blob;
    elem.download = `${fileName}.${type}`;
    document.body.appendChild(elem);
    elem.click();
    document.body.removeChild(elem);
}
