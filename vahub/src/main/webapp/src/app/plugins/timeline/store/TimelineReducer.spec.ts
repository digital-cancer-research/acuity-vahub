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

import {AppStore, DayZero, InitialOpeningStateRecord, TimelineRecord, TrackName, TrackRecord} from './ITimeline';
import {timelineReducer} from './TimelineReducer';
import {fromJS, List} from 'immutable';
import {
    APPLY_INITIAL_OPENING_STATE,
    CHANGE_DAY_ZERO_OPTIONS,
    HIDE_TRACK,
    POSSIBLE_TRACKS,
    UPDATE_INITIAL_OPENING_STATE,
    UPDATE_PLOT_BANDS
} from './TimelineAction';
import {AppStoreUtils} from './AppStoreUtils';
import {ActionWithPayload} from '../../../common/trellising/store/actions/TrellisingActionCreator';

describe('GIVEN TimelineReducer', () => {
    const initialOpeningState = AppStoreUtils.buildInitialStore();

    describe('WHEN action type is not defined', () => {
        it('THEN it should return the initial state', () => {
            const action: ActionWithPayload<any> = {
                type: undefined,
                payload: {}
            };

            const nextState = timelineReducer(initialOpeningState, action);

            expect(nextState).toEqual(initialOpeningState);
        });
    });

    describe('WHEN initial opening state action is sent', () => {

        it('THEN update initial opening state in the store', () => {
            const action = {
                type: UPDATE_INITIAL_OPENING_STATE,
                payload: {
                    timelineId: 'compareSubjects',
                    tracks: [
                        {
                            name: TrackName.AES,
                            selected: true,
                            order: 1,
                            expansionLevel: 1
                        }
                    ],
                    performedJumpToTimeline: false,
                    isInitialized: false
                }
            };

            const nextState = timelineReducer(initialOpeningState, action);
            const expectedState = fromJS({
                initialOpeningState: {
                    compareSubjects: new InitialOpeningStateRecord({
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.AES,
                                selected: true,
                                order: 1,
                                expansionLevel: 1,
                                data: []
                            })
                        ])
                    }),
                    subjectProfile: new InitialOpeningStateRecord({
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.SUMMARY,
                                selected: true,
                                order: 1,
                                expansionLevel: 1
                            }),
                            new TrackRecord({
                                name: TrackName.DOSE,
                                selected: true,
                                order: 2,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.AES,
                                selected: true,
                                order: 3,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.HEALTHCARE_ENCOUNTERS,
                                selected: true,
                                order: 4,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.LABS,
                                selected: true,
                                order: 5,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.SPIROMETRY,
                                selected: true,
                                order: 6,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.CONMEDS,
                                selected: true,
                                order: 7,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.EXACERBATION,
                                selected: true,
                                order: 8,
                                expansionLevel: 1
                            }),
                            new TrackRecord({
                                name: TrackName.VITALS,
                                selected: true,
                                order: 9,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.ECG,
                                selected: true,
                                order: 10,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.PRD,
                                selected: true,
                                order: 11,
                                expansionLevel: 1
                            })
                        ])
                    }),
                },
                timelines: {
                    compareSubjects: new TimelineRecord({
                        id: 'compareSubjects'
                    }),
                    subjectProfile: new TimelineRecord({
                        id: 'subjectProfile',
                        showPagination: false
                    })
                }
            });

            expect(nextState.toJS()).toEqual(expectedState.toJS());
        });
    });

    describe('WHEN apply opening state action is sent', () => {

        const stateBeforeApplyInitialOpeningState: AppStore = fromJS({
            initialOpeningState: {
                compareSubjects: new InitialOpeningStateRecord({
                    tracks: List([
                        new TrackRecord({
                            name: TrackName.SUMMARY,
                            selected: true,
                            order: 1,
                            expansionLevel: 1,
                            data: []
                        })
                    ])
                })
            },
            timelines: {
                compareSubjects: new TimelineRecord({
                    id: 'compareSubjects',
                    tracks: List([
                        new TrackRecord({
                            name: TrackName.SUMMARY,
                            selected: false,
                            expansionLevel: 1,
                            data: [],
                            order: 1
                        }),
                        new TrackRecord({
                            name: TrackName.AES,
                            selected: false,
                            expansionLevel: 1,
                            data: [],
                            order: 2
                        })
                    ])
                }),
                subjectProfile: new TimelineRecord({
                    id: 'subjectProfile',
                    showPagination: false
                })
            }
        });

        it('THEN apply initial opening state in the store', () => {
            const action: ActionWithPayload<any> = {
                type: APPLY_INITIAL_OPENING_STATE,
                payload: {
                    timelineId: 'compareSubjects'
                }
            };

            const nextState = timelineReducer(stateBeforeApplyInitialOpeningState, action);
            const expectedState = fromJS({
                initialOpeningState: {
                    compareSubjects: new InitialOpeningStateRecord({
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.SUMMARY,
                                selected: true,
                                order: 1,
                                expansionLevel: 1,
                                data: [],
                            })
                        ])
                    }),
                },
                timelines: {
                    compareSubjects: new TimelineRecord({
                        id: 'compareSubjects',
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.SUMMARY,
                                selected: true,
                                expansionLevel: 1,
                                data: [],
                                order: 1
                            }),
                            new TrackRecord({
                                name: TrackName.AES,
                                selected: false,
                                expansionLevel: 1,
                                data: [],
                                order: 2
                            })
                        ])
                    }),
                    subjectProfile: new TimelineRecord({
                        id: 'subjectProfile',
                        showPagination: false
                    })
                }
            });
            expect(nextState.toJS()).toEqual(expectedState.toJS());
        });
    });
    describe('WHEN change day zero options action is sent', () => {

        it('THEN updates dayZeroOptions with correct values', () => {
            const dayZeroOptions = fromJS([{
                'value': DayZero.DAYS_SINCE_FIRST_DOSE,
                'intarg': null,
                'stringarg': null
            }, {
                'value': DayZero.DAYS_SINCE_RANDOMISATION,
                'intarg': null,
                'stringarg': null
            }]);
            const action: ActionWithPayload<any> = {
                type: CHANGE_DAY_ZERO_OPTIONS,
                payload: {
                    timelineId: 'compareSubjects',
                    dayZeroOptions: dayZeroOptions
                }
            };

            const nextstate = timelineReducer(initialOpeningState, action);
            let expectedState = initialOpeningState.setIn(['timelines', 'compareSubjects', 'dayZeroOptions'], dayZeroOptions);
            expectedState = fromJS(expectedState);

            expect(nextstate.toJS()).toEqual(expectedState.toJS());
        });

    });

    describe('WHEN possible track action is sent', () => {

        it('THEN update possible tracks in the timeline state', () => {
            const action: ActionWithPayload<any> = {
                type: POSSIBLE_TRACKS,
                payload: {
                    timelineId: 'compareSubjects',
                    tracks: [
                        {
                            name: TrackName.AES,
                            selected: true,
                            expansionLevel: 1,
                            order: 1
                        }
                    ]
                }
            };

            const nextState = timelineReducer(initialOpeningState, action);
            const expectedState = fromJS({
                initialOpeningState: {
                    compareSubjects: new InitialOpeningStateRecord({
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.SUMMARY,
                                selected: true,
                                order: 1,
                                expansionLevel: 1
                            })
                        ])
                    }),
                    subjectProfile: new InitialOpeningStateRecord({
                        tracks: List([
                            new TrackRecord({
                                name: TrackName.SUMMARY,
                                selected: true,
                                order: 1,
                                expansionLevel: 1
                            }),
                            new TrackRecord({
                                name: TrackName.DOSE,
                                selected: true,
                                order: 2,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.AES,
                                selected: true,
                                order: 3,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.HEALTHCARE_ENCOUNTERS,
                                selected: true,
                                order: 4,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.LABS,
                                selected: true,
                                order: 5,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.SPIROMETRY,
                                selected: true,
                                order: 6,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.CONMEDS,
                                selected: true,
                                order: 7,
                                expansionLevel: 3
                            }),
                            new TrackRecord({
                                name: TrackName.EXACERBATION,
                                selected: true,
                                order: 8,
                                expansionLevel: 1
                            }),
                            new TrackRecord({
                                name: TrackName.VITALS,
                                selected: true,
                                order: 9,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.ECG,
                                selected: true,
                                order: 10,
                                expansionLevel: 2
                            }),
                            new TrackRecord({
                                name: TrackName.PRD,
                                selected: true,
                                order: 11,
                                expansionLevel: 1
                            })
                        ])
                    })
                },
                timelines: {
                    compareSubjects: new TimelineRecord({
                        id: 'compareSubjects',
                        tracks: List([new TrackRecord(
                            {
                                name: TrackName.AES,
                                selected: true,
                                expansionLevel: 1,
                                order: null,
                                data: []
                            }
                        )])
                    }),
                    subjectProfile: new TimelineRecord({
                        id: 'subjectProfile',
                        showPagination: false
                    })
                }
            });

            expect(nextState.toJS()).toEqual(expectedState.toJS());
        });
    });

    describe('WHEN UPDATE_PLOT_BANDS action is sent', () => {
        it('THEN update plotBands', () => {
            const plotBands = fromJS([{from: 1, to: 2}]);
            const action: ActionWithPayload<any> = {
                type: UPDATE_PLOT_BANDS,
                payload: {
                    timelineId: 'compareSubjects',
                    eventData: {
                        point: {
                            high: 2,
                            low: 1
                        },
                        ctrlKey: false
                    }
                }
            };

            const nextState = timelineReducer(initialOpeningState, action);
            const expectedState = initialOpeningState.setIn(['timelines', 'compareSubjects', 'plotBands'], plotBands);
            expect(nextState.toJS()).toEqual(expectedState.toJS());
        });
    });

    describe('WHEN HIDE_TRACK action is sent', () => {

        let loadedState;

        beforeEach(() => {
            const tracks = [
                {
                    name: TrackName.AES,
                    selected: true,
                    order: 1,
                    expansionLevel: 1,
                },
                {
                    name: TrackName.CONMEDS,
                    selected: true,
                    order: 2,
                    expansionLevel: 1,
                },
                {
                    name: TrackName.DOSE,
                    selected: true,
                    order: 3,
                    expansionLevel: 1,
                }
            ];

            loadedState = initialOpeningState.withMutations(state => {
                state.setIn(['timelines', 'compareSubjects', 'tracks'], fromJS(tracks));
            });
        });

        it('THEN the order is updated', () => {
            const action: ActionWithPayload<any> = {
                type: HIDE_TRACK,
                payload: {
                    timelineId: 'compareSubjects',
                    tracks: {
                        track: fromJS({
                            name: TrackName.DOSE
                        })
                    }
                }
            };

            const nextState = timelineReducer(loadedState, action);
            const expectedTracks = [
                {
                    name: TrackName.AES,
                    selected: true,
                    order: 1,
                    expansionLevel: 1,
                },
                {
                    name: TrackName.CONMEDS,
                    selected: true,
                    order: 2,
                    expansionLevel: 1,
                },
                {
                    name: TrackName.DOSE,
                    selected: false,
                    order: null,
                    expansionLevel: 1,
                }
            ];
            expect(nextState.getIn(['timelines', 'compareSubjects', 'tracks']).toJS()).toEqual(expectedTracks);
        });
    });

});
