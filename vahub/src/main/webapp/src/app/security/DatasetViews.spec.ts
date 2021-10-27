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

import {TestBed, inject} from '@angular/core/testing';
import {DatasetViews} from './DatasetViews';
import {StudyService} from '../common/module';
import {MockStudyService} from '../common/MockClasses';

describe('WHEN testing DatasetViews with data in db', () => {

    beforeEach(() => {

        TestBed.configureTestingModule({
            providers: [
                DatasetViews,
                {provide: StudyService, useClass: MockStudyService},
            ]
        });
    });

    it('THEN it SHOULD allow the correct access', inject([DatasetViews], (datasetViews: DatasetViews) => {

        const hasAesData = datasetViews.hasAesData();
        const hasLabsData = datasetViews.hasLabsData();
        const hasVitalsData = datasetViews.hasVitalsData();
        const hasCardiacData = datasetViews.hasCardiacData();
        const hasRenalData = datasetViews.hasRenalData();
        const hasLiverData = datasetViews.hasLiverData();
        const hasRespiratryData = datasetViews.hasRespiratryData();
        const hasExacerbationsData = datasetViews.hasExacerbationsData();
        const hasDoseData = datasetViews.hasDoseData();
        const hasOncoBiomarkersData = datasetViews.hasOncoBiomarkers();

        expect(hasAesData).toBeTruthy();
        expect(hasLabsData).toBeFalsy();
        expect(hasVitalsData).toBeTruthy();
        expect(hasCardiacData).toBeTruthy();
        expect(hasRenalData).toBeFalsy();
        expect(hasLiverData).toBeFalsy();
        expect(hasRespiratryData).toBeFalsy();
        expect(hasExacerbationsData).toBeTruthy();
        expect(hasDoseData).toBeTruthy();
        expect(hasOncoBiomarkersData).toBeTruthy();
    }));

    describe('WHEN  getting Details on Demand columns', () => {
        describe('AND tab is cievents', () => {
            const tab = 'cievents';

            it('THEN SHOULD return appropriate columns', inject([DatasetViews], (datasetViews: DatasetViews) => {
                const expectedColumns = ['studyId', 'part', 'subjectId', 'aeNumber', 'startDate', 'term'];


                const actualColumns = datasetViews.getDetailsOnDemandColumns(tab);

                expect(actualColumns).toEqual(expectedColumns);
            }));
        });
    });
});
