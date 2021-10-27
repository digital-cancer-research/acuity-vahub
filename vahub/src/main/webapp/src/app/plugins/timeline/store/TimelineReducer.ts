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

import {Iterable, List, Map, OrderedMap, fromJS} from 'immutable';
import {
    AppStore, DayZero, IHighlightedPlotArea, InitializedTimeline,
    ISubject, ITimelinePage,
    ITrack,
    ITrackDataPoint,
    IZoom,
    PageRecord, PossibleSubjects, PossibleTracks,
    SubjectRecord,
    TimelineId,
    TrackRecord,
    ZoomRecord
} from './ITimeline';
import {
    APPLY_INITIAL_OPENING_STATE,
    APPLY_ZOOM,
    CHANGE_DAY_ZERO,
    CHANGE_DAY_ZERO_OPTIONS,
    CHANGE_ECG_WARNINGS,
    CHANGE_ECG_YAXIS_VALUE,
    CHANGE_LABS_YAXIS_VALUE,
    CHANGE_PAGE,
    CHANGE_PERFORMED_JUMP_FLAG,
    CHANGE_SPIROMETRY_YAXIS_VALUE,
    CHANGE_STUDY_SELECTION,
    CHANGE_TRACKS,
    CHANGE_VITALS_YAXIS_VALUE,
    COLLAPSE_TRACK,
    EXPAND_TRACK,
    HIDE_TRACK,
    UPDATE_PLOT_BANDS,
    LOADING,
    POSSIBLE_TRACKS,
    RESET,
    SAVE_TRACK_INITIAL_OPENING_STATE,
    SHOW_TRACK,
    UPDATE_DATA,
    UPDATE_INITIAL_OPENING_STATE,
    UPDATE_INITIALIZED,
    UPDATE_POSSIBLE_SUBJECTS
} from './TimelineAction';
import {AppStoreUtils} from './AppStoreUtils';
import {TrackUtils} from '../track/TrackUtils';
import {ActionWithPayload} from '../../../common/trellising/store/actions/TrellisingActionCreator';
import {isEqual} from 'lodash';

const initialState: AppStore = AppStoreUtils.buildInitialStore();

export function timelineReducer(currentState: AppStore = initialState, actionToApply: ActionWithPayload<any>): Map<string, any> {
    switch (actionToApply.type) {
        case CHANGE_STUDY_SELECTION:
            return changeStudySelection(currentState, actionToApply);
        case UPDATE_INITIAL_OPENING_STATE:
            return setInitialOpeningState(currentState, actionToApply);
        case APPLY_INITIAL_OPENING_STATE:
            return applyInitialOpeningState(currentState, actionToApply);
        case SAVE_TRACK_INITIAL_OPENING_STATE:
            return saveTrackInitialOpeningState(currentState, actionToApply);
        case UPDATE_POSSIBLE_SUBJECTS:
            return setPossibleSubjects(currentState, actionToApply);
        case POSSIBLE_TRACKS:
            return setPossibleTracks(currentState, actionToApply);
        case RESET:
            return resetState();
        case LOADING:
            return setLoading(currentState, actionToApply);
        case CHANGE_TRACKS:
            return changeTracks(currentState, actionToApply);
        case SHOW_TRACK:
            return showTrack(currentState, actionToApply);
        case HIDE_TRACK:
            return hideTrack(currentState, actionToApply);
        case UPDATE_DATA:
            return updateData(currentState, actionToApply);
        case CHANGE_PAGE:
            return changePage(currentState, actionToApply);
        case APPLY_ZOOM:
            return updateZoom(currentState, actionToApply);
        case EXPAND_TRACK:
        case COLLAPSE_TRACK:
            return expandOrCollapseTrack(currentState, actionToApply);
        case CHANGE_LABS_YAXIS_VALUE:
            return changeLabsYAxisValue(currentState, actionToApply);
        case CHANGE_SPIROMETRY_YAXIS_VALUE:
            return changeSpirometryYAxisValue(currentState, actionToApply);
        case CHANGE_ECG_YAXIS_VALUE:
            return changeEcgYAxisValue(currentState, actionToApply);
        case CHANGE_ECG_WARNINGS:
            return changeEcgWarnings(currentState, actionToApply);
        case CHANGE_VITALS_YAXIS_VALUE:
            return changeVitalsYAxisValue(currentState, actionToApply);
        case CHANGE_DAY_ZERO:
            return changeDayZero(currentState, actionToApply);
        case CHANGE_PERFORMED_JUMP_FLAG:
            return changePerformedJumpFlag(currentState, actionToApply);
        case CHANGE_DAY_ZERO_OPTIONS:
            return changeDayZeroOptions(currentState, actionToApply);
        case UPDATE_PLOT_BANDS:
            return updatePlotBands(currentState, actionToApply);
        case UPDATE_INITIALIZED:
            return changedIsInitialized(currentState, actionToApply);
        default:
            return currentState;
    }

    /**
     * Change study selection by clearing existing initial opening state
     */
    function changeStudySelection(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId}>): Map<string, any> {
        return state.setIn(['initialOpeningState', action.payload.timelineId],
            initialState.getIn(['initialOpeningState', action.payload.timelineId]));
    }

    /**
     * Set the initial opening state of the timeline plot
     */
    function setInitialOpeningState(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        return state.withMutations(map => {
            const tracks: ITrack[] = action.payload.tracks.map((track) => {
                return new TrackRecord({
                    name: track.name,
                    order: track.order,
                    expansionLevel: track.expansionLevel,
                    selected: true,
                    data: []
                });
            });

            map.setIn(['initialOpeningState', action.payload.timelineId, 'tracks'], List<ITrack>(tracks));
            map.setIn(['timelines', action.payload.timelineId, 'performedJumpToTimeline'], action.payload.performedJumpToTimeline);
            map.setIn(['timelines', action.payload.timelineId, 'isInitialized'], action.payload.isInitialized);
            map.setIn(['timelines', action.payload.timelineId, 'tracks'], List<ITrack>());
            map.setIn(['timelines', action.payload.timelineId, 'subjects'], Map<string, ISubject>());
            map.setIn(['timelines', action.payload.timelineId, 'displayedSubjects'], List<string>());
        });
    }

    /**
     * Save the initial opening state of the timeline plot
     *  1. collect opening track details
     *  2. set track opening state in the initial opening state
     */
    function saveTrackInitialOpeningState(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId}>): Map<string, any> {
        return state.withMutations(map => {
            // get tracks first
            const tracks: List<ITrack> = map.getIn(['timelines', action.payload.timelineId, 'tracks']);
            const initialTracks = tracks.filter((track) => {
                return track.selected;
            }).map((track) => {
                return new TrackRecord({
                    name: track.name,
                    order: track.order,
                    expansionLevel: track.expansionLevel,
                    selected: track.selected,
                    data: []
                });
            });
            map.setIn(['initialOpeningState', action.payload.timelineId, 'tracks'], initialTracks);
        });
    }

    /**
     * Set possible tracks for the timeline plot
     */
    function setPossibleTracks(state: AppStore, action: ActionWithPayload<PossibleTracks>): Map<string, any> {
        const tracks = action.payload.tracks.map((track) => {
            return new TrackRecord({
                name: track.name,
                order: null,
                expansionLevel: track.expansionLevel,
                selected: track.selected,
                data: []
            });
        });

        return state.setIn(['timelines', action.payload.timelineId, 'tracks'], List<ITrack>(tracks));
    }

    /**
     * Apply the initial opening state to the timeline plot state
     *  1. update each track in timeline
     */
    function applyInitialOpeningState(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId}>): Map<string, any> {
        return state.withMutations(map => {
            // add empty tracks to possible tracks
            const tracks: List<ITrack> = map.getIn(['timelines', action.payload.timelineId, 'tracks']);

            const initialOpeningStateTracks: List<ITrack> =
                map.getIn(['initialOpeningState', action.payload.timelineId, 'tracks']);

            const newTracks: List<ITrack> = <List<ITrack>> tracks.map((track: ITrack) => {
                const initialOpeningStateTrack = initialOpeningStateTracks.find((initialTrack) => {
                    return initialTrack.name === track.name;
                });
                if (initialOpeningStateTrack) {
                    return new TrackRecord({
                        name: track.name,
                        order: track.order ? track.order : initialOpeningStateTrack.order,
                        expansionLevel: initialOpeningStateTrack.expansionLevel,
                        selected: true,
                        data: []
                    });
                } else {
                    return track;
                }
            });
            map.setIn(['timelines', action.payload.timelineId, 'tracks'], newTracks);
        });
    }

    /**
     * Set the possible subjects
     * 1. set possible subjects
     * 2. reset page
     * 3. clear displayed subjects
     */
    function setPossibleSubjects(state: AppStore, action: ActionWithPayload<PossibleSubjects>): Map<string, any> {
        return state.withMutations(map => {
            const tracks: List<ITrack> = map.getIn(['timelines', action.payload.timelineId, 'tracks']);

            const selectedTracks: List<ITrack> = <List<ITrack>> tracks.filter((track: ITrack) => {
                return track.selected;
            });

            // set possible subjects
            const possibleSubjects = action.payload.subjects.reduce((possibleSubject, subjectId) => {
                let subjectTracks = selectedTracks.map((track: ITrack) => {
                    return track.set('data', []);
                });
                // update the track expandsions
                const originalSubject = map.getIn(['timelines', action.payload.timelineId, 'subjects', subjectId]);
                if (originalSubject) {
                    subjectTracks = <List<ITrack>>subjectTracks.map((subjectTrack: ITrack) => {
                        const track: ITrack = originalSubject.tracks.find(item => {
                            return item.name === subjectTrack.name;
                        });
                        if (track) {
                            return subjectTrack.setIn(['expansionLevel'], track.expansionLevel);
                        } else {
                            return subjectTrack;
                        }
                    });
                }

                return possibleSubject.set(subjectId, new SubjectRecord({
                    subjectId: subjectId,
                    tracks: subjectTracks
                }));

            }, OrderedMap<string, ISubject>());

            map.setIn(['timelines', action.payload.timelineId, 'subjects'], possibleSubjects);

            // reset page
            map.setIn(['timelines', action.payload.timelineId, 'page'], new PageRecord({
                limit: -1,
                offset: -1
            }));

            // clear displayed subjects
            map.setIn(['timelines', action.payload.timelineId, 'displayedSubjects'], List<string>());
        });
    }

    /**
     * Set the state of the timeline plot to initial state when leaving currently viewed study
     */
    function resetState(): Map<string, any> {
        return initialState;
    }

    /**
     * Set the state of the timeline plot to loading
     */
    function setLoading(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, loading: boolean}>): Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'loading'], action.payload.loading);
    }

    /**
     * Select and deselect multiple tracks
     */
    function changeTracks(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        let currState: AppStore = state;
        for (const track in action.payload.tracks) {
            if (action.payload.tracks[track].selected) {
                currState = <AppStore>showTrack(currState, {
                    type: SHOW_TRACK,
                    payload: {timelineId: action.payload.timelineId, tracks: action.payload.tracks[track]}
                });
            } else {
                currState = <AppStore>hideTrack(currState, {
                    type: HIDE_TRACK,
                    payload: {timelineId: action.payload.timelineId, tracks: action.payload.tracks[track]}
                });
            }
        }

        return currState;
    }

    /**
     * Show track
     * 1. change selected status in track
     * 2. add track to each subject
     */
    function showTrack(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        return state.withMutations(map => {
            // get highest track order
            const currentTrackOrder: number = action.payload.tracks.order;

            // change track selection order
            const tracks: List<ITrack> = map.getIn(['timelines', action.payload.timelineId, 'tracks'])
                .map((track: ITrack) => {
                    if (track.name === action.payload.tracks.track.name) {
                        return <ITrack>track.set('selected', true).set('order', currentTrackOrder);
                    } else {
                        return track;
                    }
                });
            map.setIn(['timelines', action.payload.timelineId, 'tracks'], tracks);

            // add track to each subject
            const possibleSubjects: Map<string, ISubject> = map.getIn(['timelines', action.payload.timelineId, 'subjects']);
            const modifiedSubjects: Map<string, ISubject> = <Map<string, ISubject>>possibleSubjects.map((subject: ISubject) => {
                const subjectTracks: List<ITrack> = <List<ITrack>> subject.tracks.map((track: ITrack) => {
                    if (track.name === action.payload.tracks.track.name) {
                        return new TrackRecord({
                            name: action.payload.tracks.track.name,
                            expansionLevel: action.payload.tracks.track.expansionLevel,
                            selected: true,
                            order: currentTrackOrder,
                            data: []
                        });
                    } else {
                        return track;
                    }
                });

                return <ISubject>new SubjectRecord({
                    subjectId: subject.subjectId,
                    tracks: subjectTracks
                });
            });

            map.setIn(['timelines', action.payload.timelineId, 'subjects'], modifiedSubjects);
        });
    }

    /**
     * Hide track
     * 1. set selected status to false in track
     * 2. remove track from each subject
     */
    function hideTrack(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        return state.withMutations(map => {
            const tracks: List<ITrack> = map.getIn(['timelines', action.payload.timelineId, 'tracks'])
                .sort((track1, track2) => track1.order - track2.order)
                .map((track: ITrack) => {
                    if (track.get('name') === action.payload.tracks.track.get('name')) {
                        return track.set('selected', false).set('order', null);
                    }
                    return track;
                })
                .sort(track => track.name);

            map.setIn(['timelines', action.payload.timelineId, 'tracks'], tracks);

            // remove track from each subject
            const possibleSubjects: OrderedMap<string, ISubject> = map.getIn(['timelines', action.payload.timelineId, 'subjects']);
            const modifiedSubjects: OrderedMap<string, ISubject> =
                <OrderedMap<string, ISubject>>possibleSubjects.map((subject: ISubject) => {
                    return <ISubject>new SubjectRecord({
                        subjectId: subject.subjectId,
                        tracks: subject.tracks.filter((track: ITrack) => {
                            return track.name !== action.payload.tracks.track.name;
                        })
                    });
                });

            map.setIn(['timelines', action.payload.timelineId, 'subjects'], modifiedSubjects);
        });
    }

    /**
     *  Update subject track data
     */
    function updateData(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        return state.withMutations(map => {
            action.payload.subjects.forEach((subject: ISubject) => {
                map.setIn(['timelines', action.payload.timelineId, 'subjects', subject.subjectId],
                    <ISubject>new SubjectRecord({
                        subjectId: subject.subjectId,
                        tracks: subject.tracks
                    }));
            });
        });
    }

    /**
     * Change page
     * 1. set page limit and offset
     * 2. update displayed subjects
     */
    function changePage(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, page: ITimelinePage}>): Map<string, any> {
        return state.withMutations(map => {
            // set limit and offset
            map.setIn(['timelines', action.payload.timelineId, 'page'],
                new PageRecord({
                    limit: action.payload.page.limit,
                    offset: action.payload.page.offset
                }));

            // get the new page of subject ids
            const displayedSubjects: string[] = [];
            const possibleSubjects: Map<string, ISubject> = map.getIn(['timelines', action.payload.timelineId, 'subjects']);
            // when we update page we want to make request for all of the current tracks, so we need to mark them as changed,
            // so request for them will be performed
            const modifiedPossibleSubjects: Map<string, ISubject> = <Map<string, ISubject>>possibleSubjects.map((subject: ISubject) => {
                return <ISubject>new SubjectRecord({
                    subjectId: subject.subjectId,
                    tracks: subject.tracks.map((track: ITrack) =>  track.set('changed', true))
                });
            });
            const slicedPossibleSubjects: Iterable<string, ISubject> = modifiedPossibleSubjects
                .slice(action.payload.page.offset, (action.payload.page.limit + action.payload.page.offset));
            slicedPossibleSubjects.forEach((subject: ISubject, subjectId: string) => {
                displayedSubjects.push(subjectId);
            });

            // update subjects: track's `changed` property is set to true
            map.setIn(['timelines', action.payload.timelineId, 'subjects'], modifiedPossibleSubjects);
            // update displayed subjects
            map.setIn(['timelines', action.payload.timelineId, 'displayedSubjects'], List(displayedSubjects));

        });
    }

    /**
     * Update zoom
     * 1. update zoom to min and max values of all tracks, if action zoom payload is undefined
     * 2. update zoom using action zoom payload if it is not undefined
     */
    function updateZoom(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, zoom: any}>): Map<string, any> {
        if (action.payload.zoom) {
            return state.setIn(['timelines', action.payload.timelineId, 'zoom'], action.payload.zoom);
        } else {
            const possibleSubjects: OrderedMap<string, ISubject> = state.getIn(['timelines', action.payload.timelineId, 'subjects']);
            const zoomRange: any = possibleSubjects.reduce((range: any, subject: ISubject) => {
                subject.tracks.forEach((track: ITrack) => {
                    if (track.data) {
                        track.data.forEach((dataPoint: ITrackDataPoint) => {
                            const startDayHour = dataPoint.start.dayHour;
                            if (startDayHour > range.absMax) {
                                range.absMax = startDayHour;
                            }

                            if (startDayHour < range.absMin) {
                                range.absMin = startDayHour;
                            }

                            if (dataPoint.end) {
                                const endDayHour = dataPoint.end.dayHour;
                                if (endDayHour > range.absMax) {
                                    range.absMax = endDayHour;
                                }
                            }
                        });
                    }
                });

                return range;
            }, {absMax: 0, absMin: 0});

            const diff = TrackUtils.getDiff(zoomRange.absMin, zoomRange.absMax, 0.05);
            zoomRange.absMin -= diff;
            zoomRange.absMax += diff;

            if (zoomRange.absMax !== zoomRange.absMin) {
                const existingZoom: IZoom = state.getIn(['timelines', action.payload.timelineId, 'zoom']);
                if (existingZoom !== null && existingZoom.zoomed) {
                    zoomRange.zoomMin = existingZoom.zoomMin;
                    zoomRange.zoomMax = existingZoom.zoomMax;
                    zoomRange.zoomed = existingZoom.zoomed;
                } else {
                    if (zoomRange.absMax - diff > 0) {
                        // Min is set to zero if max is positive to avoid start day to be large negative date
                        zoomRange.zoomMin = 0;
                        zoomRange.zoomMax = zoomRange.absMax;
                    } else {
                        // Min is set to its value when max is not positive to avoid having only diff-width interval by default
                        zoomRange.zoomMin = zoomRange.absMin;
                        zoomRange.zoomMax = zoomRange.absMax;
                    }
                }

                return state.setIn(['timelines', action.payload.timelineId, 'zoom'], <IZoom>new ZoomRecord(zoomRange));
            } else {
                return state;
            }
        }
    }

    /**
     * expand or collapse track
     * 1. if payload's subject id is null, then expand/collapse track for all subject
     * 2. if payload's subject id is not null, then expand/collapse track for only that subject
     */
    function expandOrCollapseTrack(state: AppStore, action: ActionWithPayload<any>): Map<string, any> {
        return state.withMutations(map => {
            const subjectIds: string[] = [];
            const possibleSubjects: OrderedMap<string, ISubject> = map.getIn(['timelines', action.payload.timelineId, 'subjects']);

            if (action.payload.expansion.subjectId) {
                subjectIds.push(action.payload.expansion.subjectId);
            } else {
                possibleSubjects.forEach((subject: ISubject, subjectId: string) => {
                    subjectIds.push(subjectId);
                });
            }

            const modifiedSubjects: Map<string, ISubject> = <Map<string, ISubject>>possibleSubjects.map((subject: ISubject) => {
                if (subjectIds.indexOf(subject.subjectId) >= 0) {
                    return <ISubject>new SubjectRecord({
                        subjectId: subject.subjectId,
                        tracks: subject.tracks.map((track: ITrack) => {
                            if (track.name === action.payload.expansion.track &&
                                track.expansionLevel === action.payload.expansion.expansionLevel) {
                                return <ITrack>new TrackRecord({
                                    name: track.name,
                                    expansionLevel: action.payload.expansion.expand ? track.expansionLevel + 1 : track.expansionLevel - 1,
                                    selected: true,
                                    order: track.order
                                });
                            } else {
                                return track.set('changed', false);
                            }
                        })
                    });
                } else {
                    return subject;
                }

            });

            map.setIn(['timelines', action.payload.timelineId, 'subjects'], modifiedSubjects);
        });
    }

    function changeDayZeroOptions(state: AppStore,
                                  action: ActionWithPayload<{timelineId: TimelineId, dayZeroOptions: DayZero[]}>): Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'dayZeroOptions'], action.payload.dayZeroOptions);
    }

    // Change the settings for labs yaxis value
    function changeLabsYAxisValue(state: AppStore,
                                  action: ActionWithPayload<{timelineId: TimelineId, labsYAxisValue: any}>): Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'labsYAxisValue'], action.payload.labsYAxisValue);
    }

    // Change the settings for labs yaxis value
    function changeSpirometryYAxisValue(state: AppStore,
                                        action: ActionWithPayload<{timelineId: TimelineId, spirometryYAxisValue: any}>): Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'spirometryYAxisValue'], action.payload.spirometryYAxisValue);
    }

    // Change the settings for ecg yaxis value
    function changeEcgYAxisValue(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, ecgYAxisValue: any}>)
            : Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'ecgYAxisValue'], action.payload.ecgYAxisValue);
    }

    // Change the settings for ecg warnings
    function changeEcgWarnings(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, ecgWarnings: any}>)
            : Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'ecgWarnings'], action.payload.ecgWarnings);
    }

    // Change the settings for vitals yaxis value
    function changeVitalsYAxisValue(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, vitalsYAxisValue: any}>)
            : Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'vitalsYAxisValue'], action.payload.vitalsYAxisValue);
    }

    // Change the dayZero
    function changeDayZero(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, dayZero: any}>): Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'dayZero'], action.payload.dayZero);
    }

    // Change the jump t timeline flag
    function changePerformedJumpFlag(state: AppStore,
                                     action: ActionWithPayload<{timelineId: TimelineId, performedJumpToTimeline: boolean}>)
            : Map<string, any> {
        return state.setIn(['timelines', action.payload.timelineId, 'performedJumpToTimeline'], action.payload.performedJumpToTimeline);
    }

    // Add plot bands to timeline tracks
    function updatePlotBands(state: AppStore, action: ActionWithPayload<{timelineId: TimelineId, eventData: any}>): Map<string, any> {
        return state.withMutations(map => {
            const {point, ctrlKey} = action.payload.eventData;
            const {low: from, high: to} = point;
            const plotBand: IHighlightedPlotArea = {from, to};
            let plotBands: IHighlightedPlotArea[] = map.getIn(['timelines', action.payload.timelineId, 'plotBands']).toJS();
            const isValuePresent: boolean = plotBands.some(area => isEqual(area, plotBand));

            // Check if ctrl was pressed
            if (ctrlKey) {
                // Check if new value is already there
                if (isValuePresent) {
                    // Remove existing value from array
                    plotBands = plotBands.filter(area => !isEqual(area, plotBand));
                } else {
                    // Add new value to the array
                    plotBands.push(plotBand);
                }
            } else {
                // Clear the array no matter what
                plotBands.length = 0;
                // If new value is not present yet then add it
                if (!isValuePresent) {
                    plotBands.push(plotBand);
                }
            }

            map.setIn(['timelines', action.payload.timelineId, 'plotBands'], fromJS(plotBands));
        });
    }

    // Change the initialization flag
    function changedIsInitialized(state: AppStore, action: ActionWithPayload<InitializedTimeline>): Map<string, any> {
        if (action.payload.timelineId) {
            return state.setIn(['timelines', action.payload.timelineId, 'isInitialized'], action.payload.isInitialized);
        } else {
            return state.withMutations(mutatedState => {
                const timelines = mutatedState.get('timelines').keySeq();
                timelines.forEach((timeline) => {
                    return mutatedState.setIn(['timelines', timeline, 'isInitialized'], action.payload.isInitialized);
                });
            });
        }
    }
}
