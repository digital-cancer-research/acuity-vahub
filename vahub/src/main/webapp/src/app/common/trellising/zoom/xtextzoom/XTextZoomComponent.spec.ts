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

import {XTextZoomComponent} from './XTextZoomComponent';
import {IZoom} from '../../store/ITrellising';

describe('GIVEN XTextZoomComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            declarations: [XTextZoomComponent],
            providers: []
        });
    });

    function initFixture(fixture, zoom: IZoom): any {
        fixture.componentInstance.zoom = zoom;
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {
        it('THEN the input values are set to default',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(XTextZoomComponent);
                    rootTC.detectChanges();

                    expect(rootTC.componentInstance.inputValues).toEqual(
                        {
                            zoomMin: 1,
                            zoomMax: 1,
                            absMin: 1,
                            absMax: 1
                        });
                });
            }));
    });
    describe('WHEN min values are updated', () => {

        describe('AND zoomMin is smaller then absolute min', () => {

            it('THEN zoomMin is set to absolute min',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XTextZoomComponent);
                        spyOn(rootTC.componentInstance.update, 'emit');
                        rootTC.componentInstance.inputValues = {
                            zoomMin: 1,
                            zoomMax: 9,
                            absMin: 2,
                            absMax: 10
                        };
                        rootTC.componentInstance.updateMinValue();
                        const expectedResult = {
                            zoomMin: 1,
                            zoomMax: 8,
                            absMin: 1,
                            absMax: 9
                        };

                        expect(rootTC.componentInstance.update.emit).toHaveBeenCalledWith(expectedResult);
                    });
                }));
        });

        describe('AND range between zoomMin and zoomMax is less then 6', () => {

            it('THEN zoomMin is set to (zoomMax - 6)',
                async(() => {
                    TestBed.compileComponents().then(() => {
                        const rootTC = TestBed.createComponent(XTextZoomComponent);
                        spyOn(rootTC.componentInstance.update, 'emit');
                        rootTC.componentInstance.inputValues = {
                            zoomMin: 8,
                            zoomMax: 9,
                            absMin: 2,
                            absMax: 10
                        };
                        rootTC.componentInstance.updateMinValue();
                        const expectedResult = {
                            zoomMin: 2,
                            zoomMax: 8,
                            absMin: 1,
                            absMax: 9
                        };

                        expect(rootTC.componentInstance.update.emit).toHaveBeenCalledWith(expectedResult);
                    });
                }));
        });
        describe('WHEN max values are updated', () => {

            describe('AND zoomMax is bigger then absolute max', () => {

                it('THEN zoomMax is set to absolute max',
                    async(() => {
                        TestBed.compileComponents().then(() => {
                            const rootTC = TestBed.createComponent(XTextZoomComponent);
                            spyOn(rootTC.componentInstance.update, 'emit');
                            rootTC.componentInstance.inputValues = {
                                zoomMin: 3,
                                zoomMax: 11,
                                absMin: 2,
                                absMax: 10
                            };
                            rootTC.componentInstance.updateMaxValue();
                            const expectedResult = {
                                zoomMin: 2,
                                zoomMax: 9,
                                absMin: 1,
                                absMax: 9
                            };

                            expect(rootTC.componentInstance.update.emit).toHaveBeenCalledWith(expectedResult);
                        });
                    }));
            });

            describe('AND range between zoomMin and zoomMax is less then 6', () => {

                it('THEN zoomMax is set to (zoomMin + 6)',
                    async(() => {
                        TestBed.compileComponents().then(() => {
                            const rootTC = TestBed.createComponent(XTextZoomComponent);
                            spyOn(rootTC.componentInstance.update, 'emit');
                            rootTC.componentInstance.inputValues = {
                                zoomMin: 2,
                                zoomMax: 3,
                                absMin: 2,
                                absMax: 10
                            };
                            rootTC.componentInstance.updateMaxValue();
                            const expectedResult = {
                                zoomMin: 1,
                                zoomMax: 7,
                                absMin: 1,
                                absMax: 9
                            };

                            expect(rootTC.componentInstance.update.emit).toHaveBeenCalledWith(expectedResult);
                        });
                    }));
            });
        });
    });
});
