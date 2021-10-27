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

import {ComponentFixture, inject, TestBed} from '@angular/core/testing';

import {FormsModule} from '@angular/forms';
import {Observable} from 'rxjs/Observable';
import {MarkingComponent} from './MarkingComponent';
import {CommonModule, Location} from '@angular/common';
import {AesFiltersModel, PopulationFiltersModel} from '../../../../../filters/module';

import {FilterEventService} from '../../../../../filters/event/FilterEventService';
import {MockDatasetViews, MockFilterEventService, MockRouter, MockSessionEventService} from '../../../../MockClasses';
import {SpyLocation} from '@angular/common/testing';
import {Router} from '@angular/router';
import {DatasetViews} from '../../../../../security/DatasetViews';
import {MarkingDialogue} from './MarkingDialogue';
import {ISelection, ISelectionDetail} from '../../../store';
import {AesHttpService} from '../../../../../data/aes';
import {SessionEventService} from '../../../../../session/module';
import {HttpClient} from '@angular/common/http';
import {HttpClientTestingModule} from '@angular/common/http/testing';

describe('GIVEN MarkingComponent', () => {

    let component: MarkingComponent;
    let fixture: ComponentFixture<MarkingComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [MarkingComponent],
            imports: [
                CommonModule,
                FormsModule,
                HttpClientTestingModule
            ],
            providers: [
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {provide: Location, useClass: SpyLocation},
                {provide: Router, useClass: MockRouter},
                {provide: AesHttpService, useClass: AesHttpService, deps: [HttpClient, PopulationFiltersModel, AesFiltersModel]},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: DatasetViews, useClass: MockDatasetViews}
            ]
        });

        fixture = TestBed.createComponent(MarkingComponent);
        component = fixture.componentInstance;
    });

    function initFixture(fixtureValue, dialogueBox?: MarkingDialogue, selection?: ISelection, selectionDetail?: ISelectionDetail): any {
        fixtureValue.componentInstance.dialogueBox = dialogueBox;
        fixtureValue.componentInstance.selection = selection;
        fixtureValue.componentInstance.selectionDetail = selectionDetail;
        return fixtureValue;
    }

    describe('WHEN marking dialog is closed', () => {
        it('THEN the box is hidden', () => {
            fixture = initFixture(fixture);
            fixture.componentInstance.close();
            expect(fixture.componentInstance.box.show).toBeFalsy();
        });
    });

    describe('WHEN setAsPopulation is called', () => {
        describe('AND selection is empty', () => {
            it('THEN subjects population is not set to filters',
                inject([FilterEventService],
                    (filterEventService: FilterEventService) => {
                        fixture = initFixture(fixture);
                        spyOn(fixture.componentInstance.populationFiltersModel, 'setAsPopulation');
                        fixture.componentInstance.setAsPopulation();
                        expect(fixture.componentInstance.populationFiltersModel.setAsPopulation).not.toHaveBeenCalled();
                    })
            );
        });
        describe('AND selection is not empty', () => {
            it('THEN subjects population is set to filters and filters are sent to server',
                inject([FilterEventService, PopulationFiltersModel, DatasetViews],
                    (filterEventService: FilterEventService, populationFiltersModel: PopulationFiltersModel,
                     datasetViews: DatasetViews) => {
                        const selectionDetail = <ISelectionDetail> {
                            eventIds: ['eventId'],
                            subjectIds: ['subjectId'],
                            totalSubjects: 1,
                            totalEvents: 1
                        };
                        fixture = initFixture(fixture, null, null, selectionDetail);
                        spyOn(fixture.componentInstance.populationFiltersModel, 'setAsPopulation');
                        fixture.componentInstance.setAsPopulation();
                        expect(fixture.componentInstance.box.show).toBeFalsy();
                    })
            );
        });
    });

    describe('WHEN View on timeline button is pressed', () => {
        it('THEN setAsPopulation is called', () => {
            fixture = initFixture(fixture);
            spyOn(fixture.componentInstance, 'setAsPopulation');
            fixture.componentInstance.setAsTimeline();
            expect(fixture.componentInstance.setAsPopulation).toHaveBeenCalled();
        });
        it('THEN timeline state is updated', () => {
            fixture = initFixture(fixture);
            spyOn(fixture.componentInstance.setTimelineState, 'next');
            fixture.componentInstance.setAsTimeline();
            expect(fixture.componentInstance.setTimelineState.next).toHaveBeenCalled();
        });
    });

    describe('WHEN chart marking is cleared', () => {
        it('THEN disalog box is hidden', () => {
            fixture = initFixture(fixture);
            fixture.componentInstance.clearAllMarkingsAction();
            expect(fixture.componentInstance.box.show).toBeFalsy();
        });
        it('THEN event is emitted', () => {
            fixture = initFixture(fixture);
            spyOn(fixture.componentInstance.clearMarkings, 'next');
            fixture.componentInstance.clearAllMarkingsAction();
            expect(fixture.componentInstance.clearMarkings.next).toHaveBeenCalled();
        });
    });

    describe('WHEN percentage of subjects is requested', () => {
        describe('AND selection is empty', () => {

            it('THEN undefined is returned ', () => {
                fixture = initFixture(fixture);
                const percentage = fixture.componentInstance.percentageOfSubjects();
                expect(percentage).not.toBeDefined();
            });
        });
        describe('AND selection is not empty', () => {
            it('THEN event is emitted', () => {
                const selectionDetail = <ISelectionDetail> {
                    eventIds: ['eventId'],
                    subjectIds: ['subjectId'],
                    totalSubjects: 10,
                    totalEvents: 1
                };
                fixture = initFixture(fixture, null, null, selectionDetail);
                spyOn(fixture.componentInstance.clearMarkings, 'next');
                const percentage = fixture.componentInstance.percentageOfSubjects();
                expect(percentage).toBe('10.00');
            });
        });
    });

    describe('WHEN setAesNumberFilterAndGotoAes is called', () => {
        describe('AND selection is empty', () => {
            it('THEN getAssociatedAesNumbersFromEventIds is not called',
                inject([AesHttpService],
                    (aesHttpService: AesHttpService) => {
                        fixture = initFixture(fixture);
                        spyOn(aesHttpService, 'getAssociatedAesNumbersFromEventIds');
                        fixture.componentInstance.setAesNumberFilterAndGotoAes();
                        expect(aesHttpService.getAssociatedAesNumbersFromEventIds).not.toHaveBeenCalled();
                    })
            );
        });
        describe('AND selection is not empty', () => {
            it('THEN subjects population is set to filters and filters are sent to server',
                inject([AesHttpService, DatasetViews],
                    (aesHttpService: AesHttpService, datasetViews: DatasetViews) => {
                        const selectionDetail = <ISelectionDetail> {
                            eventIds: ['eventId'],
                            subjectIds: ['subjectId'],
                            totalSubjects: 1,
                            totalEvents: 1
                        };
                        fixture = initFixture(fixture, null, null, selectionDetail);
                        spyOn(aesHttpService, 'getAssociatedAesNumbersFromEventIds').and.returnValue(Observable.of(['123']));
                        spyOn(fixture.componentInstance.aesFiltersModel, 'getFilters');
                        fixture.componentInstance.setAesNumberFilterAndGotoAes();
                        expect(aesHttpService.getAssociatedAesNumbersFromEventIds).toHaveBeenCalled();
                    })
            );
        });
    });
});
