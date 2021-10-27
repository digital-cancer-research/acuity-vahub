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
import {fromJS, List, Map} from 'immutable';
import Spy = jasmine.Spy;

import {PlotService} from './PlotService';
import {TitleService} from './services/TitleService';
import {AxisLabelService} from './services/AxisLabelService';
import {SentenceCasePipe} from '../../../pipes/SentenceCasePipe';
import {LabelPipe} from '../../../pipes/LabelPipe';
import {PlotType, TabId, TrellisDesign} from '../../store/ITrellising';
import {DatasetViews} from '../../../../security/DatasetViews';
import {MockDatasetViews} from '../../../MockClasses';
import {EMPTY, ScaleTypes, YAxisParameters} from '../../store';
import {LogarithmicScalePipe, SettingsPipe} from '../../../pipes';

describe('GIVEN PlotService class', () => {
    let service, titleService, xAxisOption, yAxisOption, plot, trellisDesign;
    let spy: Spy;
    const tabId = TabId.LAB_BOXPLOT;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                PlotService,
                TitleService,
                {
                    provide: AxisLabelService,
                    useClass: AxisLabelService,
                    deps: [SentenceCasePipe, LabelPipe]
                },
                {provide: DatasetViews, useClass: MockDatasetViews},
                SentenceCasePipe,
                LabelPipe,
                LogarithmicScalePipe,
                SettingsPipe]
        });
        service = TestBed.get(PlotService);
        titleService = TestBed.get(TitleService);
        spy = spyOn(titleService, 'generateTitle');
        spy = spyOn(titleService, 'generateNewApproachTitle');
    });

    describe('WHEN plot title is requested', () => {
        describe('AND plot is not initialized', () => {
            it('THEN undefined is returned', () => {
                expect(service.getPlotTitle(null, null, null, null, null))
                    .toEqual(undefined);
            });
        });
        describe('AND plot is initialized', () => {
            beforeEach(() => {
                xAxisOption = fromJS({value: 'some value', stringarg: 'some stringarg'});
                yAxisOption = 'some y axis option';
                trellisDesign = TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES;
            });
            describe('AND plot data is null', () => {
                beforeEach(() => {
                    plot = fromJS({trellising: [], data: null});
                });
                it('THEN generateTitle method is called with correct data', () => {
                    service.getPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId);
                    expect(titleService.generateTitle)
                        .toHaveBeenCalledWith(trellisDesign, List([]), 'some value some stringarg', 'some y axis option', tabId);
                });
            });
            describe('AND plot data is not null', () => {
                describe('AND plot type is SCATTERPLOT or ERRORPLOT', () => {
                    beforeEach(() => {
                        plot = fromJS({
                            trellising: [],
                            data: {xaxisLabel: 'data x label', yaxisLabel: 'data y label'},
                            plotType: PlotType.SCATTERPLOT
                        });
                    });
                    it('THEN generateTitle method is called with correct data', () => {
                        service.getPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId);
                        expect(titleService.generateTitle)
                            .toHaveBeenCalledWith(trellisDesign, List([]), 'data x label', 'data y label', tabId);
                    });
                });
                describe('AND plot type is not SCATTERPLOT or ERRORPLOT', () => {
                    beforeEach(() => {
                        plot = fromJS({
                            trellising: [],
                            data: {xaxisLabel: 'data x label', yaxisLabel: 'data y label'},
                            plotType: PlotType.BARLINECHART
                        });
                    });
                    it('THEN generateTitle method is called with correct data', () => {
                        service.getPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId);
                        expect(titleService.generateTitle)
                            .toHaveBeenCalledWith(trellisDesign, List([]), 'some value some stringarg', 'some y axis option', tabId);
                    });
                });
            });
        });
    });

    describe('WHEN plot title is requested for plot with new approach', () => {
        beforeEach(() => {
            xAxisOption = fromJS({
                groupByOption: 'X_GROUP_BY_OPTION',
                params: {'TIMESTAMP_TYPE': 'DAYS_SINCE_FIRST_DOSE'}
            });
            yAxisOption = fromJS({groupByOption: 'Y_GROUP_BY_OPTION'});
        });
        describe('AND plot is not initialized', () => {
            it('THEN undefined is returned', () => {
                expect(service.getNewApproachPlotTitle(null, null, xAxisOption, yAxisOption,
                    tabId, ScaleTypes.LINEAR_SCALE, false, List()))
                    .toEqual(undefined);
            });
        });
        describe('AND plot is initialized', () => {
            beforeEach(() => {
                trellisDesign = TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES;
            });
            describe('AND plot data is null', () => {
                beforeEach(() => {
                    plot = Map({trellising: [], data: null});
                });
                it('THEN generateTitle method is called with correct data', () => {
                    service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption,
                        tabId, ScaleTypes.LINEAR_SCALE, false, List());
                    expect(titleService.generateNewApproachTitle)
                        .toHaveBeenCalledWith(trellisDesign, [], 'DAYS_SINCE_FIRST_DOSE', 'Y_GROUP_BY_OPTION',
                            false, tabId);
                });
            });
            describe('AND plot data is not null', () => {
                describe('AND plot type is not SCATTERPLOT or ERRORPLOT', () => {
                    beforeEach(() => {
                        plot = Map({
                            trellising: [],
                            data: Map({unit: 'SOME_UNIT'}),
                            plotType: PlotType.BARLINECHART
                        });
                        xAxisOption = fromJS({
                            groupByOption: 'X_GROUP_BY_OPTION',
                            params: {'TIMESTAMP_TYPE': 'DAYS_SINCE_FIRST_DOSE', 'DRUG_NAME': 'STDY4321'}
                        });
                    });
                    describe('AND settings can be applied to the plot', () => {
                        it('THEN generateTitle method is called with correct data', () => {
                            const plotSettings = Map({trellisedBy: 'ANALYTE'});
                            const yAxisWithSetting = 'Y_GROUP_BY_OPTION_PER_ANALYTE';
                            service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId,
                                ScaleTypes.LINEAR_SCALE, false, plotSettings, false, true);
                            expect(titleService.generateNewApproachTitle)
                                .toHaveBeenCalledWith(trellisDesign, [], 'DAYS_SINCE_FIRST_DOSE STDY4321', yAxisWithSetting,
                                    false, tabId);
                        });
                    });
                    describe('AND settings cannot be applied to the plot', () => {
                        it('THEN generateTitle method is called with correct data', () => {
                            service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId,
                                ScaleTypes.LINEAR_SCALE, false, List());
                            expect(titleService.generateNewApproachTitle)
                                .toHaveBeenCalledWith(trellisDesign, [], 'DAYS_SINCE_FIRST_DOSE STDY4321', 'Y_GROUP_BY_OPTION',
                                    false, tabId);
                        });
                    });
                });

                describe('AND plot type is SCATTERPLOT or ERRORPLOT', () => {
                    describe('AND unit is not "(empty)"', () => {

                        beforeEach(() => {
                            plot = Map({
                                trellising: [],
                                data: Map({unit: 'SOME_UNIT'}),
                                plotType: PlotType.SCATTERPLOT
                            });
                        });
                        it('THEN generateTitle method is called with correct data', () => {
                            service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId,
                                ScaleTypes.LINEAR_SCALE, false, List());
                            expect(titleService.generateNewApproachTitle)
                                .toHaveBeenCalledWith(trellisDesign, [], 'DAYS_SINCE_FIRST_DOSE(SOME_UNIT)', '',
                                    false, tabId);
                        });
                    });
                    describe('AND unit is "(empty)"', () => {

                        beforeEach(() => {
                            plot = Map({
                                trellising: [],
                                data: Map({unit: EMPTY}),
                                plotType: PlotType.SCATTERPLOT
                            });
                        });
                        it('THEN generateTitle method is called with correct data', () => {
                            service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId,
                                ScaleTypes.LINEAR_SCALE, false, List());
                            expect(titleService.generateNewApproachTitle)
                                .toHaveBeenCalledWith(trellisDesign, [], 'DAYS_SINCE_FIRST_DOSE(Empty)', '',
                                    false, tabId);
                        });
                    });
                    describe('AND trellising with trellisedBy = MEASUREMENT is not empty', () => {
                        beforeEach(() => {
                            plot = Map({
                                trellising: [{trellisedBy: YAxisParameters.MEASUREMENT, trellisOption: 'SOME_TRELLISING_OPTION'}],
                                data: Map({unit: 'SOME_UNIT'}),
                                plotType: PlotType.SCATTERPLOT
                            });
                        });
                        it('THEN generateTitle method is called with correct data', () => {
                            service.getNewApproachPlotTitle(plot, trellisDesign, xAxisOption, yAxisOption, tabId,
                                ScaleTypes.LINEAR_SCALE, false, List());
                            expect(titleService.generateNewApproachTitle)
                                .toHaveBeenCalledWith(trellisDesign, [{
                                        trellisedBy: YAxisParameters.MEASUREMENT,
                                        trellisOption: 'SOME_TRELLISING_OPTION'
                                    }], 'DAYS_SINCE_FIRST_DOSE(SOME_UNIT)', 'SOME_TRELLISING_OPTION',
                                    false, tabId);
                        });
                    });
                });
            });
        });
    });

});
