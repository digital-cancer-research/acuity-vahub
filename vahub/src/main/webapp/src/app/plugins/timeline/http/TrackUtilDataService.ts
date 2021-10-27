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
import {Response} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {
    PopulationFiltersModel,
    AesFiltersModel,
    ConmedsFiltersModel,
    DoseFiltersModel,
    CardiacFiltersModel,
    LabsFiltersModel,
    LungFunctionFiltersModel,
    VitalsFiltersModel,
    PatientDataFiltersModel,
    ExacerbationsFiltersModel
} from '../../../filters/dataTypes/module';
import {getServerPath} from '../../../common/utils/Utils';
import {ITrack} from '../store/ITimeline';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {TrackUtilDataTransformer} from './TrackUtilDataTransformer';
import * as _ from 'lodash';
import {DynamicAxis} from '../../../common/trellising/store';
import {List} from 'immutable';

/**
 * All the utility based data service methods
 */
@Injectable()
export class TrackUtilDataService {

    constructor(private http: HttpClient,
                private trackUtilDataTransformer: TrackUtilDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel,
                private aesFiltersModel: AesFiltersModel,
                private doseFiltersModel: DoseFiltersModel,
                private conmedsFilterModel: ConmedsFiltersModel,
                private cardiacFiltersModel: CardiacFiltersModel,
                private labsFiltersModel: LabsFiltersModel,
                private lungFunctionFiltersModel: LungFunctionFiltersModel,
                private vitalsFiltersModel: VitalsFiltersModel,
                private patientDataFiltersModel: PatientDataFiltersModel,
                private exacerbationsFiltersModel: ExacerbationsFiltersModel) {
    }

    fetchPossibleTracks(): Observable<ITrack[]> {
        if (this.sessionEventService.currentSelectedDatasets) {
            const path = getServerPath('timeline', 'available-tracks');

            const postData: any = {
                datasets: this.sessionEventService.currentSelectedDatasets
            };

            return this.http.post(path, JSON.stringify(postData))
                .map((response: Response) => {
                    return this.trackUtilDataTransformer.transformPossibleTrackData(response);
                });
        } else {
            return Observable.of([]);
        }
    }

    fetchSelectedSubjects(dayZero: DynamicAxis, tracks?: ITrack[], chosenSubjects?: string[]): Observable<string[]> {

        /* If from the single subject view, temporarily change population filters */
        let populationFilters: PopulationFiltersModel = this.populationFiltersModel;
        if (chosenSubjects) {
            populationFilters = _.cloneDeep(this.populationFiltersModel);
            _.find(populationFilters.itemsModels, {'displayName': 'Subject ID'})['appliedSelectedValues'] = chosenSubjects;
        }

        if (this.sessionEventService.currentSelectedDatasets) {
            const path = getServerPath('timeline', 'subjects');

            const trackNames: string[] = this.trackUtilDataTransformer.mapToOriginalTrackName(tracks);

            const postData: any = {
                populationFilters: populationFilters.transformFiltersToServer(),
                aesFilters: this.aesFiltersModel.transformFiltersToServer(),
                conmedsFilters: this.conmedsFilterModel.transformFiltersToServer(),
                doseFilters: this.doseFiltersModel.transformFiltersToServer(),
                cardiacFilters: this.cardiacFiltersModel.transformFiltersToServer(),
                labsFilters: this.labsFiltersModel.transformFiltersToServer(),
                lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer(),
                vitalsFilters: this.vitalsFiltersModel.transformFiltersToServer(),
                patientDataFilters: this.patientDataFiltersModel.transformFiltersToServer(),
                exacerbationsFilters: this.exacerbationsFiltersModel.transformFiltersToServer(),
                datasets: this.sessionEventService.currentSelectedDatasets,
                visibleTracks: trackNames,
                dayZero: dayZero.toJS()
            };

            return this.http.post(path, JSON.stringify(postData))
                .map((response: Response) => {
                    return this.trackUtilDataTransformer.transformSelectedSubjectIds(response);
                });
        } else {
            return Observable.of([]);
        }
    }

    hasRandomisationDate(): Observable<boolean> {
        if (this.sessionEventService.currentSelectedDatasets) {
            const path = getServerPath('timeline/subject', 'hasRandomisationDates');

            const postData: any = {
                datasets: this.sessionEventService.currentSelectedDatasets
            };

            return this.http.post(path, JSON.stringify(postData)).map(res => res as boolean);
        } else {
            return Observable.of(false);
        }
    }

    fetchDayZeroOptions(): Observable<List<DynamicAxis>> {
        const path = getServerPath('timeline', 'available-options');
        const postData = {
            datasets: this.sessionEventService.currentSelectedDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as List<DynamicAxis>);
    }
}
