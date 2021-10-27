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

import {Action} from '@ngrx/store';
import {fromJS, List, Map} from 'immutable';
import {
    ActionTypes, UpdateAvailableStudiesAction, UpdateLoadingStateAction,
    UpdateSearchStringAction, UpdateSelectedDatasetsAction
} from '../actions/AvailableStudiesActions';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {TextUtils} from '../../../../common/utils/TextUtils';
import {createSelector} from 'reselect';
import * as _ from 'lodash';
import {StudyListService} from '../../../../studylist/StudyListService';
import {ActionWithPayload} from '../../../../common/trellising/store/actions/TrellisingActionCreator';

export interface AvailableStudies extends Map<string, any> {
    selectedDatasetIds: List<number>;
    combinedStudyInfo: Map<string, any>;
    loading: boolean;
    searchString: string;
}

const initialState = <AvailableStudies>fromJS({
    selectedDatasets: fromJS([]),
    combinedStudyInfo: fromJS({
        roisWithPermission: [],
        studySelectionDatasetInfo: [],
        studyWarnings: [],
        counts: {}
    }),
    loading: true,
    searchString: ''
});

export function reducer(state: AvailableStudies = initialState, action: ActionWithPayload<any>): AvailableStudies {
    switch (action.type) {
        case ActionTypes.UPDATE_AVAILABLE_STUDIES_LIST:
            return updateAvailableStudiesList(state, action);
        case ActionTypes.UPDATE_SEARCH_STRING_ACTION:
            return updateSearchString(state, action);
        case ActionTypes.UPDATE_LOADING_STATE:
            return updateLoadingState(state, action);
        case ActionTypes.UPDATE_SELECTED_DATASETS_ACTION:
            return updateSelectedDatasets(state, action);
        default:
            return state;
    }

    function updateAvailableStudiesList(state, action: UpdateAvailableStudiesAction): AvailableStudies {
        return state.withMutations(state => {
            state.set('combinedStudyInfo', fromJS(action.payload));
            state.set('searchString', '');
            state.set('loading', false);
        });
    }

    function updateSearchString(state, action: UpdateSearchStringAction): AvailableStudies {
        return state.set('searchString', action.payload);
    }

    function updateLoadingState(state, action: UpdateLoadingStateAction): AvailableStudies {
        return state.set('loading', action.payload);
    }

    function updateSelectedDatasets(state, action: UpdateSelectedDatasetsAction): AvailableStudies {
        return state.withMutations(state => {
            let selectedDatasetList = state.get('selectedDatasets');
            const selectedDrugProgramme = selectedDatasetList.isEmpty() ? '' : selectedDatasetList.first().get('drugProgramme'),
                  selectedDataset = action.payload;

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

            state.set('selectedDatasets', selectedDatasetList);
        });
    }
}

export const getStudySelection = (state: ApplicationState): AvailableStudies => {
    return state.availableStudies;
};

export const getSearchString = createSelector(getStudySelection, (studySelection) => studySelection.get('searchString'));

export const getLoadingState = createSelector(getStudySelection, (studySelection) => studySelection.get('loading'));

export const getSelectedDatasets = createSelector(getStudySelection, (studySelection) => {
    return studySelection.get('selectedDatasets');
});

// export const getSelectedDrugProgramme = createSelector(getSelectedDatasets, (selectedDatasets) => {
//     return _.head(selectedDatasets.drugProgramme);
// });

export const getSelectedDatasetIds = createSelector(getSelectedDatasets, (selectedDatasets: any) => {
    return selectedDatasets.map((selectedDataset: Map<string, any>): any => {
        return selectedDataset.get('id');
    });
});

export const getCombinedStudyInfo = createSelector(getStudySelection, (studySelection) => studySelection.get('combinedStudyInfo'));

export const getCounts = createSelector(getCombinedStudyInfo, (combinedStudyInfo) => {
    return combinedStudyInfo.get('counts')
        .mapEntries(([k, v]) => [_.last(k.split('.')), v]);
});

export const getRois = createSelector(getCombinedStudyInfo, (combinedStudyInfo) => combinedStudyInfo.get('roisWithPermission'));

export const getStudySelectionDatasetInfo = createSelector(getCombinedStudyInfo, (combinedStudyInfo: any) => {
    return combinedStudyInfo.get('studySelectionDatasetInfo');
});

export const getStudySelectionMap = createSelector(getStudySelectionDatasetInfo, (studySelectionDatasetInfo) => {
    return Map(studySelectionDatasetInfo.map(info => [info.get('datasetId'), info]));
});

export const getDatasets = createSelector(
    getRois, getSearchString, getStudySelectionMap,
    (rois: any, searchString: any, studySelectionMap: any) => {
        const a = rois
            .filter((dataset: Map<string, any>) => {
                return !_.isEmpty(dataset.get('drugProgramme')) && !_.isEmpty(dataset.get('clinicalStudyName')) && !_.isEmpty(dataset.get('name')) &&
                    TextUtils.contains([
                        dataset.get('drugProgramme'),
                        dataset.get('clinicalStudyName'),
                        dataset.get('name'),
                        StudyListService.getNumberOfDosedSubjects(dataset.get('id'), studySelectionMap),
                        studySelectionMap.get(dataset.get('id')).get('dataCutoffDate')],
                        searchString);
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
