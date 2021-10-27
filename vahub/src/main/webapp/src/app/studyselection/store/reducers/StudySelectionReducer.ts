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

import {fromJS, List, Map} from 'immutable';
import {
    ActionTypes,
    UpdateCombinedStudyInfoAction,
    UpdateLoadingStateAction,
    UpdateSearchStringAction,
    UpdateSelectedDatasetsAction
} from '../actions/StudySelectionActions';
import {createSelector} from 'reselect';
import * as _ from 'lodash';
import {TextUtils} from '../../../common/utils/TextUtils';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {StudyListService} from '../../../studylist/StudyListService';
import {VACumulativePermissions} from '../../../security/VAPermissionEvalutator';
import {ActionWithPayload} from '../../../common/trellising/store/actions/TrellisingActionCreator';
import Dataset = Request.Dataset;

export interface StudySelection extends Map<string, any> {
    selectedDatasetIds: List<number>;
    combinedStudyInfo: Map<string, any>;
    loading: boolean;
    searchString: string;
}

const initialState = <StudySelection>fromJS({
    selectedDatasets: fromJS(<Dataset[]>[]),
    combinedStudyInfo: fromJS({
        roisWithPermission: [],
        studySelectionDatasetInfo: [],
        studyWarnings: []
    }),
    loading: true,
    searchString: ''
});

export function reducer(state: StudySelection = initialState, action: ActionWithPayload<any>): Map<string, any> {
    switch (action.type) {
        case ActionTypes.UPDATE_SELECTED_DATASETS:
            return updateSelectedDataset(state, action);
        case ActionTypes.UPDATE_COMBINED_STUDY_INFO:
            return updateStudyInfo(state, action);
        case ActionTypes.UPDATE_LOADING_STATE:
            return updateLoadingState(state, action);
        case ActionTypes.UPDATE_SEARCH_STRING:
            return updateSearchString(state, action);
        default:
            return state;
    }

    function updateSelectedDataset(state1: StudySelection, action1: UpdateSelectedDatasetsAction): Map<string, any> {
        if (action1.payload.replace) {
            const datasets = action1.payload.dataset ? List([action1.payload.dataset]) : List([]);
            return state1.set('selectedDatasets', datasets);
        } else {
            return state1.withMutations(state2 => {
                let selectedDatasetList = state2.get('selectedDatasets');
                const availableDatasetList = state2.getIn(['combinedStudyInfo', 'roisWithPermission']),
                    selectedDrugProgramme = selectedDatasetList.isEmpty() ? '' : selectedDatasetList.first().get('drugProgramme'),
                    selectedDataset = action1.payload.dataset;

                if (selectedDatasetList.isEmpty()) {
                    selectedDatasetList.push(selectedDataset);
                }

                if (selectedDrugProgramme !== selectedDataset.get('drugProgramme')) {
                    selectedDatasetList = selectedDatasetList.clear();
                }

                const index = selectedDatasetList.indexOf(selectedDataset);
                if (index === -1) {
                    selectedDatasetList = selectedDatasetList.push(selectedDataset);
                } else {
                    selectedDatasetList = selectedDatasetList.remove(index);
                }

                state2.set('selectedDatasets', selectedDatasetList);
            });
        }
    }

    function updateStudyInfo(state1: StudySelection, action1: UpdateCombinedStudyInfoAction): Map<string, any> {
        return state1.withMutations(state2 => {
            state2.set('combinedStudyInfo', fromJS(action1.payload));
            state2.set('selectedDatasets', List<Dataset>());
            state2.set('searchString', '');
            state2.set('loading', false);
        });
    }

    function updateLoadingState(state1, action1: UpdateLoadingStateAction): Map<string, any> {
        return state1.set('loading', action1.payload);
    }

    function updateSearchString(state1, action1: UpdateSearchStringAction): Map<string, any> {
        return state1.set('searchString', action1.payload);
    }
}

export const getStudySelection = (state: ApplicationState): StudySelection => {
    return state.studySelection;
};

export const getSearchString = createSelector(getStudySelection, (studySelection) => studySelection.get('searchString'));

export const getLoadingState = createSelector(getStudySelection, (studySelection) => studySelection.get('loading'));

export const getSelectedDatasets = createSelector(getStudySelection, (studySelection) => {
    return studySelection.get('selectedDatasets');
});

export const getSelectedDrugProgramme = createSelector(getSelectedDatasets, (selectedDatasets) => {
    return selectedDatasets.isEmpty() ? '' : selectedDatasets.first().get('drugProgramme');
});

export const getSelectedDatasetIds = createSelector(getSelectedDatasets, (selectedDatasets: any) => {
    return selectedDatasets.map((selectedDataset: Map<string, any>): any => {
        return selectedDataset.get('id');
    });
});

export const getCombinedStudyInfo = createSelector(getStudySelection, (studySelection) => studySelection.get('combinedStudyInfo'));

export const getRois = createSelector(getCombinedStudyInfo, (combinedStudyInfo) => combinedStudyInfo.get('roisWithPermission'));

export const getStudySelectionDatasetInfo = createSelector(getCombinedStudyInfo, (combinedStudyInfo: any) => {
    return combinedStudyInfo.get('studySelectionDatasetInfo');
});

export const getStudySelectionMap = createSelector(getStudySelectionDatasetInfo, (studySelectionDatasetInfo) => {
    return Map(studySelectionDatasetInfo.map(info => [info.get('datasetId'), info]));
});

export const getDatasets = createSelector(
    getRois, getSearchString, getStudySelectionMap,
    (rois: any, searchString: any, studySelectionMap: any): any => {
        const a = rois
            .filter((dataset: Map<string, any>) => {
                return !_.isEmpty(dataset.get('drugProgramme'))
                    && !_.isEmpty(dataset.get('clinicalStudyName'))
                    && !_.isEmpty(dataset.get('name'))
                    && TextUtils.contains([
                        dataset.get('drugProgramme'),
                        dataset.get('clinicalStudyName'),
                        dataset.get('name'),
                        StudyListService.getNumberOfDosedSubjects(dataset.get('id'), studySelectionMap),
                        studySelectionMap.getIn([dataset.get('id'), 'dataCutoffDate'])
                    ], searchString);
            })
            .groupBy((dataset: Map<string, any>) => {
                return dataset.get('drugProgramme');
            })
            .sortBy((value, key) => key.toLowerCase())
            .map((dataset: Map<string, any>) => {
                return dataset
                    .groupBy(value => value.get('clinicalStudyName'))
                    .sortBy((v, k) => k.toLowerCase());
            });
        return a;
    });

export const getStudyWarnings = createSelector(getCombinedStudyInfo, (combinedStudyInfo: any) => {
    return Map(combinedStudyInfo.get('studyWarnings')
        .filter(w => w.get('blinded') || w.get('forRegulatoryPurposes') || w.get('randomised'))
        .map(warning => {
            return Map({
                studyId: warning.get('studyId'),
                value: StudyListService.getWarnings(warning)
            });
        })
        .map(warning => [warning.get('studyId'), warning]));
});

export const getNumberOfSubjects = createSelector(
    getSelectedDatasetIds, getStudySelectionMap,
    (selectedDatasetIds: any, studySelectionMap: any) => {
        if (!selectedDatasetIds.isEmpty() && !studySelectionMap.isEmpty()) {
            return selectedDatasetIds.reduce((numberOfSubjects, datasetId: number) => {
                numberOfSubjects += StudyListService.getNumberOfDosedSubjects(datasetId, studySelectionMap);
                return numberOfSubjects;
            }, 0);
        }
        return 0;
    });

export const isExportAvailable = createSelector(getRois, (rois) => {
    return rois.some((roi: Map<string, any>) => {
        return roi.get('rolePermissionMask') === VACumulativePermissions.DEVELOPMENT_TEAM;
    });
});
