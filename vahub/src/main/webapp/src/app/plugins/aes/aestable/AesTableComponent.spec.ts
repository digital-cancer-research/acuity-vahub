import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {ComponentFixture, ComponentFixtureAutoDetect, inject, TestBed} from '@angular/core/testing';
import {AgGridModule} from 'ag-grid-angular/main';

import {AesFiltersModel, FilterEventService, PopulationFiltersModel} from '../../../filters/module';
import {DropdownComponentModule, StudyService} from '../../../common/module';
import {
    MockFilterEventService,
    MockSessionEventService,
    MockStudyService,
    MockTimelineDispatcher,
    MockTrellisingDispatcher
} from '../../../common/MockClasses';
import {AesTableComponent} from './AesTableComponent';
import {AesTableDropdownModel} from './AesTableDropdownModel';
import {AgGridService, AgGridVersionEnum} from '../../../common/AgGridService';
import {AesTableAgGridStrategy} from './ag-grid-strategy/AesTableAgGridStrategy';
import {AesTableServiceCommunity} from './ag-grid-strategy/AesTableServiceCommunity';
import {AesTableServiceEnterprise} from './ag-grid-strategy/AesTableServiceEnterprise';
import {AesTableHttpService} from '../../../data/aes';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {ModalMessageComponentModule} from '../../../common/modals/modalMessage/ModalMessageComponent.module';
import {SessionEventService} from '../../../session/event/SessionEventService';

describe('GIVEN AesTableComponent', () => {

    let component: AesTableComponent;
    let fixture: ComponentFixture<AesTableComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [AesTableComponent],
            imports: [
                HttpClientTestingModule,
                DropdownComponentModule,
                ModalMessageComponentModule,
                AgGridModule.withComponents([
                    AesTableComponent
                ])
            ],
            providers: [
                AesTableDropdownModel,
                FilterEventService,
                HttpClient,
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: ComponentFixtureAutoDetect, useValue: true},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {provide: TimelineDispatcher, useClass: MockTimelineDispatcher},
                {provide: StudyService, useClass: MockStudyService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {
                    provide: AesTableHttpService,
                    useClass: AesTableHttpService,
                    deps: [HttpClient, PopulationFiltersModel, AesFiltersModel]
                },
                {provide: AgGridService, useValue: {
                    getAgGridVersion: () => AgGridVersionEnum.Enterprise
                }},
                AesTableServiceCommunity,
                AesTableServiceEnterprise,
                {
                    provide: AesTableAgGridStrategy,
                    useClass: AesTableAgGridStrategy,
                    deps: [AgGridService, AesTableServiceCommunity, AesTableServiceEnterprise]
                }
            ]
        });

        fixture = TestBed.createComponent(AesTableComponent);
        component = fixture.componentInstance;
    });

    describe('WHEN the component is loaded', () => {
        it('THEN data is requested from the server', () => {
            spyOn(component, 'refreshDataFromServer');

            component.onTableInitialised();

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        });
    });

    describe('WHEN the filters change', () => {
        it('THEN data is requested from the server', inject([FilterEventService], (filterEventService: FilterEventService) => {
            spyOn(component, 'refreshDataFromServer');

            component.ngOnInit();
            filterEventService.setAesFilter({});

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        }));
    });

    describe('WHEN the AE Level dropdown is changed', () => {
        it('THEN data is requested from the server', () => {
            spyOn(component, 'refreshDataFromServer');

            component.ngOnInit();
            component.aeLevelClicked({displayName: 'PT', serverName: 'PT'});

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        });
    });
});
