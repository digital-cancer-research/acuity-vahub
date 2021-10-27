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
import {NoopAnimationsModule} from '@angular/platform-browser/animations';

import {AxisLabelService} from '../AxisLabelService';
import {YAxisLabelComponent} from './YAxisLabelComponent';
import {CommonModule} from '@angular/common';
import {CommonPipesModule} from '../../../pipes/CommonPipes.module';


describe('GIVEN YAxisLabelComponent', () => {

    let component: YAxisLabelComponent;
    let fixture: ComponentFixture<YAxisLabelComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [YAxisLabelComponent],
            imports: [
                CommonModule,
                FormsModule,
                CommonPipesModule,
                NoopAnimationsModule
            ],
            providers: [
                AxisLabelService
            ]
        });

        fixture = TestBed.createComponent(YAxisLabelComponent);
        component = fixture.componentInstance;
        component.options = fromJS(['FIRST', 'SECOND', 'THIRD']);
        component.option = fromJS('FIRST');
    });

    describe('WHEN initialised', () => {
        it('THEN the tab name is set correctly', () => {
            component.ngOnInit();
            fixture.detectChanges();
            expect(fixture.componentInstance.tooltip).toBe('Y-Axis settings');
            expect(fixture.componentInstance.isOpen).toBeFalsy();
            expect(fixture.componentInstance.hasEmitted).toBeFalsy();
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
            });
        });

        it('AND this component was not an emitter ', () => {
            it('THEN the "emmited" flag is changed', () => {
                spyOn(component, 'closeControl');
                component.hasEmitted = true;
                (<any>component).axisLabelService.closeOtherAxisLabels();
                expect(component.closeControl).toHaveBeenCalled();
            });
        });
    });
});
