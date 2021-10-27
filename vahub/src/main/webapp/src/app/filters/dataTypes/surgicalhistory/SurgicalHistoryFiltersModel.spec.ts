import {inject, TestBed} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';

import {SurgicalHistoryFiltersModel} from './SurgicalHistoryFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a SurgicalHistoryFiltersModel class', () => {
    let surgicalHistoryFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: SurgicalHistoryFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): SurgicalHistoryFiltersModel =>
                        new SurgicalHistoryFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([SurgicalHistoryFiltersModel], (_surgicalHistoryFiltersModel: SurgicalHistoryFiltersModel) => {
        surgicalHistoryFiltersModel = _surgicalHistoryFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(surgicalHistoryFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(surgicalHistoryFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name surgicalHistory', () => {

            expect(surgicalHistoryFiltersModel.getName()).toEqual('surgicalHistory');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setSurgicalHistoryFilter', inject([FilterEventService],
                (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.surgicalHistoryFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            surgicalHistoryFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasSurgicalHistoryData').and.returnValue(false);
                expect(surgicalHistoryFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasSurgicalHistoryData').and.returnValue(true);
                expect(surgicalHistoryFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
