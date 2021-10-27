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

import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {List} from 'immutable';
import {ColorByLabels, ITrellises, TabId, TrellisCategory} from '../../store';
import {Trellising} from '../../store/Trellising';

@Component({
    selector: 'trellising-control',
    templateUrl: 'ControlComponent.html',
    styleUrls: ['ControlComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlComponent implements OnChanges {
    static ALL = 'All';
    static NONE = 'NONE';

    open = false;
    enableComponent = false;
    option: string;
    options: string[];

    @Input() noStyles: boolean;
    @Input() baseTrellising: List<ITrellises>;
    @Input() trellising: List<ITrellises>;
    @Input() isAllColoringOptionAvailable: boolean;
    @Input() customControlLabel: ColorByLabels; // if some explanation to what the control is controlling is needed
    @Input() tabId: TabId;

    get staticAll(): string {
        return ControlComponent.ALL;
    }

    constructor(private trellisingMiddleware: Trellising) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.trellising) {
            this.option = this.currentSeries();
        }
        if (this.baseTrellising) {
            this.options = this.series();
            this.enableComponent = this.hasSeries();
        }
    }

    updateSeries(event: any): void {
        const newTrellising = <List<ITrellises>>this.trellising.filter((x: ITrellises) => {
            return x.get('category') !== TrellisCategory.NON_MANDATORY_SERIES;
        });
        const filteredBaseTrellising = <List<ITrellises>>this.baseTrellising.filter((trellising: ITrellises) => {
            return trellising.get('trellisedBy') !== ControlComponent.NONE
                && trellising.get('trellisedBy') === event.target.value;
        });
        this.trellisingMiddleware.updateTrellisingOptions(<List<ITrellises>>newTrellising.concat(filteredBaseTrellising), this.tabId);
    }

    toggle(): void {
        this.open = !this.open;
    }

    private series(): string[] {
        return this.baseTrellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).map((x: ITrellises) => x.get('trellisedBy')).toArray();
    }

    private currentSeries(): string {
        const series = this.trellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).map((x: ITrellises) => x.get('trellisedBy')).join(', ');
        return series ? series : this.isAllColoringOptionAvailable ? ControlComponent.ALL : ControlComponent.NONE;
    }

    private hasSeries(): boolean {
        return this.baseTrellising && this.baseTrellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).size > 0 && (this.customControlLabel !== ColorByLabels.PRIOR_THERAPY
            && this.customControlLabel !== ColorByLabels.DATE_OF_DIAGNOSIS);
    }
}
