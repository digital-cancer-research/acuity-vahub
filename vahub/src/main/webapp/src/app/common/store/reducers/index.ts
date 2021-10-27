import * as fromStudySelection from '../../../studyselection/store/reducers/StudySelectionReducer';
import * as fromAvailableStudies from '../../../about/availablestudies/store/reducers/AvailableStudiesReducer';
import {trellisingReducer} from '../../trellising/store/reducer/TrellisingReducer';
import {timelineReducer} from '../../../plugins/timeline/store/TimelineReducer';
import * as fromSingleSubjectView from '../../../plugins/refactored-singlesubject/store/reducers/SingleSubjectViewReducer';
import {ActionReducerMap} from '@ngrx/store';
import {sharedStateReducer} from './SharedStateReducer';
import * as fromDetailsOnDemand from '../../trellising/detailsondemand/store/reducers/DetailsOnDemandReducer';

export const reducers: ActionReducerMap<any> = {
    timelineReducer: timelineReducer,
    trellisingReducer: trellisingReducer,
    singleSubjectReducer: fromSingleSubjectView.reducer,
    studySelection: fromStudySelection.reducer,
    availableStudies: fromAvailableStudies.reducer,
    detailsOnDemand: fromDetailsOnDemand.reducer,
    sharedStateReducer: sharedStateReducer
};

