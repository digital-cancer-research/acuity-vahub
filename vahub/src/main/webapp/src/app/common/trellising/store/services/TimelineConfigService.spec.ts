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

import {TabId} from '../ITrellising';
import {TrackName} from '../../../../plugins/timeline/store/ITimeline';
import {TimelineConfigService} from './TimelineConfigService';
import {LabsFiltersModel, PopulationFiltersModel} from '../../../../filters/module';
import {MockFilterModel} from '../../../MockClasses';
import {DatasetViews} from '../../../../security/DatasetViews';
import {StudyService} from '../../../StudyService';

describe('GIVEN TimelineConfigService', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                StudyService,
                {provide: DatasetViews, deps: [StudyService]},
                PopulationFiltersModel,
                {provide: LabsFiltersModel, useClass: MockFilterModel},
                {
                    provide: TimelineConfigService,
                    useFactory: (labs: LabsFiltersModel, datasetViews: DatasetViews): TimelineConfigService => {
                        return new TimelineConfigService(labs, datasetViews);
                    },
                    deps: [LabsFiltersModel, DatasetViews]
                }
            ]
        });
    });

    describe('WHEN we navigate from AEs counts', () => {
        it('THEN the AEs track is opened on the PT level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {
                const res = timelineConfigService.getInitialState(TabId.AES_COUNTS_BARCHART);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.AES,
                    expansionLevel: 2,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from AEs over time', () => {
        it('THEN the AEs track is opened on the PT level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {
                const res = timelineConfigService.getInitialState(TabId.AES_OVER_TIME);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.AES,
                    expansionLevel: 2,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Labs Boxplot', () => {
        it('THEN the Labs track is opened on the Lab Measurement level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {
                const res = timelineConfigService.getInitialState(TabId.LAB_BOXPLOT);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.LABS,
                    expansionLevel: 3,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Labs Shiftplot', () => {
        it('THEN the Labs track is opened on the Lab Measurement level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {

                const res = timelineConfigService.getInitialState(TabId.LAB_SHIFTPLOT);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.LABS,
                    expansionLevel: 3,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Labs Shiftplot', () => {
        it('THEN the Labs track is opened on the Lab Measurement level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {

                const res = timelineConfigService.getInitialState(TabId.LAB_LINEPLOT);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.LABS,
                    expansionLevel: 3,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Vitals', () => {
        it('THEN the Vitals track is opened on the Vitals Measurement level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {

                const res = timelineConfigService.getInitialState(TabId.VITALS_BOXPLOT);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.VITALS,
                    expansionLevel: 2,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Cardiac', () => {
        it('THEN the Cardiac track is opened on the Cardiac Measurement level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {

                const res = timelineConfigService.getInitialState(TabId.CARDIAC_BOXPLOT);

                expect(res.length).toBe(1);
                expect(res[0]).toEqual({
                    name: TrackName.ECG,
                    expansionLevel: 2,
                    selected: true,
                    data: []
                });
            })
        );
    });

    describe('WHEN we navigate from Respiratory', () => {
        it('THEN the Lung Function and Exacerbations tracks are opened on the first level',
            inject([TimelineConfigService], (timelineConfigService: TimelineConfigService) => {

                const res = timelineConfigService.getInitialState(TabId.LUNG_FUNCTION_BOXPLOT);

                expect(res.length).toBe(2);
                expect(res[0]).toEqual({
                    name: TrackName.EXACERBATION,
                    expansionLevel: 1,
                    selected: true,
                    data: []
                });
                expect(res[1]).toEqual({
                    name: TrackName.SPIROMETRY,
                    expansionLevel: 2,
                    selected: true,
                    data: []
                });
            })
        );
    });
});
