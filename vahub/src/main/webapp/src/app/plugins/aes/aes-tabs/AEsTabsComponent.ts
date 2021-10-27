import {Component, Input} from '@angular/core';
import {every} from 'lodash';

import {DatasetViews} from '../../../security/module';
import {AbstractPluginComponent} from '../../AbstractPluginComponent';
import {TabName} from '../../../common/trellising/store/ITrellising';
import {PluginsService} from '../../PluginsService';
import {StudyService} from '../../../common/StudyService';
import {SessionEventService} from '../../../session/event/SessionEventService';

@Component({
    selector: 'aes-tabs',
    templateUrl: 'AEsTabsComponent.html'
})
export class AEsTabsComponent extends AbstractPluginComponent {
    @Input() isDropdown: boolean;

    constructor(public datasetViews: DatasetViews,
                private pluginsService: PluginsService,
                private sessionEventService: SessionEventService) {
        super();
    }

    tabClicked(tab: TabName): void {
        if (this.isDropdown) {
            this.pluginsService.selectedTab.next(tab);
        } else {
            this.pluginsService.selectedTab.next(null);
        }
    }

    isOngoing(): boolean {
        return every(this.sessionEventService.currentSelectedDatasets, dataset => StudyService.isOngoingStudy(dataset.type));
    }
}
