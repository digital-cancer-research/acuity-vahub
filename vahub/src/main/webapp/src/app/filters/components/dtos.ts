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

export interface CheckedValues extends Values {
    checkedValues?: any[];
    uncheckedValues?: any[];
}

export interface Values {
    values?: any[];
}

export interface ValuesWithEmpty extends Values {
    includeEmptyValues?: boolean;
}

export interface Range {
    from: any;
    to: any;
}

export interface RangeWithEmpty extends Range {
    includeEmptyValues: boolean;
}

export enum FILTER_TYPE {
    RANGE,
    RANGE_DATE,
    LIST,
    CHECK_LIST,
        /*
         *  same as CHECK_LIST but it sends the checkbox values that havent been checked to the
         *  server rather than the ones that have been checked so the server can do an NOT query
         */
    CHECK_LIST_INVERSE,
    MAP_LIST,
    MAP_CHECK_LIST,
        /*
         *  same as MAP_CHECK_LIST but it sends the checkbox values that havent been checked to the
         *  server rather than the ones that have been checked so the server can do an NOT query
         */
    MAP_CHECK_LIST_INVERSE,
    STUDY_SPECIFIC_FILTERS,
    MAP_RANGE_DATE,
    MAP_RANGE,
    TOGGLE_LIST,
    COHORT_EDITOR,
        /*
         *  same as CHECK_LIST but checkboxes are not selected by default.
         *  Value is sent to backend when it is checked.
         */
    UNSELECTED_CHECK_LIST

}

