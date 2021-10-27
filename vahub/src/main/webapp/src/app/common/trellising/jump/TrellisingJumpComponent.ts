import {Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {TabId, IJumpLink} from '../store/ITrellising';
import {TrellisingJumpService} from './TrellisingJumpService';
import {Router} from '@angular/router';
import {Location} from '@angular/common';
import {TrellisingDispatcher} from '../store/dispatcher/TrellisingDispatcher';

@Component({
    selector: 'trellis-jump',
    templateUrl: 'TrellisingJumpComponent.html'
})
export class TrellisingJumpComponent implements OnChanges {
    @Input() tabId: TabId;
    @Input() loading: boolean;

    currentTabLinks: IJumpLink[] = [];

    constructor(protected trellisingJumpService: TrellisingJumpService,
                protected router: Router,
                protected location: Location,
                protected trellisingDispatcher: TrellisingDispatcher) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['tabId']) {
            this.currentTabLinks = this.trellisingJumpService.getLinksForCurrentTab(this.tabId);
        }
    }

    jump(link: IJumpLink): void {
        this.trellisingJumpService.presetFiltersForJump(link);
        this.trellisingDispatcher.localResetNotification(link.destinationFilterKey);
        this.router.navigate([link.destinationUrl]);
    }
}
