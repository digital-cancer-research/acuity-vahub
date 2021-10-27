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

import {Component, Input, OnInit} from '@angular/core';
import {DetailsOnDemandHeightService} from '../common/trellising/detailsondemand/services/DetailsOnDemandHeightService';

@Component({
    selector: 'details-table',
    templateUrl: 'DetailsTableComponent.html',
    styleUrls: ['../common/trellising/detailsondemand/DetailsOnDemandComponent.css', './DetailsTableComponent.css']
})
export class DetailsTableComponent implements OnInit {
    @Input() details: any;

    constructor(private detailsOnDemandHeightService: DetailsOnDemandHeightService) {

    }

    ngOnInit(): void {
        const $draggable = $('.draggable').draggabilly({
            axis: 'y',
            handle: '.details-area-header'
        });

        this.bindToDrag($draggable);
    }

    openOrClose(): void {
        this.detailsOnDemandHeightService.onExpandCollapseButtonPress();
    }

    onWindowResize($event): void {
        this.detailsOnDemandHeightService.adjustTableHeightAfterWindowResize();
    }

    private bindToDrag($draggable: any): void {
        const that = this;
        const draggie = $draggable.data('draggabilly');
        $draggable.on('dragMove', () => that.detailsOnDemandHeightService.onDragBar(draggie));
        that.detailsOnDemandHeightService.openToPreviousHeight();
    }
}
