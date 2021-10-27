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

// Change study selectino
import {Action} from '@ngrx/store';
export const CHANGE_STUDY_SELECTION = 'CHANGE_STUDY_SELECTION';

// SET THE INITIAL LOADING STATE OF THE TIMELINE
export const UPDATE_INITIAL_OPENING_STATE = 'UPDATE_INITIAL_OPENING_STATE';

// Apply initial opening state to the rest of the store
export const APPLY_INITIAL_OPENING_STATE = 'APPLY_INITIAL_OPENING_STATE';

// Save track initial opening state
export const SAVE_TRACK_INITIAL_OPENING_STATE = 'SAVE_TRACK_INITIAL_OPENING_STATE';

// Initialise timeline state
export const INITIALISE_TIMELINE = 'INITIALISE_TIMELINE';

// Update the selected subjects
export const UPDATE_SELECTED_SUBJECTS = 'UPDATE_SELECTED_SUBJECTS';

// Set possible tracks
export const POSSIBLE_TRACKS = 'POSSIBLE_TRACKS';

// Reset state to initial
export const RESET = 'RESET';

// Set selected tracks
export const SELECTED_TRACKS = 'SELECTED_TRACKS';

export const LOADING = 'LOADING';
// TRACK SELECTION
// {TRACK_NAME, EXPANSION_LEVEL}
// ADD EMPTY TRACKS INTO DISPLAYED SUBJECTS
// THEN PERFORMAN API CALLS
// DISPATCH UPDATE_DATA ACTION
export const SHOW_TRACK = 'SHOW_TRACK';

// REMOVE TRACK FOR EACH SUBJECT
export const HIDE_TRACK = 'HIDE_TRACK';

// select or deselect multiple tracks
export const CHANGE_TRACKS = 'CHANGE_TRACKS';

// EXPAND AND COLLAPSE TRACK
// {TRACK_NAME, SUBJECT_ID}
// FIGURE OUT CURRENT EXPANSION LEVEL, UPDATE POSSIBLE SUBJECTS ON DEFAULT
export const EXPAND_TRACK = 'EXPAND_TRACK';
export const COLLAPSE_TRACK = 'COLLAPSE_TRACK';

// APPLY SORT
// {SORT_BY, DIRECTION}
// PERFORM API CALL TO GET SORTED SUBJECT LIST
export const SORT_SUBJECTS = 'SORT_SUBJECTS';

// UPDATE SORTED POSSIBLE SUBJECTS
export const UPDATE_POSSIBLE_SUBJECTS = 'UPDATE_POSSIBLE_SUBJECTS';

// CHANGE PAGE
// ALSO CAN BE UESED FOR APPLYING FILTER
//{LIMIT, OFFSET}
// EXPANSION LEVELS
// PERFORM API CALL TO GET NEXT BATCH OF SUBJECT DATA
export const CHANGE_PAGE = 'CHANGE_PAGE';

// UPDATE DATA, private action
// NEED TO APPLY ZOOM
export const UPDATE_DATA = 'UPDATE_DATA';

// RELOAD PAGE DATA
export const RELOAD_DATA = 'RELOAD_DATA';

// APPLY ZOOM
// {MIN, MAX}
// CALCULATE ABSOLUTEMIN AND ABSOLUTEMAX
export const APPLY_ZOOM = 'APPLY_ZOOM';

// CHANGE DAY ZERO
// UPDATE DATA, QUERY FOR API
// RESET ZOOM
export const CHANGE_DAY_ZERO = 'CHANGE_DAY_ZERO';

// Change labs yAxis value
export const CHANGE_LABS_YAXIS_VALUE = 'CHANGE_LABS_YAXIS_VALUE';

// Change spirometry yAxis value
export const CHANGE_SPIROMETRY_YAXIS_VALUE = 'CHANGE_SPIROMETRY_YAXIS_VALUE';

// Change ECG yAxis value
export const CHANGE_ECG_YAXIS_VALUE = 'CHANGE_ECG_YAXIS_VALUE';

// Change ECG warnings value
export const CHANGE_ECG_WARNINGS = 'CHANGE_ECG_WARNINGS';

// Change Vitals yAxis value
export const CHANGE_VITALS_YAXIS_VALUE = 'CHANGE_VITALS_YAXIS_VALUE';

// change none population related filters, e.g, AE filter, Conmed filter
export const CHANGE_NONE_POPULATION_FILTER = 'CHANGE_NONE_POPULATION_FILTER';

// change population related filter
export const CHANGE_POPULATION_FILTER = 'CHANGE_POPULATION_FILTER';

// change flag that informs if current timeline initialization is performed after jump from trellising plot
export const CHANGE_PERFORMED_JUMP_FLAG = 'CHANGE_PERFORMED_JUMP_FLAG';

export const CHANGE_DAY_ZERO_OPTIONS = 'CHANGE_DAY_ZERO_OPTIONS';

export const UPDATE_INITIALIZED = 'UPDATE_INITIALIZED';

export const UPDATE_PLOT_BANDS = 'UPDATE_PLOT_BANDS';

export class ChangeStudySelectionAction implements Action {
    readonly type = CHANGE_STUDY_SELECTION;
    constructor(public payload: any) {
    }
}
