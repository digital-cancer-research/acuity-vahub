import {AppStore} from '../../../plugins/timeline/store/ITimeline';
import {TabId, TabStore} from '../../trellising/store/ITrellising';
import {StudySelection} from '../../../studyselection/store/reducers/StudySelectionReducer';
import {AvailableStudies} from '../../../about/availablestudies/store/reducers/AvailableStudiesReducer';
import {SingleSubjectState} from '../../../plugins/refactored-singlesubject/store/reducers/SingleSubjectViewReducer';
import {SharedState} from '../reducers/SharedStateReducer';
import {DetailsOnDemandState} from '../../trellising/detailsondemand/store/reducers/DetailsOnDemandReducer';

export interface ApplicationState extends Map<string, any> {
    studySelection: StudySelection;
    trellisingReducer: TabStore;
    timelineReducer: AppStore;
    singleSubjectReducer: SingleSubjectState;
    availableStudies: AvailableStudies;
    sharedStateReducer: SharedState;
    detailsOnDemand: DetailsOnDemandState;
}
