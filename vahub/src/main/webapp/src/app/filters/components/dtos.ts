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

