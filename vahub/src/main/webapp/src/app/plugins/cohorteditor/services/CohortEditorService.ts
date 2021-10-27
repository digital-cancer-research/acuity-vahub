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
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import * as  _ from 'lodash';

import {getServerPath} from '../../../common/utils/Utils';
import {BaseFilterItemModel} from '../../../filters/module';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {FilterId} from '../../../common/trellising/store';
import {FilterReloadService} from './FilterReloadService';
import {SaveCohortDto} from '../dto/SaveCohortDto';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {PopulationFiltersModel} from '../../../filters/dataTypes/population/PopulationFiltersModel';
import SavedFilterVO = Request.SavedFilterVO;
import UserVO = Request.UserVO;

@Injectable()
export class CohortEditorService {

    constructor(private http: HttpClient,
                private session: SessionEventService,
                private globalPopulationFilters: PopulationFiltersModel,
                private filterEventService: FilterEventService,
                private timelineDispatcher: TimelineDispatcher,
                private trellisingDispatcher: TrellisingDispatcher,
                private filterReloadService: FilterReloadService) {
    }

    updateNumberOfSubjectsInSelectedFilters(): void {
        this.globalPopulationFilters.getFilters();
    }

    getCohorts(): Observable<SavedFilterVO[]> {
        const path = getServerPath('cohorteditor', 'savefilters/list');
        const requestBody = {
            datasets: this.session.currentSelectedDatasets
        };
        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as SavedFilterVO[]);
    }

    getAllDatasetUsers(): Observable<UserVO[]> {
        const path = getServerPath('cohorteditor', 'dataset-users');
        const requestBody = {
            datasets: this.session.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as UserVO[]);
    }

    saveCohort(saveCohortDto: SaveCohortDto): Observable<SavedFilterVO[]> {
        const path = getServerPath('cohorteditor', 'savefilters');
        const requestBody = this.getSaveRequestBody(saveCohortDto);

        return this.http.post(path, requestBody).map(res => res as SavedFilterVO[]);
    }

    applyCohort(savedFilterId: number, cohortName: string): Observable<void> {
        const path = getServerPath('cohorteditor', 'getsubjects');
        const requestBody = {
            datasets: this.session.currentSelectedDatasets,
            savedFilterId: savedFilterId
        };

        return this.http.post(path, JSON.stringify(requestBody))
            .map((subjectIds) => {
                this.globalPopulationFilters.getFilters(false, true);
                this.globalPopulationFilters.removeCohortFilter([cohortName], false);
                this.addCohortSubjectsToPopulationFilter(subjectIds, cohortName);
                this.globalPopulationFilters.toggleSubjectIdFilterVisibility(false);
                this.trellisingDispatcher.localResetNotification(FilterId.POPULATION);
                this.timelineDispatcher.globalResetNotification();
            });
    }

    deleteCohort(id: number, cohortName: string): Observable<SavedFilterVO[]> {
        const path = getServerPath('cohorteditor', 'savefilters/delete');
        const requestBody = {
            savedFilterId: id,
            datasets: this.session.currentSelectedDatasets
        };

        this.globalPopulationFilters.removeCohortFilter([cohortName], true);
        if (this.globalPopulationFilters.hasCohortSelected(cohortName)) {
            // Need to relaod to make sure that the SSV works correctly
            this.filterReloadService.resetFilters(this.globalPopulationFilters);
        }
        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as SavedFilterVO[]);
    }

    clearAllFilters(populationFiltersModel: PopulationFiltersModel): void {
        _.each(populationFiltersModel.itemsModels, (filter: BaseFilterItemModel) => {
            filter.clear();
            filter.resetNumberOfSelectedFilters();
        });
    }

    renameCohortIfApplied(oldName: string, newName: string): void {
        if (!_.isEmpty(oldName) && !_.isEmpty(this.globalPopulationFilters.getCohortEditorFilters())) {
            this.globalPopulationFilters.renameCohortIfApplied(oldName, newName);
        }
    }

    private getSaveRequestBody(saveCohortDto: SaveCohortDto): string {

        const populationFilterJson = {
            id: saveCohortDto.populationFilterId,
            filterView: 'POPULATION',
            json: JSON.stringify(saveCohortDto.localPopulationFiltersModel.transformFiltersToServer(true))
        };
        const aeFilterJson = {
            id: saveCohortDto.aeFilterId,
            filterView: 'AES',
            json: JSON.stringify(saveCohortDto.localAeFiltersModel.transformFiltersToServer(true))
        };

        const json = {
            datasets: this.session.currentSelectedDatasets,
            savedFilterVO: {
                savedFilter: {
                    id: saveCohortDto.cohortId,
                    name: saveCohortDto.cohortName,
                    owner: this.session.userDetails.userId
                },
                cohortFilters: [],
                sharedWith: saveCohortDto.shareWith
            }
        };

        if (populationFilterJson.json !== '{}') {
            json.savedFilterVO.cohortFilters.push(populationFilterJson);
        }

        if (aeFilterJson.json !== '{}') {
            json.savedFilterVO.cohortFilters.push(aeFilterJson);
        }

        return JSON.stringify(json);
    }

    private addCohortSubjectsToPopulationFilter(subjectIds: any, cohortName: string): void {
        this.globalPopulationFilters.addCohortFilter(subjectIds, cohortName);
        const totalSubjectsCount = this.getSubjectCountInAllAppliedCohorts();
        this.globalPopulationFilters.matchedItemsCount = totalSubjectsCount;
        this.filterEventService.setPopulationFilterSubjectCount(totalSubjectsCount);
    }

    private getSubjectCountInAllAppliedCohorts(): number {
        const allSubjects = _.map(this.globalPopulationFilters.getCohortEditorFilters(), 'selectedValues');
        const intersectOfSubjects = _.reduce(allSubjects, (r: string[], a: string[]) => _.intersection(r, a));
        return intersectOfSubjects ? intersectOfSubjects.length : 0;
    }
}
