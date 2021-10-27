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

import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import * as  _ from 'lodash';
import {Map} from 'immutable';
import {Store} from '@ngrx/store';

import {SessionEventService} from '../../session/module';
import {AppStore, PageName, TimelineId} from '../timeline/store/ITimeline';
import {CHANGE_STUDY_SELECTION} from '../timeline/store/TimelineAction';
import {TRELLIS_SET_INIT_STATE} from '../../common/trellising/store';
import {StudyService} from '../../common/StudyService';
import {UrlParcerService} from './UrlParcerService';
import {TabName} from '../../common/trellising/store';
import {TrellisingActionCreator} from '../../common/trellising/store/actions/TrellisingActionCreator';
import {UpdateSelectedDatasetsAction} from '../../studyselection/store/actions/StudySelectionActions';
import {TimelineObservables} from '../timeline/store/observable/TimelineObservables';
import {PluginsService} from '../PluginsService';
import {SetColumnsAction} from '../../common/trellising/detailsondemand/store/actions/DetailsOnDemandActions';
import {getColumnNames} from '../../common/utils/Utils';
import {UpdateSelectedSubject} from '../refactored-singlesubject/store/actions/SingleSubjectViewActions';

@Injectable()
export class CanActivatePlugins implements CanActivate {

    constructor(private sessionEventService: SessionEventService,
                private _store: Store<AppStore>,
                private router: Router,
                private studyService: StudyService,
                private urlParcerService: UrlParcerService,
                private timelineObservables: TimelineObservables,
                private pluginsService: PluginsService) {
    }

    canActivate(route: ActivatedRouteSnapshot,
                state: RouterStateSnapshot): Observable<boolean> | boolean {
        const parcedUrlSummary = this.urlParcerService.getParcedUrl(state);
        if (parcedUrlSummary.acuityDatasetId) {
            // TODO request single study instead of the whole list
            return this.studyService.getCombinedStudyInfo().switchMap((res) => {
                const selectedDataset = _.find(res.roisWithPermission, (acl: any) => {
                    return acl.id === parcedUrlSummary.acuityDatasetId;
                });
                if (selectedDataset) {
                    this._store.dispatch(new UpdateSelectedDatasetsAction({
                        dataset: Map(selectedDataset),
                        replace: true
                    }));

                    return this.checkAndLoadDatasets().map((result) => {
                        if (parcedUrlSummary.destinationTabSummary.pageName.indexOf(TabName.SINGLE_SUBJECT_TIMELINE) !== -1) {
                            this.timelineObservables.setTimelineId(TimelineId.SUBJECT_PROFILE);
                            this._store.dispatch(new UpdateSelectedSubject(parcedUrlSummary.subject));
                            this._store.dispatch(TrellisingActionCreator.makeInitialStateOfTimelineAction(
                                this.urlParcerService.getInitialSingleSubjectTimelineState(), TimelineId.SUBJECT_PROFILE));
                        }
                        return result;
                    });
                } else {
                    this._store.dispatch(new UpdateSelectedDatasetsAction({dataset: null, replace: true}));
                    return this.checkAndLoadDatasets();
                }
            });
        } else {
            //this is done cause TimelineFilterComponent subscribes to timeline store while it is not yet initialized
            if (parcedUrlSummary.destinationTabSummary.pageName.indexOf(TabName.SINGLE_SUBJECT_TIMELINE) !== -1) {
                this.timelineObservables.setTimelineId(TimelineId.SUBJECT_PROFILE);
            }
            if (parcedUrlSummary.destinationTabSummary.pageName.indexOf(PageName.TIMELINE) !== -1) {
                this.timelineObservables.setTimelineId(TimelineId.COMPARE_SUBJECTS);
            }
            return this.checkAndLoadDatasets();
        }
    }

    private checkAndLoadDatasets(): Observable<boolean> {
        if (_.isEmpty(this.sessionEventService.currentSelectedDatasets)) {
            this.router.navigate(['/']);
            return Observable.of(false);
        } else {
            return this.studyService.getMetadataInfoObservable(this.sessionEventService.currentSelectedDatasets)
                .switchMap((res) => {
                    this.studyService.metadataInfo = res;

                    this._store.dispatch(new SetColumnsAction({columns: getColumnNames(res)}));

                    this._store.dispatch({
                        type: CHANGE_STUDY_SELECTION,
                        payload: {timelineId: TimelineId.COMPARE_SUBJECTS}
                    });
                    this._store.dispatch({
                        type: CHANGE_STUDY_SELECTION,
                        payload: {timelineId: TimelineId.SUBJECT_PROFILE}
                    });
                    this._store.dispatch({
                        type: TRELLIS_SET_INIT_STATE,
                        payload: {}
                    });
                    this.pluginsService.subscribeToRouter();
                    return Observable.of(true);
                });
        }
    }
}

