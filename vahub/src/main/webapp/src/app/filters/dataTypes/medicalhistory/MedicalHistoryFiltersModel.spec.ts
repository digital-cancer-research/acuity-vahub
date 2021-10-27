import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {MedicalHistoryFiltersModel} from './MedicalHistoryFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';
import {ListFilterItemModel} from '../../components/module';

class MockFilterHttpService {
}

describe('GIVEN a MedicalHistoryFiltersModel class', () => {
    let medicalHistoryFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: MedicalHistoryFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): MedicalHistoryFiltersModel =>
                        new MedicalHistoryFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([MedicalHistoryFiltersModel], (_medicalHistoryFiltersModel: MedicalHistoryFiltersModel) => {
        medicalHistoryFiltersModel = _medicalHistoryFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(medicalHistoryFiltersModel.itemsModels.length).toBe(9);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[3]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[3]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[3]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>medicalHistoryFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
            expect(medicalHistoryFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {
            expect(medicalHistoryFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name medicalHistory', () => {
            expect(medicalHistoryFiltersModel.getName()).toEqual('medicalHistory');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setMedicalHistoryFilter', inject([FilterEventService],
            (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.medicalHistoryFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            medicalHistoryFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasMedicalHistoryData').and.returnValue(false);
                expect(medicalHistoryFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasMedicalHistoryData').and.returnValue(true);
                expect(medicalHistoryFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
