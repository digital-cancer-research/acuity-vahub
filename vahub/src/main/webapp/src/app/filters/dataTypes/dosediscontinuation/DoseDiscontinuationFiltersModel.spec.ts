import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {DoseDiscontinuationFiltersModel} from './DoseDiscontinuationFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a DoseDiscontinuationFiltersModel class', () => {
    let doseDiscontinuationFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: DoseDiscontinuationFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): DoseDiscontinuationFiltersModel =>
                        new DoseDiscontinuationFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([DoseDiscontinuationFiltersModel], (_doseDiscontinuationFiltersModel: DoseDiscontinuationFiltersModel) => {
        doseDiscontinuationFiltersModel = _doseDiscontinuationFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(doseDiscontinuationFiltersModel.itemsModels.length).toBe(7);
            expect(doseDiscontinuationFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(doseDiscontinuationFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name doseDiscontinuation', () => {

            expect(doseDiscontinuationFiltersModel.getName()).toEqual('doseDisc');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setDoseDiscontinuationFilter', inject([FilterEventService],
                (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.doseDiscontinuationFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            doseDiscontinuationFiltersModel.emitEvent({});

            expect(validator.called).toHaveBeenCalledWith({});
        }));
    });

    describe('WHEN checking if visible', () => {
        let datasetViews;

        beforeEach(inject([DatasetViews], (_datasetViews: DatasetViews) => {
            datasetViews = _datasetViews;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasDoseDiscontinuationData').and.returnValue(false);
                expect(doseDiscontinuationFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasDoseDiscontinuationData').and.returnValue(true);
                expect(doseDiscontinuationFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
