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

import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {
    MockDatasetViews,
    MockEnvService,
    MockFilterEventService,
    MockSessionEventService
} from '../../../common/MockClasses';
import {EnvService} from '../../../env/module';

import {TrackDataService} from './TrackDataService';
import {DayZero, ISubject, ITrack, SubjectRecord, TrackName, TrackRecord} from '../store/ITimeline';
import {TrackRequest} from './IDataService';
import {TrackUtilDataService} from './TrackUtilDataService';
import {StatusTrackDataService} from './status/StatusTrackDataService';
import {AesTrackDataService} from './aes/AesTrackDataService';


import {AesTrackDataTransformer} from './aes/AesTrackDataTransformer';
import {DoseTrackDataTransformer} from './dose/DoseTrackDataTransformer';
import {ConmedsTrackDataTransformer} from './conmeds/ConmedsTrackDataTransformer';
import {ExacerbationsTrackDataTransformer} from './exacerbations/ExacerbationsTrackDataTransformer';
import {EcgTrackDataTransformer} from './ecg/EcgTrackDataTransformer';
import {VitalsTrackDataTransformer} from './vitals/VitalsTrackDataTransformer';
import {TrackUtilDataTransformer} from './TrackUtilDataTransformer';
import {StatusTrackDataTransformer} from './status/StatusTrackDataTransformer';
import {SpirometryTrackDataTransformer} from './spirometry/SpirometryTrackDataTransformer';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {SessionHttpService} from '../../../session/http/SessionHttpService';

import {
    AesFiltersModel,
    CardiacFiltersModel,
    ConmedsFiltersModel,
    DoseFiltersModel,
    ExacerbationsFiltersModel,
    LabsFiltersModel,
    LungFunctionFiltersModel,
    PatientDataFiltersModel,
    PopulationFiltersModel,
    VitalsFiltersModel
} from '../../../filters/module';

import {FilterHttpService} from '../../../filters/http/FilterHttpService';
import {FilterEventService} from '../../../filters/event/FilterEventService';
import {DoseTrackDataService} from './dose/DoseTrackDataService';
import {ConmedsTrackDataService} from './conmeds/ConmedsTrackDataService';
import {LabsTrackDataService} from './labs/LabsTrackDataService';
import {LabsTrackDataTransformer} from './labs/LabsTrackDataTransformer';
import {ExacerbationsTrackDataService} from './exacerbations/ExacerbationsTrackDataService';
import {EcgTrackDataService} from './ecg/EcgTrackDataService';
import {VitalsTrackDataService} from './vitals/VitalsTrackDataService';
import {SpirometryTrackDataService} from './spirometry/SpirometryTrackDataService';
import {DatasetViews} from '../../../security/DatasetViews';
import {PatientDataTrackDataService} from './patientdata/PatientDataTrackDataService';
import {PatientDataTrackDataTransformer} from './patientdata/PatientDataTrackDataTransformer';

describe('GIVEN a TrackDataService class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: EnvService, useValue: new MockEnvService()},
                TrackUtilDataService,
                StatusTrackDataService,
                AesTrackDataService,
                AesTrackDataTransformer,
                DoseTrackDataTransformer,
                ConmedsTrackDataTransformer,
                ExacerbationsTrackDataTransformer,
                EcgTrackDataTransformer,
                VitalsTrackDataTransformer,
                PatientDataTrackDataTransformer,
                LabsTrackDataService,
                LabsTrackDataTransformer,
                TrackUtilDataTransformer,
                StatusTrackDataTransformer,
                SpirometryTrackDataTransformer,
                SessionHttpService,
                FilterHttpService,
                TrackDataService,
                DoseTrackDataService,
                ConmedsTrackDataService,
                ExacerbationsTrackDataService,
                EcgTrackDataService,
                VitalsTrackDataService,
                SpirometryTrackDataService,
                PatientDataTrackDataService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: LungFunctionFiltersModel, useClass: LungFunctionFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {provide: ConmedsFiltersModel, useClass: ConmedsFiltersModel, deps: [FilterEventService]},
                {provide: DoseFiltersModel, useClass: DoseFiltersModel, deps: [FilterEventService]},
                {provide: LabsFiltersModel, useClass: LabsFiltersModel, deps: [FilterEventService]},
                {provide: VitalsFiltersModel, useClass: VitalsFiltersModel, deps: [FilterEventService]},
                {provide: CardiacFiltersModel, useClass: CardiacFiltersModel, deps: [FilterEventService]},
                {provide: LungFunctionFiltersModel, useClass: LungFunctionFiltersModel, deps: [FilterEventService]},
                {provide: ExacerbationsFiltersModel, useClass: ExacerbationsFiltersModel, deps: [FilterEventService]},
                {provide: PatientDataFiltersModel, useClass: PatientDataFiltersModel, deps: [FilterEventService]}
            ]
        });
    });

    let subjects: ISubject[] = [];
    const dayZero: DayZero = DayZero.DAYS_SINCE_FIRST_DOSE;

    beforeEach(() => {
        subjects = [];

        subjects.push(<ISubject>new SubjectRecord({
            subjectId: 'SubjectA',
            tracks: [
                <ITrack>new TrackRecord({
                    name: TrackName.AES,
                    expansionLevel: 1,
                    selected: true,
                    order: 1
                }),
                <ITrack>new TrackRecord({
                    name: TrackName.SUMMARY,
                    expansionLevel: 1,
                    selected: true,
                    order: 2
                })
            ]
        }));

        subjects.push(<ISubject>new SubjectRecord({
            subjectId: 'SubjectB',
            tracks: [
                <ITrack>new TrackRecord({
                    name: TrackName.AES,
                    expansionLevel: 2,
                    selected: true,
                    order: 1
                }),
                <ITrack>new TrackRecord({
                    name: TrackName.SUMMARY,
                    expansionLevel: 1,
                    selected: true,
                    order: 2
                })
            ]
        }));
    });

    describe('WHEN getting track requests', () => {
        it('THEN splits out displayed subjects into unique tracks with lists of subjects',
            inject([TrackDataService], (service: TrackDataService) => {
                const results = service.getTrackRequests(subjects, dayZero);
                const expected: TrackRequest[] = [
                    <TrackRequest>{
                        name: TrackName.AES,
                        expansionLevel: 1,
                        dayZero: DayZero.DAYS_SINCE_FIRST_DOSE,
                        trackSubjects: ['SubjectA'],
                        subjectIds: ['SubjectA']
                    },
                    <TrackRequest>{
                        name: TrackName.AES,
                        expansionLevel: 2,
                        dayZero: DayZero.DAYS_SINCE_FIRST_DOSE,
                        trackSubjects: ['SubjectB'],
                        subjectIds: ['SubjectB']
                    },
                    <TrackRequest>{
                        name: TrackName.SUMMARY,
                        expansionLevel: 1,
                        dayZero: DayZero.DAYS_SINCE_FIRST_DOSE,
                        trackSubjects: ['SubjectA', 'SubjectB'],
                        subjectIds: ['SubjectA', 'SubjectB']
                    }
                ];
                expect(results).toEqual(expected);
            })
        );
    });
    describe('WHEN getting collect and sort unique tracks', () => {

        it('THEN splits out displayed subjects into unique tracks with lists of subjects',
            inject([TrackDataService], (service: TrackDataService) => {
                const results = service.collectSortedUnqiueTracks(subjects);
                const expected: ITrack[] = [
                    <ITrack>new TrackRecord({
                        name: TrackName.AES,
                        expansionLevel: 1,
                        selected: true,
                        order: 1
                    }),
                    <ITrack>new TrackRecord({
                        name: TrackName.AES,
                        expansionLevel: 2,
                        selected: true,
                        order: 1
                    }),
                    <ITrack>new TrackRecord({
                        name: TrackName.SUMMARY,
                        expansionLevel: 1,
                        selected: true,
                        order: 2
                    }),
                ];
                expect(results).toEqual(expected);
            })
        );
    });

});
