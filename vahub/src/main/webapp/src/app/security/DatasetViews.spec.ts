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
