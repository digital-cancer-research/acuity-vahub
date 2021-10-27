import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';

import {RenalFiltersModel} from './RenalFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockFilterEventService, MockDatasetViews} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN RenalFiltersModel', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                HttpClient,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useValue: new MockFilterEventService()},
                {
                    provide: RenalFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews): RenalFiltersModel =>
                        new RenalFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    describe('WHEN getName is called', () => {
        it('THEN renal filter name is returned',
            inject([RenalFiltersModel], (model: RenalFiltersModel) => {
                expect(model.getName()).toEqual('renal');
            })
        );
    });

    describe('WHEN checking if visible', () => {
        let datasetViews, renalFiltersModel;

        beforeEach(inject([DatasetViews, RenalFiltersModel], (_datasetViews: DatasetViews, _renalFiltersModel: RenalFiltersModel) => {
            datasetViews = _datasetViews;
            renalFiltersModel = _renalFiltersModel;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasRenalData').and.returnValue(false);
                expect(renalFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasRenalData').and.returnValue(true);
                expect(renalFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
