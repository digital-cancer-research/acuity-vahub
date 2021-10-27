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

import {TestBed, ComponentFixture, inject} from '@angular/core/testing';

import {fromJS} from 'immutable';
import {FormsModule} from '@angular/forms';

import {AxisLabelService} from '../AxisLabelService';
import {XAxisLabelComponent} from './XAxisLabelComponent';
import {CommonModule} from '@angular/common';
import {CommonPipesModule} from '../../../pipes/CommonPipes.module';
import {XAxisLabelService} from './XAxisLabelService';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

describe('GIVEN XAxisLabelComponent', () => {

    let component: XAxisLabelComponent;
    let fixture: ComponentFixture<XAxisLabelComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [XAxisLabelComponent],
            imports: [
                CommonModule,
                FormsModule,
                CommonPipesModule,
                NoopAnimationsModule
            ],
            providers: [
                AxisLabelService,
                XAxisLabelService
            ]
        });

        fixture = TestBed.createComponent(XAxisLabelComponent);
        component = fixture.componentInstance;
        component.options = fromJS([{value: 'FIRST'}, {value: 'SECOND'}, {value: 'THIRD'}]);
        component.option = fromJS({value: 'FIRST'});
    });

    describe('WHEN initialised', () => {
        it('THEN the tab name is set correctly', () => {
            component.ngOnInit();
            fixture.detectChanges();
            expect(fixture.componentInstance.tooltip).toBe('X-Axis settings');
            expect(fixture.componentInstance.isOpen).toBeFalsy();
            expect(fixture.componentInstance.hasEmitted).toBeFalsy();
        });
    });

    describe('WHEN an option is applied', () => {
        let validator;
        beforeEach(inject([AxisLabelService], (axisLabelService: AxisLabelService) => {
            component.ngOnInit();

            validator = jasmine.createSpyObj('validator', ['called']);
            component.update.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );
        }));

        it('THEN the event is emitted', () => {
            component.updateSelection({target: {value: '{"value":"VISIT_NUMBER","intarg":null,"stringarg":null}'}});
            expect(validator.called).toHaveBeenCalledWith({
                'value': 'VISIT_NUMBER',
                'intarg': null,
                'stringarg': null
            });
        });

        it('THEN the selector is closed', () => {
            component.updateSelection({target: {value: '{"value":"VISIT_NUMBER","intarg":null,"stringarg":null}'}});
            expect(component.isOpen).toBeFalsy();
        });
    });

    describe('WHEN another axis label is opened', () => {
        beforeEach(() => {
            component.ngOnInit();
        });

        it('AND this component was an emitter ', () => {
            it('THEN the "emmited" flag is changed', () => {

                    component.hasEmitted = true;
                    (<any>component).axisLabelService.closeOtherAxisLabels();
                    expect(component.hasEmitted).toBeFalsy();
                }
            );
        });

        it('AND this component was not an emitter ', () => {
            it('THEN the "emmited" flag is changed', () => {
                    spyOn(component, 'closeControl');
                    component.hasEmitted = true;
                    (<any>component).axisLabelService.closeOtherAxisLabels();
                    expect(component.closeControl).toHaveBeenCalled();
                }
            );
        });
    });

    describe('WHEN closeControl is called', () => {
        beforeEach(() => {
            component.ngOnInit();
        });

        it('THEN control is closed', () => {
            component.isOpen = true;
            component.closeControl();
            expect(component.isOpen).toBeFalsy();
        });
    });

    describe('WHEN selection is updated', () => {
        let validator;
        beforeEach(() => {
            component.ngOnInit();
            validator = jasmine.createSpyObj('validator', ['called']);
            component.update.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );
        });

        it('THEN event with selected option is emitted', () => {
            component.updateSelection({target: {value: '{"value":"OPTION","intarg":null,"stringarg":null}'}});
            expect(validator.called).toHaveBeenCalledWith({'value': 'OPTION', 'intarg': null, 'stringarg': null});
        });

        it('THEN control is closed', () => {
            component.isOpen = true;
            component.updateSelection({target: {value: '{"value":"OPTION","intarg":null,"stringarg":null}'}});
            expect(component.isOpen).toBeFalsy();
        });
    });

    describe('WHEN control is toggled', () => {
        let validator;
        beforeEach(inject([AxisLabelService], (axisLabelService: AxisLabelService) => {
            component.ngOnInit();
            validator = jasmine.createSpyObj('validator', ['called']);
            spyOn(axisLabelService, 'closeOtherAxisLabels');
        }));

        it('THEN control is closed if was opened', () => {
            component.isOpen = true;
            component.toggleOpen();
            expect(component.isOpen).toBeFalsy();
        });

        it('THEN control is opened and other controls are closed', () => {
            component.isOpen = false;
            component.toggleOpen();
            expect(component.isOpen).toBeTruthy();
            expect(component.hasEmitted).toBeTruthy();
            expect((<any>component).axisLabelService.closeOtherAxisLabels).toHaveBeenCalled();
        });
    });
});
