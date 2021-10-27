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
import {TabId} from '../../../../common/trellising/store';
import {Action} from '@ngrx/store';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {createSelector} from 'reselect';
import {
    ActionTypes,
    ClearSubjectSelection,
    UpdateOpenedTabData,
    UpdateSelectedSubject,
    UpdateSubjectSearchString,
    UpdateSummaryTabData,
    UpdateSummaryTablesData,
    UpdateSummaryTablesHeaderData,
    UpdateSummaryTabMetadata,
    UpdateTableConfig
} from '../actions/SingleSubjectViewActions';
import {getAvailableSubjects, getTabId} from '../../../../common/store/reducers/SharedStateReducer';
import {ColDef} from 'ag-grid/main';
import {DoseColumnModel} from '../../datatypes/dose/DoseColumnModel';
import {DoseDiscontinuationColumnModel} from '../../datatypes/dose-discontinuation/DoseDiscontinuationColumnModel';
import {AdverseEventsColumnModel} from '../../datatypes/adverse-events/AdverseEventsColumnModel';
import {AlcoholColumnModel} from '../../datatypes/alcohol/AlcoholColumnModel';
import {ConmedsColumnModel} from '../../datatypes/conmeds/ConmedsColumnModel';
import {SurgicalHistoryColumnModel} from '../../datatypes/surgical-history/SurgicalHistoryColumnModel';
import {SeriousAeColumnModel} from '../../datatypes/serious-adverse-events/SeriousAeColumnModel';
import {NicotineColumnModel} from '../../datatypes/nicotine/NicotineColumnModel';
import {MedicalHistoryColumnModel} from '../../datatypes/medical-history/MedicalHistoryColumnModel';
import {LiverRiskFactorsColumnModel} from '../../datatypes/liver-risk-factors/LiverRiskFactorsColumnModel';
import {LiverDiagnosticInvestigationColumnModel} from '../../datatypes/liver-diag-invest/LiverDiagnosticInvestigationColumnModel';
import {ExacerbationsColumnModel} from '../../datatypes/exacerbations/ExacerbationsColumnModel';
import {DeathColumnModel} from '../../datatypes/death/DeathColumnModel';
import {CardiacColumnModel} from '../../datatypes/cardiac/CardiacColumnModel';
import {VitalsColumnModel} from '../../datatypes/vitals/VitalsColumnModel';
import {LabsColumnModel} from '../../datatypes/labs/LabsColumnModel';
import {RenalColumnsModel} from '../../datatypes/renal/RenalColumnsModel';
import {LungFunctionColumnModel} from '../../datatypes/lungfunction/LungFunctionColumnModel';
import {getAvailableDetailsOnDemandColumns} from '../../../../common/trellising/detailsondemand/store/reducers/DetailsOnDemandReducer';
import {ActionWithPayload} from '../../../../common/trellising/store/actions/TrellisingActionCreator';
import {ColGroupDef} from 'ag-grid';

export const SingleSubjectViewTableConfigRecord = Record({
    isToolPanelShown: false
});

/**
 * contans inital state of each basic tab in single subject view
 * @type {Record.Class}
 */
export const SingleSubjectViewTabStateRecord = Record({
    tabData: List(),
    columnDefs: List<any>(),
    hasTrellising: false,
    hasTable: true,
    tableConfig: new SingleSubjectViewTableConfigRecord()
});

export const SingleSubjectNewSummaryTabStateRecord = Record({
    tables: Map<string, any>(),
    header: Map<string, any>(),
    metadata: []
});

export const SingleSubjectSummaryTabStateRecord = Record({
    metadata: Map<string, any>(),
    details: Map<string, any>()
});

export const SingleSubjectTabsStateRecord = Record({
    SINGLE_SUBJECT_DOSE_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new DoseColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new DoseDiscontinuationColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_AE_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new AdverseEventsColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_CONMEDS_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new ConmedsColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_DEATH_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new DeathColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_EXACERBATIONS_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new ExacerbationsColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new LiverDiagnosticInvestigationColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new LiverRiskFactorsColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_MEDICAL_HISTORY_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new MedicalHistoryColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_NICOTINE_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new NicotineColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_SAE_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new SeriousAeColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_SURGICAL_HISTORY_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new SurgicalHistoryColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_ALCOHOL_TAB: new SingleSubjectViewTabStateRecord({
        columnDefs: new AlcoholColumnModel().columnDefs
    }),
    SINGLE_SUBJECT_CARDIAC_LINEPLOT: new SingleSubjectViewTabStateRecord({
        columnDefs: new CardiacColumnModel().columnDefs,
        hasTrellising: true
    }),
    SINGLE_SUBJECT_VITALS_LINEPLOT: new SingleSubjectViewTabStateRecord({
        columnDefs: new VitalsColumnModel().columnDefs,
        hasTrellising: true
    }),
    SINGLE_SUBJECT_LAB_LINEPLOT: new SingleSubjectViewTabStateRecord({
        columnDefs: new LabsColumnModel().columnDefs,
        hasTrellising: true
    }),
    SINGLE_SUBJECT_LIVER_HYSLAW: new SingleSubjectViewTabStateRecord({
        hasTrellising: true,
        hasTable: false
    }),
    SINGLE_SUBJECT_RENAL_LINEPLOT: new SingleSubjectViewTabStateRecord({
        hasTrellising: true,
        columnDefs: new RenalColumnsModel().columnDefs
    }),
    SINGLE_SUBJECT_LUNG_LINEPLOT: new SingleSubjectViewTabStateRecord({
        hasTrellising: true,
        columnDefs: new LungFunctionColumnModel().columnDefs
    }),

    SINGLE_SUBJECT_SUMMARY_TAB: new SingleSubjectSummaryTabStateRecord(),
    SINGLE_SUBJECT_NEW_SUMMARY_TAB: new SingleSubjectNewSummaryTabStateRecord()
});

export const SingleSubjectStateRecord = Record({
    selectedSubjectId: '',
    subjectSearchString: '',
    tabs: new SingleSubjectTabsStateRecord(),
    isLoading: false
});

export class SingleSubjectTabsState extends SingleSubjectTabsStateRecord {
    SINGLE_SUBJECT_DOSE_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_AE_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_CONMEDS_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_DEATH_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_EXACERBATIONS_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_MEDICAL_HISTORY_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_NICOTINE_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_SAE_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_SURGICAL_HISTORY_TAB: SingleSubjectViewTabState;
    SINGLE_SUBJECT_ALCOHOL_TAB: SingleSubjectViewTabState;

    SINGLE_SUBJECT_CARDIAC_LINEPLOT: SingleSubjectViewTabState;
    SINGLE_SUBJECT_VITALS_LINEPLOT: SingleSubjectViewTabState;
    SINGLE_SUBJECT_LAB_LINEPLOT: SingleSubjectViewTabState;
    SINGLE_SUBJECT_LIVER_HYSLAW: SingleSubjectViewTabState;
    SINGLE_SUBJECT_RENAL_LINEPLOT: SingleSubjectViewTabState;
    SINGLE_SUBJECT_LUNG_LINEPLOT: SingleSubjectViewTabState;

    SINGLE_SUBJECT_SUMMARY_TAB: any;
}

export class SingleSubjectState extends SingleSubjectStateRecord {
    selectedSubjectId: string;
    subjectSearchString: string;
    tabs: SingleSubjectTabsState;
    isLoading: boolean;
}

/**
 * Tab state
 * tabData - contains single subject table data
 * columnsDefs - contains column definitions (classes like <...>ColumnModel)
 * hasTrellising - tab has plot
 * hasTable - tab has ag-grid table
 */
export class SingleSubjectViewTabState extends SingleSubjectViewTabStateRecord {
    tabData: List<any>;
    columnDefs: List<any>;
    hasTrellising: boolean;
    hasTable: boolean;
}

const initialState: SingleSubjectState = new SingleSubjectState();

export function reducer(state: SingleSubjectState = initialState, action: ActionWithPayload<any>): Map<string, any> {
    switch (action.type) {
        case ActionTypes.UPDATE_SELECTED_SUBJECT:
            return updateSelectedSubject(state, action);
        case ActionTypes.UPDATE_SUBJECT_SEARCH_STRING:
            return updateSubjectSearchString(state, action);
        case ActionTypes.CLEAR_SUBJECT_SELECTION:
            return clearSubjectSelection(state, action);
        case ActionTypes.UPDATE_OPENED_TAB_DATA:
            return updateOpenedTabData(state, action);
        case ActionTypes.UPDATE_TABLE_CONFIG:
            return updateTableConfig(state, action);
        case ActionTypes.LOAD_DATA:
            return updateIsLoading(state, action, true);
        case ActionTypes.LOAD_DATA_SUCCESS:
            return updateIsLoading(state, action, false);
        case ActionTypes.UPDATE_SUMMARY_TAB_METADATA:
            return updateSummaryTabMetadata(state, action);
        case ActionTypes.UPDATE_SUMMARY_TAB_DATA:
            return updateSummaryTabData(state, action);
        case ActionTypes.UPDATE_SUBJECT_SUMMARY_TABLES_DATA:
            return updateSummaryTablesData(state, action);
        case ActionTypes.UPDATE_TABLES_HEADER:
            return updateSummaryTablesHeader(state, action);
        default:
            return state;
    }

    function updateSummaryTabData(subjectState: SingleSubjectState, updateAction: UpdateSummaryTabData): Map<string, any> {
        return subjectState.setIn(['tabs', TabId.SINGLE_SUBJECT_SUMMARY_TAB, 'details'], updateAction.payload);
    }

    function updateSummaryTabMetadata(subjectState: SingleSubjectState, updateAction: UpdateSummaryTabMetadata): Map<string, any> {
        return subjectState.setIn(['tabs', TabId.SINGLE_SUBJECT_SUMMARY_TAB, 'metadata'], updateAction.payload);
    }

    function updateSummaryTablesData(subjectState: SingleSubjectState, updateAction: UpdateSummaryTablesData): Map<string, any> {
        return subjectState.setIn(['tabs', TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB, 'tables'], updateAction.payload);
    }

    function updateSummaryTablesHeader(subjectState: SingleSubjectState, updateAction: UpdateSummaryTablesHeaderData): Map<string, any> {
        return subjectState.setIn(['tabs', TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB, 'header'], updateAction.payload);
    }

    function updateIsLoading(subjectState: SingleSubjectState, updateAction: Action, isLoading: boolean): Map<string, any> {
        return subjectState.set('isLoading', isLoading);
    }

    function updateOpenedTabData(subjectState: SingleSubjectState, updateAction: UpdateOpenedTabData): Map<string, any> {
        return subjectState.setIn(['tabs', updateAction.payload.tabId, 'tabData'], updateAction.payload.tableData);
    }

    function updateTableConfig(subjectState: SingleSubjectState, updateAction: UpdateTableConfig): Map<string, any> {
        return subjectState.setIn(['tabs', updateAction.payload.tabId, 'tableConfig'], updateAction.payload.tableConfig);
    }

    function updateSelectedSubject(subjectState: SingleSubjectState, updateAction: UpdateSelectedSubject): Map<string, any> {
        return subjectState.withMutations(singleSubjectState => {
            singleSubjectState.set('selectedSubjectId', updateAction.payload);
            singleSubjectState.set('subjectSearchString', updateAction.payload);
        });
    }

    function updateSubjectSearchString(subjectState: SingleSubjectState, updateAction: UpdateSubjectSearchString): Map<string, any> {
        return subjectState.set('subjectSearchString', updateAction.payload);
    }

    function clearSubjectSelection(subjectState: SingleSubjectState, updateAction: ClearSubjectSelection): Map<string, any> {
        return subjectState.withMutations(singleSubjectState => {
            singleSubjectState.set('selectedSubjectId', '');
            singleSubjectState.set('subjectSearchString', '');
        });
    }
}

export const getSingleSubjectViewState = (appState: ApplicationState): SingleSubjectState => {
    return appState.singleSubjectReducer;
};

export const getIsSingleSubjectViewDataLoading =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => singleSubjectState.get('isLoading'));

export const getSelectedSubjectId =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => singleSubjectState.get('selectedSubjectId'));

export const getSummaryTabMetadata =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => {
    return singleSubjectState.getIn(['tabs', TabId.SINGLE_SUBJECT_SUMMARY_TAB, 'metadata']);
});

export const getSummaryTabData =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => {
    return singleSubjectState.getIn(['tabs', TabId.SINGLE_SUBJECT_SUMMARY_TAB, 'details']);
});

export const getSummaryTablesData =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => {
    return singleSubjectState.getIn(['tabs', TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB, 'tables']);
});

export const getSummaryTablesHeader =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => {
    return singleSubjectState.getIn(['tabs', TabId.SINGLE_SUBJECT_NEW_SUMMARY_TAB, 'header']);
});

export const getSubjectSearchString =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => singleSubjectState.get('subjectSearchString'));

export const getSingleSubjectTabsState =
    createSelector(getSingleSubjectViewState, (singleSubjectState: SingleSubjectState) => {
    return singleSubjectState.get('tabs');
});

/**
 * returns filtered {@link SharedState.availableSubjects} by {@link SingleSubjectState.subjectSearchString}
 *
 */
export const getAvailableSubjectsBySearchString = createSelector(
    getAvailableSubjects, getSubjectSearchString,
    (subjects: List<string>, searchString: string): List<string> => {
        return subjects.filter((subjectId: string) => {
            return subjectId.indexOf(searchString) !== -1;
        }).toList();
    });

export const getSingleSubjectViewTabData = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string) => {
        return singleSubjectTabsState.getIn([tabId, 'tabData']);
    }
);

export const getSingleSubjectTableConfig = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string) => {
        return singleSubjectTabsState.getIn([tabId, 'tableConfig']);
    }
);

export const getSingleSubjectViewTabColumnDefs = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string): List<ColDef> => {
        return singleSubjectTabsState.getIn([tabId, 'columnDefs']);
    }
);

export const getSingleSubjectViewHasTrellising = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string) => {
        return singleSubjectTabsState.getIn([tabId, 'hasTrellising'], false);
    }
);

export const getSingleSubjectViewHasTable = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string) => {
        return singleSubjectTabsState.getIn([tabId, 'hasTable'], false);
    }
);

export const isSelectedTabPartOfSSV = createSelector(
    getSingleSubjectTabsState, getTabId,
    (singleSubjectTabsState: SingleSubjectTabsState, tabId: string) => {
        return !!singleSubjectTabsState.get(tabId);
    }
);

export const getFilteredDetailsOnDemandColumnsForTab = createSelector(
    getTabId, getSingleSubjectViewTabColumnDefs, getAvailableDetailsOnDemandColumns,
    (tabId: string, columnDefs: List<any>, availableColumns: Map<string, any>): any => {
        const tabStorePathByTabId = getTabStorePathByTabId(tabId);
        columnDefs = columnDefs.filter((column) => {
            return !!availableColumns.get(tabStorePathByTabId).find((availableColumn) => {
                if (!column.children) {
                    return availableColumn === column.field;
                } else  {
                    // always show first column
                    column.children[0].columnGroupShow = null;
                    return true;
            }
            });
        }).toList();
        const availableColumnGroupDefs = new Array<any>();
        columnDefs.forEach(column => {
            if (column.children) {
                 const properChildren = column.children.filter(child => {
                    return !!availableColumns.get(tabStorePathByTabId).find(availableColumn =>
                        availableColumn === child.field);
                });
                 addColumnGroupToTable(availableColumnGroupDefs, column, properChildren);
            } else {
                availableColumnGroupDefs.push(column);
            }
        });

        return availableColumnGroupDefs;
    }
);

export function getTabStorePathByTabId(tabId: TabId): string {
    let tabStorePathByTabId;
    switch (tabId) {
        case TabId.SINGLE_SUBJECT_DOSE_TAB:
            tabStorePathByTabId = 'dose';
            break;
        case TabId.SINGLE_SUBJECT_SUMMARY_TAB:
            tabStorePathByTabId = 'dose';
            break;
        case TabId.SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB:
            tabStorePathByTabId = 'doseDisc';
            break;
        case TabId.SINGLE_SUBJECT_AE_TAB:
            tabStorePathByTabId = 'aes';
            break;
        case TabId.SINGLE_SUBJECT_CONMEDS_TAB:
            tabStorePathByTabId = 'conmeds';
            break;
        case TabId.SINGLE_SUBJECT_DEATH_TAB:
            tabStorePathByTabId = 'death';
            break;
        case TabId.SINGLE_SUBJECT_EXACERBATIONS_TAB:
            tabStorePathByTabId = 'exacerbation';
            break;
        case TabId.SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB:
            tabStorePathByTabId = 'liverDiag';
            break;
        case TabId.SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB:
            tabStorePathByTabId = 'liverRisk';
            break;
        case TabId.SINGLE_SUBJECT_MEDICAL_HISTORY_TAB:
            tabStorePathByTabId = 'medicalHistory';
            break;
        case TabId.SINGLE_SUBJECT_NICOTINE_TAB:
            tabStorePathByTabId = 'nicotine';
            break;
        case TabId.SINGLE_SUBJECT_SAE_TAB:
            tabStorePathByTabId = 'seriousAe';
            break;
        case TabId.SINGLE_SUBJECT_SURGICAL_HISTORY_TAB:
            tabStorePathByTabId = 'surgicalHistory';
            break;
        case TabId.SINGLE_SUBJECT_ALCOHOL_TAB:
            tabStorePathByTabId = 'alcohol';
            break;
        case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
            tabStorePathByTabId = 'labs';
            break;
        case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
            tabStorePathByTabId = 'vitals-java';
            break;
        case TabId.SINGLE_SUBJECT_LIVER_HYSLAW:
            tabStorePathByTabId = 'liver';
            break;
        case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
            tabStorePathByTabId = 'renal-java';
            break;
        case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
            tabStorePathByTabId = 'cardiac';
            break;
        case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
            tabStorePathByTabId = 'lungFunction-java';
            break;
        default:
            tabStorePathByTabId = '';
    }
    return tabStorePathByTabId;
}

// keys should be the same as field name in metadata
export const SingleSubjectSummaryTablesUrls = Record({
    demography: 'demography',
    outcomeSummary: 'outcome-summary',
    pastMedicalHistory: 'past-medical-history',
    surgicalHistory: 'surgical-history',
    currentMedicalHistory: 'concurrent-conditions',
    conmeds: 'conmeds',
    pathgen: 'pathgen',
    disExt: 'disease-extent',
    pastChemotherapy: 'past-chemotherapy',
    radiotherapy: 'past-radiotherapy',
    labs: 'labs',
    drugDose: 'drug-dose',
    doseDisc: 'dosedisc',
    doseLimiting: 'dose-limiting',
    adverseEvents: 'aes',
    targetlesion: 'target-lesion',
    nontargetLesion: 'non-target-lesion',
    newLesion: 'assessment',
    postChemotherapy: 'post-chemotherapy',
    secondTimeOfProgression: 'second-time-of-progression',
    survivalStatus: 'survival-status'
});

export function addColumnGroupToTable(availableColumnGroupDefs: ColGroupDef[], columnGroup: ColGroupDef, columnDefs: ColDef[]): void {
    availableColumnGroupDefs.push({
    headerName: columnGroup.headerName,
    children: columnDefs,
});
}
