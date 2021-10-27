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
import {TabId, TrellisDesign} from '../ITrellising';
import {fromJS} from 'immutable';
import {WaterfallPlotUtilsService} from './WaterfallPlotUtilsService';

describe('GIVEN a WaterfallPlotUtilsService class', () => {
    let service;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                WaterfallPlotUtilsService
            ]
        });

        service = TestBed.get(WaterfallPlotUtilsService);
    });

    describe('WHEN calculating zoom ranges', () => {
        describe('AND data doesnt contain Y that are more than 100 and less than -100', () => {
            it('THEN should calculate zoomRanges accordingly', () => {
                const plots = fromJS([
                    {
                        data: {
                            xcategories: ['A', 'B', 'C'],
                            entries: [
                                {
                                    y: 1
                                },
                                {
                                    y: -1
                                }
                            ]
                        }
                    }
                ]);


                expect(
                    service.calculateZoomRanges(
                        plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.TUMOUR_RESPONSE_WATERFALL_PLOT
                    )
                ).toEqual({
                    x: { min: 0, max: 2 },
                    y: { min: -100, max: 100 }
                });
            });
        });

        describe('AND data does contain Y that are more than 100 and less than -100', () => {
            it('THEN should calculate zoomRanges accordingly', () => {
                const plots = fromJS([
                    {
                        data: {
                            xcategories: ['A', 'B', 'C'],
                            entries: [
                                {
                                    y: -1002
                                },
                                {
                                    y: 1234
                                }
                            ]
                        }
                    }
                ]);


                expect(
                    service.calculateZoomRanges(
                        plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.TUMOUR_RESPONSE_WATERFALL_PLOT
                    )
                ).toEqual({
                    x: { min: 0, max: 2 },
                    y: { min: -1002, max: 1234 }
                });
            });
        });

        describe('AND data does contain Y that are more than 100 but not less than -100', () => {
            it('THEN should calculate zoomRanges accordingly', () => {
                const plots = fromJS([
                    {
                        data: {
                            xcategories: ['A', 'B', 'C'],
                            entries: [
                                {
                                    y: -1
                                },
                                {
                                    y: 1234
                                }
                            ]
                        }
                    }
                ]);


                expect(
                    service.calculateZoomRanges(
                        plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.TUMOUR_RESPONSE_WATERFALL_PLOT
                    )
                ).toEqual({
                    x: { min: 0, max: 2 },
                    y: { min: -100, max: 1234 }
                });
            });
        });

        describe('AND data does contain Y that are less than -100 but not more than 100', () => {
            it('THEN should calculate zoomRanges accordingly', () => {
                const plots = fromJS([
                    {
                        data: {
                            xcategories: ['A', 'B', 'C'],
                            entries: [
                                {
                                    y: -123
                                },
                                {
                                    y: 1
                                }
                            ]
                        }
                    }
                ]);


                expect(
                    service.calculateZoomRanges(
                        plots, TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES, TabId.TUMOUR_RESPONSE_WATERFALL_PLOT
                    )
                ).toEqual({
                    x: { min: 0, max: 2 },
                    y: { min: -123, max: 100 }
                });
            });
        });
    });
});
