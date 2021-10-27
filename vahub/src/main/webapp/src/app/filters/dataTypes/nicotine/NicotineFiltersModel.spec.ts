import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {NicotineFiltersModel} from './NicotineFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a NicotineFiltersModel class', () => {
    let nicotineFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: NicotineFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): NicotineFiltersModel =>
                        new NicotineFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([NicotineFiltersModel], (_nicotineFiltersModel: NicotineFiltersModel) => {
        nicotineFiltersModel = _nicotineFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(nicotineFiltersModel.itemsModels.length).toBe(11);
            expect(nicotineFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', inject([NicotineFiltersModel], () => {

            expect(nicotineFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name nicotine', inject([NicotineFiltersModel], () => {

            expect(nicotineFiltersModel.getName()).toEqual('nicotine');
        }));
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setNicotineFilter', inject([FilterEventService], (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.nicotineFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            nicotineFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasNicotineData').and.returnValue(false);
                expect(nicotineFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasNicotineData').and.returnValue(true);
                expect(nicotineFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
