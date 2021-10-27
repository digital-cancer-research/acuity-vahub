import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {Store, StoreModule} from '@ngrx/store';

import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {MockFilterEventService, MockDatasetViews} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';
import {TumourResponseFiltersModel} from './TumourResponseFiltersModel';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {trellisingReducer} from '../../../common/trellising/store/reducer/TrellisingReducer';
import {sharedStateReducer} from '../../../common/store/reducers/SharedStateReducer';

class MockFilterHttpService {
}

describe('GIVEN a TumourResponseFiltersModel class', () => {
    let tumourResponseFiltersModel;
    let store: Store<ApplicationState>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule, StoreModule.forRoot({trellisingReducer: trellisingReducer, sharedStateReducer: sharedStateReducer})],
            providers: [
                HttpClient,
                {
                    provide: DatasetViews, useClass: MockDatasetViews
                },
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: TumourResponseFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService,
                                 d: DatasetViews, s: Store<ApplicationState>): TumourResponseFiltersModel =>
                        new TumourResponseFiltersModel(p, f, e, d, s),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews, Store]
                }
            ]
        });
    });

    beforeEach(inject([TumourResponseFiltersModel, Store],
        (_tumourResponseFiltersModel: TumourResponseFiltersModel, _store: Store<ApplicationState>) => {
        tumourResponseFiltersModel = _tumourResponseFiltersModel;
        store = _store;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(tumourResponseFiltersModel.itemsModels.length).toBe(11);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(tumourResponseFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name therapy', () => {

            expect(tumourResponseFiltersModel.getName()).toEqual('therapy');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setTumourResponseFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);
                filterEventService.tumourResponseFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                tumourResponseFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasTumourResponseData').and.callFake(() => false);

                expect(tumourResponseFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasTumourResponseData').and.callFake(() => true);

                expect(tumourResponseFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
