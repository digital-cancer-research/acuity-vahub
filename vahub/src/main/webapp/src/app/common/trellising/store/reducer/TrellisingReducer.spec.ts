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

import {trellisingReducer} from './TrellisingReducer';
import {TabStoreUtils} from '../utils/TabStoreUtils';
import {
    TabId, TabStore, TrellisCategory, ITrellises, IPlot, PlotType, IContinuousSelection,
    XAxisOptions, TrellisDesign, ScaleTypes, YAxisParameters
} from '../ITrellising';
import {
    TRELLIS_CHANGE_PAGE,
    TRELLIS_CHANGE_TRELLISING,
    TRELLIS_CLEAR_SELECTIONS,
    TRELLIS_UPDATE_SELECTION,
    TRELLIS_GENERATE_EMPTY_PLOTS,
    TRELLIS_UPDATE_PLOTS,
    TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
    TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
    TRELLIS_CHANGE_AXIS,
    TRELLIS_UPDATE_ZOOM, TRELLIS_CHANGE_AXIS_OPTIONS, TRELLIS_OPTION_CHANGE_TRELLISING, TRELLIS_LOADING,
    TRELLIS_NO_DATA, TRELLIS_INITIALISED, TRELLIS_UPDATE_HEIGHT, TRELLIS_CLEAR_SELECTION, TRELLIS_SCALE_CHANGE
} from '../actions/TrellisingActions';
import {fromJS, List, Map} from 'immutable';
import {InitialTrellisService} from '../services/InitialTrellisService';
import * as _ from 'lodash';
import {PlotUtilsFactory} from '../utils/PlotUtilsFactory';
import {BoxPlotUtilsService} from '../plotutils/BoxPlotUtilsService';
import {TestBed, inject} from '@angular/core/testing';
import {ActionWithPayload} from '../actions/TrellisingActionCreator';

describe('GIVEN trellisingReducer', () => {
    let initialState: TabStore;
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                PlotUtilsFactory,
                BoxPlotUtilsService
            ]
        });
    });
    let boxPlotUtilsService: BoxPlotUtilsService;
    beforeEach(() => {
        initialState = TabStoreUtils.buildInitialStore();
    });

    describe('WHEN state is initialised', () => {
        it('THEN sets trellising all states to false', () => {
            expect(initialState.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allSeries'])).toBeFalsy();
            expect(initialState.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allTrellising'])).toBeFalsy();
        });
    });

    describe('WHEN action type is not defined', () => {
        it('THEN it should return the initial state', () => {
            const action: ActionWithPayload<any> = {
                type: undefined,
                payload: {}
            };

            const nextState = trellisingReducer(initialState, action);

            expect(nextState).toEqual(initialState);
        });
    });

    describe('WHEN action TRELLIS_UPDATE_SELECTION', () => {
        let state1: TabStore;
        let selection: any;
        beforeEach(() => {
            selection = List([{
                trellising: [],
                series: null,
                range: <IContinuousSelection>{
                    xMax: 1,
                    xMin: 0,
                    yMax: 1,
                    yMin: 0
                }
            }]);
            const action: ActionWithPayload<any> = {
                type: TRELLIS_UPDATE_SELECTION,
                payload: {
                    tabId: TabId.LAB_BOXPLOT,
                    selection: selection
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, action);
        });

        it('THEN updates selection', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'selections']).toJS()).toEqual(selection.toJS());
        });

        describe('WHEN action TRELLIS_UPDATE_SELECTION on another labs chart', () => {
            let state2: TabStore;
            beforeEach(() => {
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_SELECTION,
                    payload: {
                        tabId: TabId.LAB_LINEPLOT,
                        selection: selection
                    }
                };
                state2 = <TabStore>trellisingReducer(state1, action);
            });

            it('THEN updates selection and clears other related tabs', () => {
                expect(state2.getIn(['tabs', TabId.LAB_LINEPLOT, 'selections']).toJS()).toEqual(selection.toJS());
                expect(state2.getIn(['tabs', TabId.LAB_BOXPLOT, 'selections']).size).toBe(0);
            });

        });

        describe('WHEN action TRELLIS_CLEAR_SELECTIONS on another labs chart', () => {
            let state2: TabStore;
            beforeEach(() => {
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_CLEAR_SELECTIONS,
                    payload: {
                        tabId: TabId.LAB_LINEPLOT,
                    }
                };
                state2 = <TabStore>trellisingReducer(state1, action);
            });

            it('THEN clears all related tabs', () => {
                expect(state2.getIn(['tabs', TabId.LAB_LINEPLOT, 'selections']).size).toBe(0);
                expect(state2.getIn(['tabs', TabId.LAB_BOXPLOT, 'selections']).size).toBe(0);
            });

        });
    });

    describe('WHEN action TRELLIS_CHANGE_TRELLISING from no trellis options', () => {
        let state1: TabStore;
        beforeEach(() => {
            state1 = <TabStore>initialState.withMutations(state => {
                state.setIn(['tabs', TabId.CONMEDS_BARCHART, 'allTrellising'], false);
                state.setIn(['tabs', TabId.CONMEDS_BARCHART, 'allSeries'], false);
                state.setIn(['tabs', TabId.CONMEDS_BARCHART, 'trellising'], List());
                state.setIn(['tabs', TabId.CONMEDS_BARCHART, 'previousTrellising'], fromJS([
                    {
                        category: TrellisCategory.NON_MANDATORY_SERIES,
                        trellisedBy: 'ONGOING',
                        trellisOptions: ['Y', 'N']
                    }]));
                state.setIn(['tabs', TabId.CONMEDS_BARCHART, 'isInitialised'], true);
            });
        });
        it('THEN goes back to previous trellising option if present', () => {
            const trellisingOptions: List<ITrellises> = fromJS([
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ONGOING',
                    trellisOptions: ['Y', 'N']
                }
            ]);
            const action: ActionWithPayload<any> = {
                type: TRELLIS_CHANGE_TRELLISING,
                payload: {
                    tabId: TabId.CONMEDS_BARCHART,
                    trellising: trellisingOptions,
                    onGoing: false
                }
            };
            const state2 = <TabStore>trellisingReducer(state1, action);
            expect(state2.getIn(['tabs', TabId.CONMEDS_BARCHART, 'trellising'])).toEqual(fromJS([
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ONGOING',
                    trellisOptions: ['Y', 'N']
                }]));
        });
        it('THEN doesnt update the previousTrellising if no options', () => {
            const action: ActionWithPayload<any> = {
                type: TRELLIS_CHANGE_TRELLISING,
                payload: {
                    tabId: TabId.CONMEDS_BARCHART,
                    trellising: List(),
                    onGoing: false
                }
            };
            const state2 = <TabStore>trellisingReducer(state1, action);
            expect(state2.getIn(['tabs', TabId.CONMEDS_BARCHART, 'previousTrellising'])).toEqual(fromJS([
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ONGOING',
                    trellisOptions: ['Y', 'N']
                }]));
        });
    });

    describe('WHEN action CHANGE_TRELLISING given colour by option', () => {
        let trellisingOptions: List<ITrellises>;
        let state1: TabStore;
        beforeEach(() => {
            trellisingOptions = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: 'ON_GOING',
                    trellisOptions: ['Yes', 'No']
                }
            ]);
            const action: ActionWithPayload<any> = {
                type: TRELLIS_CHANGE_TRELLISING,
                payload: {
                    tabId: TabId.CARDIAC_BOXPLOT,
                    trellising: trellisingOptions,
                    onGoing: false
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, action);
        });

        it('THEN updates trellising', () => {
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'trellising']).toJS()).toEqual([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1']
                }
            ]);
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allSeries'])).toBeTruthy();
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allTrellising'])).toBeFalsy();
        });
    });


    describe('WHEN action CHANGE_TRELLISING', () => {
        let trellisingOptions: List<ITrellises>;
        let state1: TabStore;
        beforeEach(() => {
            trellisingOptions = fromJS([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1']
                }
            ]);
            const action: ActionWithPayload<any> = {
                type: TRELLIS_CHANGE_TRELLISING,
                payload: {
                    tabId: TabId.CARDIAC_BOXPLOT,
                    trellising: trellisingOptions,
                    onGoing: false
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, action);
        });

        it('THEN updates trellising', () => {
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'trellising']).toJS()).toEqual([
                {
                    category: TrellisCategory.MANDATORY_TRELLIS,
                    trellisedBy: YAxisParameters.MEASUREMENT,
                    trellisOptions: ['PR', 'HR']
                },
                {
                    category: TrellisCategory.NON_MANDATORY_TRELLIS,
                    trellisedBy: 'ARM',
                    trellisOptions: ['Placebo', 'Drug 1']
                }
            ]);
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allSeries'])).toBeFalsy();
            expect(state1.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'allTrellising'])).toBeFalsy();
        });

        describe('WHEN action CHANGE_PAGE', () => {
            let state2: TabStore;
            beforeEach(() => {
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_PAGE,
                    payload: {
                        tabId: TabId.CARDIAC_BOXPLOT,
                        pageNumber: 1
                    }
                };
                state2 = <TabStore>trellisingReducer(state1, action);
            });

            it('THEN updates the limit offset', () => {
                expect(state2.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'page', 'limit'])).toBe(4);
                expect(state2.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'page', 'offset'])).toBe(1);
            });

            describe('WHEN generate empty plots', () => {
                let state3: TabStore;
                beforeEach(() => {
                    const action: ActionWithPayload<any> = {
                        type: TRELLIS_GENERATE_EMPTY_PLOTS,
                        payload: {
                            tabId: TabId.CARDIAC_BOXPLOT
                        }
                    };
                    state3 = <TabStore>trellisingReducer(state2, action);
                });

                it('THEN updates the plots with null data', () => {
                    const results: any = state3.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'plots']).toJS();
                    expect(results.length).toBe(4);
                    expect(results[0].data).toBeNull();
                    expect(results[1].data).toBeNull();
                    expect(results[2].data).toBeNull();
                    expect(results[3].data).toBeNull();
                });

                describe('WHEN adding plot data', () => {
                    let state4: TabStore;
                    beforeEach(() => {
                        const plotData: List<IPlot> = fromJS([
                            {
                                plotType: PlotType.BOXPLOT,
                                trellising: [
                                    {
                                        category: TrellisCategory.MANDATORY_TRELLIS,
                                        trellisedBy: YAxisParameters.MEASUREMENT,
                                        trellisOption: 'HR'
                                    },
                                    {
                                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                        trellisedBy: 'ARM',
                                        trellisOption: 'Drug 1'
                                    }
                                ],
                                data: Map({value: 1})
                            },
                            {
                                plotType: PlotType.BOXPLOT,
                                trellising: [
                                    {
                                        category: TrellisCategory.MANDATORY_TRELLIS,
                                        trellisedBy: YAxisParameters.MEASUREMENT,
                                        trellisOption: 'PR'
                                    },
                                    {
                                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                        trellisedBy: 'ARM',
                                        trellisOption: 'Drug 1'
                                    }
                                ],
                                data: Map({value: 2})
                            },
                            {
                                plotType: PlotType.BOXPLOT,
                                trellising: [
                                    {
                                        category: TrellisCategory.MANDATORY_TRELLIS,
                                        trellisedBy: YAxisParameters.MEASUREMENT,
                                        trellisOption: 'HR'
                                    },
                                    {
                                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                        trellisedBy: 'ARM',
                                        trellisOption: 'Placebo'
                                    }
                                ],
                                data: Map({value: 3})
                            },
                            {
                                plotType: PlotType.BOXPLOT,
                                trellising: [
                                    {
                                        category: TrellisCategory.MANDATORY_TRELLIS,
                                        trellisedBy: YAxisParameters.MEASUREMENT,
                                        trellisOption: 'PR'
                                    },
                                    {
                                        category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                        trellisedBy: 'ARM',
                                        trellisOption: 'Placebo'
                                    }
                                ],
                                data: Map({value: 4})
                            }
                        ]);
                        const action: ActionWithPayload<any> = {
                            type: TRELLIS_UPDATE_PLOTS,
                            payload: {
                                tabId: TabId.CARDIAC_BOXPLOT,
                                plots: plotData
                            }
                        };
                        state4 = <TabStore>trellisingReducer(state3, action);
                    });
                    it('THEN updates the plots with data', () => {
                        const results: any = state4.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'plots']).toJS();
                        expect(results.length).toBe(4);
                        expect(results[0].data).toEqual({value: 4});
                        expect(results[1].data).toEqual({value: 2});
                        expect(results[2].data).toEqual({value: 3});
                        expect(results[3].data).toEqual({value: 1});
                    });
                    describe('WHEN adding plot data', () => {
                        let state5: TabStore;
                        beforeEach(() => {
                            const plotData: List<IPlot> = fromJS([
                                {
                                    plotType: PlotType.BOXPLOT,
                                    trellising: [
                                        {
                                            category: TrellisCategory.MANDATORY_TRELLIS,
                                            trellisedBy: YAxisParameters.MEASUREMENT,
                                            trellisOption: 'PR'
                                        },
                                        {
                                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                            trellisedBy: 'Placebo',
                                            trellisOption: 'ARM'
                                        }
                                    ],
                                    data: Map({value: 1})
                                },
                                {
                                    plotType: PlotType.BOXPLOT,
                                    trellising: [
                                        {
                                            category: TrellisCategory.MANDATORY_TRELLIS,
                                            trellisedBy: YAxisParameters.MEASUREMENT,
                                            trellisOption: 'PR'
                                        },
                                        {
                                            category: TrellisCategory.NON_MANDATORY_TRELLIS,
                                            trellisedBy: 'ARM',
                                            trellisOption: 'Placebo'
                                        }
                                    ],
                                    data: Map({value: 2})
                                }
                            ]);
                            const action: ActionWithPayload<any> = {
                                type: TRELLIS_UPDATE_PLOTS,
                                payload: {
                                    tabId: TabId.CARDIAC_BOXPLOT,
                                    plots: plotData
                                }
                            };
                            state5 = <TabStore>trellisingReducer(state4, action);
                        });
                        it('THEN updates the plots with data', () => {
                            const results: any = state5.getIn(['tabs', TabId.CARDIAC_BOXPLOT, 'plots']).toJS();
                            expect(results.length).toBe(4);
                            expect(results[0].data).toEqual({value: 2});
                            expect(results[1].data).toEqual([]);
                        });
                    });
                });
            });
        });
    });

    describe('WHEN action TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND', () => {
        let state1: TabStore;
        let detailsOnDemandTableData: any;
        beforeEach(() => {
            detailsOnDemandTableData = {
                tableData: ['a', 'b', 'c']
            };
            const action: ActionWithPayload<any> = {
                type: TRELLIS_UPDATE_EVENT_DETAILS_ON_DEMAND,
                payload: {
                    tabId: TabId.LAB_BOXPLOT,
                    detailsOnDemand: fromJS(detailsOnDemandTableData)
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, action);
        });

        it('THEN updates details on demand data', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'eventDetailsOnDemand']).toJS()).toEqual(detailsOnDemandTableData);
        });
    });

    describe('WHEN action TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND', () => {
        let state1: TabStore;
        let detailsOnDemandTableData: any;
        beforeEach(() => {
            detailsOnDemandTableData = {
                tableData: ['a', 'b', 'c']
            };
            const action: ActionWithPayload<any> = {
                type: TRELLIS_UPDATE_SUBJECT_DETAILS_ON_DEMAND,
                payload: {
                    tabId: TabId.LAB_BOXPLOT,
                    detailsOnDemand: fromJS(detailsOnDemandTableData)
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, action);
        });

        it('THEN updates details on demand data', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'subjectDetailsOnDemand']).toJS()).toEqual(detailsOnDemandTableData);
        });
    });

    describe('WHEN action TRELLIS_CHANGE_AXIS', () => {
        let state1: TabStore;
        let optionPayload: any;
        let zoomPayload: any;
        describe('AND Y axis is changed', () => {

            beforeEach(() => {
                optionPayload = 'SOME_OPTION';
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        option: fromJS(optionPayload),
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, action);
            });

            it('THEN updates y axis option', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'option'])).toEqual(optionPayload);
            });
        });
        describe('AND Y axis is not changed', () => {

            beforeEach(() => {
                optionPayload = 'SOME_OPTION';
                const optionAction: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        option: fromJS(optionPayload),
                        yAxis: true
                    }
                };
                zoomPayload = {
                    absMax: 10,
                    absMin: 0,
                    zoomMax: 8,
                    zoomMin: 3
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, optionAction);
                state1 = <TabStore>trellisingReducer(state1, zoomAction);
                state1 = <TabStore>trellisingReducer(state1, optionAction);
            });

            it('THEN axis option and zoom is not updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });

        describe('AND X axis is changed', () => {
            describe('AND xAxisTextZoomRequired is false for the tab', () => {

                beforeEach(() => {
                    optionPayload = {
                        value: XAxisOptions.STUDY_DEFINED_WEEK,
                        intarg: 1,
                        stringarg: 'somearg'
                    };
                    const action: ActionWithPayload<any> = {
                        type: TRELLIS_CHANGE_AXIS,
                        payload: {
                            tabId: TabId.LAB_BOXPLOT,
                            option: optionPayload,
                            xAxis: true
                        }
                    };
                    state1 = <TabStore>trellisingReducer(initialState, action);
                });

                it('THEN updates x axis option and trellisingDesign', () => {
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'option']).toJS()).toEqual(optionPayload);
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'trellisDesign'])).toEqual(TrellisDesign.CONTINUOUS_OVER_TIME);
                });
            });
            describe('AND xAxisTextZoomRequired is true for the tab', () => {

                beforeEach(() => {
                    optionPayload = {
                        value: XAxisOptions.STUDY_DEFINED_WEEK,
                        intarg: 1,
                        stringarg: 'somearg'
                    };
                    const action: ActionWithPayload<any> = {
                        type: TRELLIS_CHANGE_AXIS,
                        payload: {
                            tabId: TabId.LAB_BOXPLOT,
                            option: optionPayload,
                            xAxis: true
                        }
                    };
                    zoomPayload = {
                        absMax: 10,
                        absMin: 0,
                        zoomMax: 8,
                        zoomMin: 2
                    };
                    state1 = <TabStore>initialState.setIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom'], zoomPayload);
                    state1 = <TabStore>state1.setIn(['tabs', TabId.LAB_BOXPLOT, 'xAxisTextZoomRequired'], true);
                    state1 = <TabStore>trellisingReducer(state1, action);
                });

                it('THEN zoom is not updated', () => {
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom'])).toEqual(zoomPayload);
                });
            });
        });
        describe('AND X axis is not changed', () => {

            beforeEach(() => {
                optionPayload = {
                    value: 'SOME_OPTION',
                    intarg: 2,
                    stringarg: 'somearg'
                };
                const optionAction: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        option: optionPayload,
                        xAxis: true
                    }
                };
                zoomPayload = {
                    absMax: 0,
                    absMin: 10,
                    zoomMax: 2,
                    zoomMin: 8
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        xAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, optionAction);
                state1 = <TabStore>trellisingReducer(state1, zoomAction);
                state1 = <TabStore>trellisingReducer(state1, optionAction);
            });

            it('THEN axis option and zoom is not updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });
    });

    describe('WHEN action TRELLIS_CHANGE_AXIS_OPTIONS', () => {
        let state1: TabStore;
        let optionsPayload: any;
        let zoomPayload: any;
        describe('AND Y axis options are changed', () => {

            beforeEach(() => {
                optionsPayload = ['SOME_OPTION1', 'SOME_OPTION2'];
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        options: fromJS(optionsPayload),
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, action);
            });

            it('THEN updates y axis options and selected option', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'options']).toJS()).toEqual(optionsPayload);
            });
        });
        describe('AND Y axis is not changed', () => {

            beforeEach(() => {
                optionsPayload = ['SOME_OPTION1', 'SOME_OPTION2'];
                const optionsAction: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        options: fromJS(optionsPayload),
                        yAxis: true
                    }
                };
                zoomPayload = {
                    absMax: 0,
                    absMin: 10,
                    zoomMax: 2,
                    zoomMin: 8
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, optionsAction);
                state1 = <TabStore>trellisingReducer(state1, zoomAction);
                state1 = <TabStore>trellisingReducer(state1, optionsAction);
            });

            it('THEN axis option and zoom is not updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });

        describe('AND X axis options are changed', () => {

            beforeEach(() => {
                optionsPayload = [
                    {
                        value: 'SOME_OPTION1',
                        intarg: 1,
                        stringarg: 'somearg1'
                    },
                    {
                        value: 'SOME_OPTION2',
                        intarg: 2,
                        stringarg: 'somearg2'
                    }
                ];
                const action: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        options: fromJS(optionsPayload),
                        xAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, action);
            });

            it('THEN updates x axis options and selected option', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'options']).toJS()).toEqual(optionsPayload);
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'trellisDesign'])).toEqual(TrellisDesign.CONTINUOUS_OVER_TIME);
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'option']).toJS()).toEqual(optionsPayload[0]);
            });
        });
        describe('AND X axis is not changed', () => {

            beforeEach(() => {
                optionsPayload = ['SOME_OPTION1', 'SOME_OPTION2'];
                const optionsAction: ActionWithPayload<any> = {
                    type: TRELLIS_CHANGE_AXIS_OPTIONS,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        options: fromJS(optionsPayload),
                        yAxis: true
                    }
                };
                zoomPayload = {
                    absMax: 0,
                    absMin: 10,
                    zoomMax: 2,
                    zoomMin: 8
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, optionsAction);
                state1 = <TabStore>trellisingReducer(state1, zoomAction);
                state1 = <TabStore>trellisingReducer(state1, optionsAction);
            });

            it('THEN axis option and zoom is not updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });
    });

    describe('WHEN action TRELLIS_UPDATE_ZOOM', () => {
        beforeEach(
            inject([PlotUtilsFactory, BoxPlotUtilsService], (
                _plotUtilsFactory: PlotUtilsFactory, _boxPlotUtilsService: BoxPlotUtilsService) => {
                boxPlotUtilsService = _boxPlotUtilsService;
                spyOn(PlotUtilsFactory, 'getPlotUtilsService').and.callFake(() => {
                    return boxPlotUtilsService;
                });
            })
        );

        let state1: TabStore;
        let zoomPayload: any;

        describe('AND Y axis zoom is passed in payload', () => {

            beforeEach(() => {
                zoomPayload = {
                    absMax: 0,
                    absMin: 10,
                    zoomMax: 2,
                    zoomMin: 8
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        yAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, zoomAction);
            });

            it('THEN y axis zoom is updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });
        describe('AND X axis zoom is passed in payload', () => {

            beforeEach(() => {
                zoomPayload = {
                    absMax: 0,
                    absMin: 10,
                    zoomMax: 2,
                    zoomMin: 8
                };
                const zoomAction: ActionWithPayload<any> = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: zoomPayload,
                        xAxis: true
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, zoomAction);
            });

            it('THEN x axis zoom is updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom']).toJS()).toEqual(zoomPayload);
            });
        });

        describe('AND Y axis zoom is not passed in payload', () => {
            let zoomAction: ActionWithPayload<any>;
            beforeEach(() => {
                zoomAction = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: null,
                        yAxis: true
                    }
                };
                const plotZoom = {
                    x: {
                        min: 1,
                        max: 10
                    },
                    y: {
                        min: 2,
                        max: 8
                    }
                };
                spyOn(boxPlotUtilsService, 'calculateZoomRanges').and.callFake(() => {
                    return plotZoom;
                });
            });
            describe('AND current zoom is not defined', () => {
                beforeEach(() => {
                    state1 = <TabStore>trellisingReducer(initialState, zoomAction);
                });
                it('THEN y axis zoom is updated', () => {
                    const expectedZoom = {absMax: 8, absMin: 2, zoomMax: 8, zoomMin: 2};
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(expectedZoom);
                });
            });
            describe('AND current y axis zoom is defined', () => {
                let currentZoom;
                beforeEach(() => {
                    currentZoom = {
                        absMax: 0,
                        absMin: 10,
                        zoomMax: 7,
                        zoomMin: 3
                    };
                    state1 = <TabStore>initialState.setIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom'], currentZoom);
                    state1 = <TabStore>trellisingReducer(state1, zoomAction);
                });
                it('THEN y axis zoom is not updated', () => {
                    const expectedZoom = {absMax: 8, absMin: 2, zoomMax: 7, zoomMin: 3};
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'yAxis', 'zoom']).toJS()).toEqual(expectedZoom);
                });
            });
        });

        describe('AND X axis zoom is not passed in payload', () => {
            let zoomAction: ActionWithPayload<any>;
            beforeEach(() => {
                zoomAction = {
                    type: TRELLIS_UPDATE_ZOOM,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        zoom: null,
                        xAxis: true
                    }
                };
                const plotZoom = {
                    x: {
                        min: 2,
                        max: 8
                    },
                    y: {
                        min: 1,
                        max: 10
                    }
                };
                spyOn(boxPlotUtilsService, 'calculateZoomRanges').and.callFake(() => {
                    return plotZoom;
                });
            });
            describe('AND current zoom is not defined', () => {
                beforeEach(() => {
                    state1 = <TabStore>trellisingReducer(initialState, zoomAction);
                });
                it('THEN x axis zoom is updated', () => {
                    const expectedZoom = {absMax: 8, absMin: 2, zoomMax: 8, zoomMin: 2};
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom']).toJS()).toEqual(expectedZoom);
                });
            });
            describe('AND current x axis zoom is defined', () => {
                let currentZoom;
                beforeEach(() => {
                    currentZoom = {
                        absMax: 0,
                        absMin: 10,
                        zoomMax: 7,
                        zoomMin: 3
                    };
                    state1 = <TabStore>initialState.setIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom'], currentZoom);
                    state1 = <TabStore>trellisingReducer(state1, zoomAction);
                });
                it('THEN x axis zoom is not updated', () => {
                    const expectedZoom = {absMax: 8, absMin: 2, zoomMax: 7, zoomMin: 3};
                    expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'xAxis', 'zoom']).toJS()).toEqual(expectedZoom);
                });
            });
        });

    });

    describe('WHEN action TRELLIS_OPTION_CHANGE_TRELLISING', () => {
        let state1: TabStore;
        let trellising: any;
        let newTrellising: any;
        let isAllTrellising: any;
        let isAllSeries: any;

        describe('AND trellising is passed into payload', () => {

            beforeEach(() => {
                trellising = fromJS([{
                    category: TrellisCategory.MANDATORY_HIGHER_LEVEL,
                    trellisOptions: ['option1', 'option2'],
                    trellisedBy: 'trellisedby'
                }]);
                isAllTrellising = true;
                isAllSeries = true;
                const trellisingAction: ActionWithPayload<any> = {
                    type: TRELLIS_OPTION_CHANGE_TRELLISING,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        trellising: trellising
                    }
                };
                spyOn(InitialTrellisService, 'isAllTrellising').and.returnValue(isAllTrellising);
                spyOn(InitialTrellisService, 'isAllSeries').and.returnValue(isAllSeries);

                state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
            });

            it('THEN trellising is updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'trellising'])).toEqual(trellising);
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'allTrellising'])).toEqual(isAllTrellising);
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'allSeries'])).toEqual(isAllSeries);
            });
        });

        describe('AND previous trellising is not empty', () => {

            beforeEach(() => {
                trellising = fromJS([{
                    category: TrellisCategory.MANDATORY_HIGHER_LEVEL,
                    trellisOptions: ['option1', 'option2'],
                    trellisedBy: 'trellisedby'
                }]);
                newTrellising = fromJS([{
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisOptions: ['newOption1', 'newOption2'],
                    trellisedBy: 'newTrellisedby'
                }]);
                isAllTrellising = true;
                isAllSeries = true;
                const trellisingAction: ActionWithPayload<any> = {
                    type: TRELLIS_OPTION_CHANGE_TRELLISING,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        trellising: trellising
                    }
                };
                const newTrellisingAction: ActionWithPayload<any> = {
                    type: TRELLIS_OPTION_CHANGE_TRELLISING,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        trellising: newTrellising
                    }
                };
                spyOn(InitialTrellisService, 'isAllTrellising').and.returnValue(isAllTrellising);
                spyOn(InitialTrellisService, 'isAllSeries').and.returnValue(isAllSeries);

                state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
                state1 = <TabStore>trellisingReducer(state1, newTrellisingAction);
            });

            it('THEN trellising is updated', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'trellising'])).toEqual(newTrellising);
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'previousTrellising'])).toEqual(trellising);
            });
        });

    });

    describe('WHEN action TRELLIS_LOADING', () => {
        let state1: TabStore;
        let isLoading: boolean;

        beforeEach(() => {
            isLoading = true;
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_LOADING,
                payload: {
                    tabId: TabId.LAB_BOXPLOT,
                    isLoading: isLoading
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
        });

        it('THEN loading is updated', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'isLoading'])).toEqual(isLoading);
        });

    });

    describe('WHEN action TRELLIS_UPDATE_HEIGHT', () => {
        let state1: TabStore;
        let height: number;

        beforeEach(() => {
            height = 1000;
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_UPDATE_HEIGHT,
                payload: {
                    height: height
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
        });

        it('THEN height is updated', () => {
            expect(state1.get('height')).toEqual(height);
        });

    });

    describe('WHEN action TRELLIS_NO_DATA', () => {
        let state1: TabStore;
        let noData: boolean;

        beforeEach(() => {
            noData = true;
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_NO_DATA,
                payload: {
                    tabId: TabId.LAB_BOXPLOT,
                    noData: noData
                }
            };
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
        });

        it('THEN noData is updated', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'noData'])).toEqual(noData);
        });

    });

    describe('WHEN action TRELLIS_INITIALISED', () => {
        let state1: TabStore;
        let isInitialised: boolean;

        describe('AND tab id is passed into payload', () => {

            beforeEach(() => {
                isInitialised = true;
                const trellisingAction: ActionWithPayload<any> = {
                    type: TRELLIS_INITIALISED,
                    payload: {
                        tabId: TabId.LAB_BOXPLOT,
                        isInitialised: isInitialised
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
            });

            it('THEN current tab initialization is reset', () => {
                expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'isInitialised'])).toEqual(isInitialised);
            });
        });

        describe('AND tab id is not passed into payload', () => {

            beforeEach(() => {
                isInitialised = true;
                const trellisingAction: ActionWithPayload<any> = {
                    type: TRELLIS_INITIALISED,
                    payload: {
                        tabId: null,
                        isInitialised: isInitialised
                    }
                };
                state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
            });

            it('THEN current tab initialization is reset', () => {
                const tabs: TabId[] = state1.get('tabs').keySeq().toArray();
                expect(_.some(tabs, tabss => !state1.getIn(['tabs', tabss, 'isInitialised'])))
                    .toBeFalsy(false);
            });
        });
    });

    describe('WHEN action TRELLIS_CLEAR_SELECTION', () => {
        let state1: TabStore;

        beforeEach(() => {
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_CLEAR_SELECTION,
                payload: {
                    tabId: TabId.LAB_BOXPLOT
                }
            };
            const selection = List([{
                trellising: [],
                series: null,
                range: <IContinuousSelection>{
                    xMax: 1,
                    xMin: 0,
                    yMax: 1,
                    yMin: 0
                }
            }]);
            initialState.setIn(['tabs', TabId.LAB_BOXPLOT, 'selections'], selection);
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
        });

        it('THEN selection is reset', () => {
            expect(state1.getIn(['tabs', TabId.LAB_BOXPLOT, 'selections']).size).toBe(0);
        });
    });

    describe('WHEN action TRELLIS_SCALE_CHANGE', () => {
        let state1: TabStore;

        beforeEach(() => {
            initialState.setIn(['tabs', TabId.ANALYTE_CONCENTRATION, 'availableScaleTypes'],
                [ScaleTypes.LINEAR_SCALE, ScaleTypes.LOGARITHMIC_SCALE]);
        });

        it('THEN selection is changed to logarithmic scale', () => {
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_SCALE_CHANGE,
                payload: {
                    tabId: TabId.ANALYTE_CONCENTRATION,
                    scaleType: ScaleTypes.LOGARITHMIC_SCALE
                }
            };

            initialState.setIn(['tabs', TabId.ANALYTE_CONCENTRATION, 'scaleType'], ScaleTypes.LINEAR_SCALE);
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
            expect(state1.getIn(['tabs', TabId.ANALYTE_CONCENTRATION, 'scaleType'])).toBe(ScaleTypes.LOGARITHMIC_SCALE);
        });

        it('THEN selection is not changed', () => {
            const trellisingAction: ActionWithPayload<any> = {
                type: TRELLIS_SCALE_CHANGE,
                payload: {
                    tabId: TabId.ANALYTE_CONCENTRATION,
                    scaleType: ScaleTypes.LINEAR_SCALE
                }
            };

            initialState.setIn(['tabs', TabId.ANALYTE_CONCENTRATION, 'scaleType'], ScaleTypes.LINEAR_SCALE);
            state1 = <TabStore>trellisingReducer(initialState, trellisingAction);
            expect(state1.getIn(['tabs', TabId.ANALYTE_CONCENTRATION, 'scaleType'])).toBe(ScaleTypes.LINEAR_SCALE);
        });
    });
});
