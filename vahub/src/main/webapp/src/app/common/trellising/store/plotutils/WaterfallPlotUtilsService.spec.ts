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
