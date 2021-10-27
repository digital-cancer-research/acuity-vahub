import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {LiverRiskFactorsFiltersModel} from './LiverRiskFactorsFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a LiverRiskFactorsFiltersModel class', () => {
    let liverRiskFactorsFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: LiverRiskFactorsFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): LiverRiskFactorsFiltersModel =>
                        new LiverRiskFactorsFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([LiverRiskFactorsFiltersModel], (_liverRiskFactorsFiltersModel: LiverRiskFactorsFiltersModel) => {
        liverRiskFactorsFiltersModel = _liverRiskFactorsFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(liverRiskFactorsFiltersModel.itemsModels.length).toBe(10);
            expect(liverRiskFactorsFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {
            expect(liverRiskFactorsFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name liverRiskFactors', () => {
            expect(liverRiskFactorsFiltersModel.getName()).toEqual('liverRisk');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setLiverRiskFactorsFilter', inject([FilterEventService],
            (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.liverRiskFactorsFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            liverRiskFactorsFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasLiverRiskFactorsData').and.returnValue(false);
                expect(liverRiskFactorsFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasLiverRiskFactorsData').and.returnValue(true);
                expect(liverRiskFactorsFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
