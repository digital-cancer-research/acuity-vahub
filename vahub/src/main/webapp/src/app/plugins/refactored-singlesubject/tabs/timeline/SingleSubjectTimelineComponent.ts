import {Component, OnInit, OnDestroy, ChangeDetectionStrategy} from '@angular/core';
import {Store} from '@ngrx/store';
import {Subscription} from 'rxjs/Subscription';
import {AppStore, TimelineId} from '../../../timeline/store/ITimeline';
import {FilterEventService} from '../../../../filters/event/FilterEventService';
import {SingleSubjectModel} from '../../../refactored-singlesubject/SingleSubjectModel';
import {AbstractTimelineComponent} from '../../../timeline/view/AbstractTimelineComponent';
import {TimelineTrackService} from '../../../timeline/config/trackselection/TimelineTrackService';
import {TimelineConfigurationService} from '../../../timeline/config/tracksconfig/TracksConfigurationService';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../../timeline/store/dispatcher/TimelineDispatcher';
import {TimelineObservables} from '../../../timeline/store/observable/TimelineObservables';
import {SingleSubjectTimelineTrackDataService} from './SingleSubjectTimelineTrackDataService';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {getIsSingleSubjectViewDataLoading, getSelectedSubjectId} from '../../store/reducers/SingleSubjectViewReducer';
import {Observable} from 'rxjs/Observable';

@Component({
    templateUrl: '../../../timeline/view/TimelineComponent.html',
    styleUrls: ['../../../timeline/view/TimelineComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class SingleSubjectTimelineComponent extends AbstractTimelineComponent<SingleSubjectTimelineTrackDataService>
implements OnInit, OnDestroy {
    timelineId: string = TimelineId.SUBJECT_PROFILE;
    private singleSubjectSubscription: Subscription;
    isLoading: Observable<boolean>;
    selectedSubjectId: Observable<string>;
    // Flag for TimelineComponent.html template to change the view behaviour in case of SSV plugin
    isSingleSubjectComponent = true;

    constructor(_store: Store<ApplicationState>,
                dataService: SingleSubjectTimelineTrackDataService,
                filterEventService: FilterEventService,
                private singleSubjectModel: SingleSubjectModel,
                trackService: TimelineTrackService,
                timelineConfigurationService: TimelineConfigurationService,
                protected trellisingDispatcher: TrellisingDispatcher,
                protected timelineDispatcher: TimelineDispatcher,
                public timelineObservables: TimelineObservables) {
        super(_store,
            dataService,
            filterEventService,
            trackService,
            timelineConfigurationService,
            trellisingDispatcher,
            timelineDispatcher,
            timelineObservables);
        this.isLoading = this._store.select(getIsSingleSubjectViewDataLoading);
        this.selectedSubjectId = this._store.select(getSelectedSubjectId);
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.singleSubjectSubscription = this.singleSubjectModel.chosenSubject.subscribe((subjectId: string) => {
            this.timeline.updatePopulation();
        });
        this.calculateViewportHeight();

    }

    ngOnDestroy(): void {
        if (this.singleSubjectSubscription) {
            this.singleSubjectSubscription.unsubscribe();
        }
        super.ngOnDestroy();
    }

    calculateViewportHeight(): void {
        if (window.screen.availHeight >= 820) {
            this.maxHeight = 'calc(100vh - 430px)';
        } else {
            this.maxHeight = 'calc(100vh - 425px)';
        }
    }
}
