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

import {async, TestBed, inject} from '@angular/core/testing';
import {ApplySafetyFiltersModalComponent, ApplySafetyFiltersModalService} from './module';
import {ModalMessageComponent} from '../modalMessage/ModalMessageComponent';
import {SessionEventService} from '../../../session/module';
import {DatasetViews} from '../../../security/module';
import {MockSessionEventService} from '../../MockClasses';
import {PopulationFiltersModel} from '../../../filters/dataTypes/population/PopulationFiltersModel';

class MockPopulationFiltersModel {
    setSafetyPopulationAsY(): void {
        console.log('setSafetyPopulationAsY');
    }

    getFilters(a: boolean, b: boolean): any {
        return '';
    }

}

class MockApplySafetyFiltersModalService extends ApplySafetyFiltersModalService {
    constructor() {
        super(null);
    }
}

describe('GIVEN ApplySafetyFiltersModalComponent', () => {


    let mockDatasetViews: DatasetViews;

    const mockApplySafetyFiltersModalService = new MockApplySafetyFiltersModalService();

    beforeEach(() => {

        spyOn(mockApplySafetyFiltersModalService, 'setReShowMessage');

        mockDatasetViews = <any>{
            hasSafetyAsNoInPopulation: function (): boolean {
                return true;
            }
        };

        TestBed.configureTestingModule({
            declarations: [ModalMessageComponent, ApplySafetyFiltersModalComponent],
            providers: [
                {provide: ApplySafetyFiltersModalService, useValue: mockApplySafetyFiltersModalService},
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: DatasetViews, useValue: mockDatasetViews},
                {provide: PopulationFiltersModel, useClass: MockPopulationFiltersModel}
            ]
        });
    });

    function initFixture(fixture, isVisable: boolean): any {
        fixture.componentInstance.modalIsVisible = isVisable;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        describe('AND the its not got any safety No data', () => {

            it('THEN the model is displayed',
                async(inject([ApplySafetyFiltersModalService], (ApplySafetyFiltersModalService: ApplySafetyFiltersModalService) => {
                    TestBed.compileComponents().then(() => {

                        ApplySafetyFiltersModalService.setReShowMessage(true);
                        const rootTC = TestBed.createComponent(ApplySafetyFiltersModalComponent);
                        rootTC.detectChanges();

                        const result = rootTC.componentInstance.modalIsVisible;

                        expect(result).toBeFalsy();
                    });
                }))
            );
        });

        describe('WHEN modalIsVisible is set to false', () => {

            it('THEN the modal is displayed',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ApplySafetyFiltersModalComponent);

                        const element = initFixture(rootTC, true);
                        rootTC.detectChanges();

                        const res = element.querySelector('.modal-title').textContent;

                        expect(res).toContain('Restrict to Safety Population?');
                    });
                }));
        });

        describe('WHEN okModal() is called', () => {

            it('THEN the modal is closed',
                async(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ApplySafetyFiltersModalComponent);
                        spyOn(populationFiltersModel, 'setSafetyPopulationAsY');

                        initFixture(rootTC, true);

                        rootTC.componentInstance.okModal();
                        rootTC.detectChanges();
                        const modalIsVisible = rootTC.componentInstance.modalIsVisible;

                        expect(populationFiltersModel.setSafetyPopulationAsY).toHaveBeenCalled();
                        expect(mockApplySafetyFiltersModalService.setReShowMessage).toHaveBeenCalledWith(false);
                    });
                })));
        });


        describe('WHEN cancelModal() is called', () => {

            it('THEN the modal is closed',
                async(inject([PopulationFiltersModel], (populationFiltersModel: PopulationFiltersModel) => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(ApplySafetyFiltersModalComponent);
                        spyOn(populationFiltersModel, 'setSafetyPopulationAsY');

                        initFixture(rootTC, true);

                        rootTC.componentInstance.cancelModal();
                        rootTC.detectChanges();

                        const result = rootTC.componentInstance.modalIsVisible;

                        //expect(modalIsVisible).toBeFalsy();
                        expect(populationFiltersModel.setSafetyPopulationAsY).not.toHaveBeenCalled();
                        expect(mockApplySafetyFiltersModalService.setReShowMessage).toHaveBeenCalledWith(false);
                    });
                })));
        });
    });
});
