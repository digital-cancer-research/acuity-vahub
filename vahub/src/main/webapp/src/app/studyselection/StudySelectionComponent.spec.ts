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

import {TestBed} from '@angular/core/testing';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {StudySelectionComponent} from './StudySelectionComponent';
import {SessionEventService} from '../session/event/SessionEventService';
import {MockRouter, MockSessionEventService, MockStudyService} from '../common/MockClasses';
import {StudyService} from '../common/module';
import {ProgressComponentModule} from '../common/loading/ProgressComponent.module';

describe('GIVEN StudySelectionComponent', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: StudyService, useClass: MockStudyService },
                { provide: Router, useClass: MockRouter },
                { provide: Router, useClass: MockRouter },
                HttpClient,
                { provide: SessionEventService, useClass: MockSessionEventService }
            ],
            declarations: [StudySelectionComponent],
            imports: [
                ProgressComponentModule,
                HttpClientTestingModule,
                CommonModule,
                FormsModule
            ]
        });
    });

    /*xdescribe('WHEN the component is initialised', () => {
        describe('AND there is a previously-opened study (from old localStorage info)', () => {
            it('THEN no study is opened',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const fixture = TestBed.createComponent(StudySelectionComponent);
                        let mockSessionEventService = new MockSessionEventService();
                        let component = fixture.componentInstance;
                        localStorage.setItem('selectedDatasets', JSON.stringify(mockSessionEventService.currentSelectedDatasets));
                        component.ngOnInit();
                        expect(component.selectedDatasets).toBeUndefined();
                    });
                })
            );
        });

        describe('AND there is not a previously-opened study', () => {
            it('THEN no study is opened',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const fixture = TestBed.createComponent(StudySelectionComponent);
                        let component = fixture.componentInstance;
                        localStorage.setItem('selectedDataset', null);
                        component.ngOnInit();
                        expect(component.selectedDatasets).toBeUndefined();
                    });
                })
            );
        });

        describe('WHEN the page has loaded', () => {
            /!*                it('THEN the drug programmes are displayed correctly',
             async(() => {
             TestBed.compileComponents().then(() => {
             const fixture = TestBed.createComponent(StudySelectionComponent);
             let component = fixture.componentInstance;
             localStorage.setItem('selectedDataset', null);
             component.ngOnInit();
             component['loggedOnCallback']();
             fixture.detectChanges();

             let drugProgrammes = _.map(fixture.nativeElement.querySelectorAll('.studies__title'), 'innerHTML');
             expect(drugProgrammes).toEqual(['Drug X', 'STDY4321']);
             });
             })
             );

             it('THEN the studies are displayed correctly',
             async(() => {
             TestBed.compileComponents().then(() => {
             const fixture = TestBed.createComponent(StudySelectionComponent);
             let component = fixture.componentInstance;
             localStorage.setItem('selectedDataset', null);
             component.ngOnInit();
             component['loggedOnCallback']();
             fixture.detectChanges();

             let studiesForDrugX = $('.studies__title:contains("Drug X")').parent().find('.study-group__header span')
             .map(function (): any { return $(this).text(); });
             let studiesForDrugSTDY4321 = $('.studies__title:contains("STDY4321")').parent().find('.study-group__header span')
             .map(function (): any { return $(this).text(); });
             expect(studiesForDrugX).toEqual(jasmine.arrayContaining(['DummyData', 'Dummy2000', 'Dummy1000', 'DummyCombined']));
             expect(studiesForDrugSTDY4321).toEqual(jasmine.arrayContaining(['STDY4321 Dummy Instance']));
             });
             })
             );

             it('THEN the Datasets and their info are displayed correctly',
             async(() => {
             TestBed.compileComponents().then(() => {
             const fixture = TestBed.createComponent(StudySelectionComponent);
             let component = fixture.componentInstance;
             localStorage.setItem('selectedDataset', null);
             component.ngOnInit();
             component['loggedOnCallback']();
             fixture.detectChanges();

             let numDatasetsForDrugX = $('.studies__title:contains("Drug X")').parent().find('.study-group-table__row span').length;
             let numDatasetsForDrugSTDY4321 = $('.studies__title:contains("STDY4321")').parent().find('.study-group__header span').length;
             expect(numDatasetsForDrugX).toEqual(12);
             expect(numDatasetsForDrugSTDY4321).toEqual(2);
             });
             })
             );

             it('THEN the Warnings are displayed correctly',
             async(() => {
             TestBed.compileComponents().then(() => {
             const fixture = TestBed.createComponent(StudySelectionComponent);
             let component = fixture.componentInstance;
             localStorage.setItem('selectedDataset', null);
             component.ngOnInit();
             component['loggedOnCallback']();
             fixture.detectChanges();

             let studyWarnings = $('.studyWarnings').map(function (): any { return $(this).text().trim(); });
             expect(studyWarnings.length).toEqual(5);
             expect(studyWarnings[0]).toBe('Blinded, Randomised, Regulatory');
             expect(studyWarnings[1]).toBe('Blinded');
             expect(studyWarnings[2]).toBe('');
             });
             })
             );
             *!/
        });

        describe('WHEN a user selects a study', () => {
            let acls;
            beforeEach(() => {
                acls = [{
                    name: 'DummyStudy', id: 1, identifier: '', type: '',
                    shortNameByType: '', supertype: 'com.acuity.va.security.acl.domain.Dataset',
                    typeForJackson: '', canView: true, rolePermissionMask: 1, autoGeneratedId: true
                }];
            });

            it('THEN the study is set to be the selected study',
                async(inject([SessionEventService], (sessionEventService: SessionEventService) => {
                    TestBed.compileComponents().then(() => {
                        const fixture = TestBed.createComponent(StudySelectionComponent);
                        let component = fixture.componentInstance;
                        component.acls = acls;
                        component.selectedDatasetIds = [1];
                        spyOn(sessionEventService, 'setSelectedDataset');
                        component.openDataset();

                        expect(component.selectedDatasets[0].name).toBe('DummyStudy');
                        expect(sessionEventService.setSelectedDataset).toHaveBeenCalled();
                    });
                }))
            );
        });

    });*/
});

