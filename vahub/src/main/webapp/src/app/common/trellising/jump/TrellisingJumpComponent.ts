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
