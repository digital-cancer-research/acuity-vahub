import {Component, Input} from '@angular/core';
import {UserPermissions} from '../../../security/UserPermissions';
import {DatasetViews} from '../../../security/DatasetViews';
import {StudyService} from '../../../common/StudyService';
import {SessionEventService} from '../../../session/event/SessionEventService';
import {TabRecords} from '../../../common/trellising/store/ITrellising';
import {PluginsService} from '../../PluginsService';
import {getTabId} from '../../../common/store/reducers/SharedStateReducer';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';

@Component({
    selector: 'ssv-tabs',
    templateUrl: 'SingleSubjectTabsComponent.html',
    styleUrls: ['./SingleSubjectTabsComponent.css']
})
export class SingleSubjectTabsComponent {
    tabRecords = new TabRecords();
    @Input() isDropdown: boolean;
    openedTabName$: Observable<string>;

    constructor(public userPermissions: UserPermissions,
                public datasetViews: DatasetViews,
                public sessionEventService: SessionEventService,
                private pluginsService: PluginsService,
                private _store: Store<ApplicationState>) {

        this.openedTabName$ = this._store.select(getTabId)
            .distinctUntilChanged()
            .map((tabId) => {
                if (!tabId) {
                    return '';
                }
                return this.tabRecords.get(tabId).name;
            });
    }

    ngAfterViewInit(): void {
        this.subscribeToTabChanges();
    }

    public isOngoing(): boolean {
        return StudyService.isOngoingStudy(this.sessionEventService.currentSelectedDatasets[0].type);
    }

    private subscribeToTabChanges(): void {
        this.openedTabName$.subscribe((tabName) => {
            setTimeout(() => {
                if (tabName) {
                    if (this.isDropdown) {
                        this.pluginsService.selectedTab.next(tabName);
                    } else {
                        this.pluginsService.selectedTab.next(null);
                    }
                }
            });
        });
    }

}
