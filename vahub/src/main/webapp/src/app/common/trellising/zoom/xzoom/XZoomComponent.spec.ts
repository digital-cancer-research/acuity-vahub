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

import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {Map} from 'immutable';

import {XZoomComponent} from './XZoomComponent';
import {TabId, TrellisDesign} from '../../store/ITrellising';

describe('GIVEN XTextZoomComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [XZoomComponent],
            providers: []
        });
    });

    describe('WHEN the component is loaded', () => {
        describe('AND axis is categorical', () => {
            it('THEN step size is chosen correctly',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XZoomComponent);
                        rootTC.detectChanges();
                        rootTC.componentInstance.zoom = <any>Map({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100});
                        rootTC.componentInstance.trellisDesign = TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES;
                        expect(rootTC.componentInstance.stepSize()).toEqual(1);
                    });
                }));
        });
        describe('AND we are on biomarkers page', () => {
            it('THEN step size is chosen correctly',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XZoomComponent);
                        rootTC.detectChanges();
                        rootTC.componentInstance.zoom = <any>Map({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100});
                        rootTC.componentInstance.trellisDesign = TrellisDesign.VARIABLE_Y_VARIABLE_X;
                        rootTC.componentInstance.tabId = TabId.BIOMARKERS_HEATMAP_PLOT;
                        expect(rootTC.componentInstance.stepSize()).toEqual(1);
                    });
                }));
        });
        describe('AND we are on Analyte concentration page', () => {
            it('THEN step size is chosen correctly',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XZoomComponent);
                        rootTC.detectChanges();
                        rootTC.componentInstance.zoom = <any>Map({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100});
                        rootTC.componentInstance.trellisDesign = TrellisDesign.CONTINUOUS_OVER_TIME;
                        rootTC.componentInstance.tabId = TabId.ANALYTE_CONCENTRATION;
                        expect(rootTC.componentInstance.stepSize()).toEqual(1);
                    });
                }));
        });
        describe('AND we are on tl diameters tumour page', () => {
            it('THEN step size is chosen correctly',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XZoomComponent);
                        rootTC.detectChanges();
                        rootTC.componentInstance.zoom = <any>Map({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100});
                        rootTC.componentInstance.trellisDesign = TrellisDesign.CONTINUOUS_OVER_TIME;
                        rootTC.componentInstance.tabId = TabId.TL_DIAMETERS_PLOT;
                        expect(rootTC.componentInstance.stepSize()).toEqual(1);
                    });
                }));
        });
        describe('AND we axis is continuous over time', () => {
            it('THEN step size is chosen correctly',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XZoomComponent);
                        rootTC.detectChanges();
                        rootTC.componentInstance.zoom = <any>Map({absMin: 0, absMax: 100, zoomMin: 0, zoomMax: 100});
                        rootTC.componentInstance.trellisDesign = TrellisDesign.CONTINUOUS_OVER_TIME;
                        expect(rootTC.componentInstance.stepSize()).toEqual(0.1);
                    });
                }));
        });
    });
});
