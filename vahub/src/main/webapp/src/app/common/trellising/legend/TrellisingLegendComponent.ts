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
import {map} from 'lodash';

import {ColorByLabels, ILegend, ILegendEntry, LegendSymbol, TabId} from '../store';
import {AlphabeticOrderByPipe, IntervalsOrderByPipe, MonthsOrderByPipe} from '../../pipes';
import {TrellisingObservables} from '../store/observable/TrellisingObservables';
import {LEFT_ARROW_BASE64} from '../../CommonChartUtils';

@Component({
    selector: 'trellising-legend',
    templateUrl: 'TrellisingLegendComponent.html',
    styleUrls: ['./TrellisingLegendComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrellisingLegendComponent implements OnChanges {
    @Input() legend: ILegend[];
    @Input() tabId: TabId;
    @Input() height: number;
    @Input() legendSortingRequired: boolean;
    @Input() customControlLabel: ColorByLabels;
    @Input() legendDisabled: boolean;

    legendSymbol = LegendSymbol;
    calculatedHeight: string;
    sortedLegends: ILegend[];
    colorByLabels = ColorByLabels;

    arrowLeftBase64 = LEFT_ARROW_BASE64;

    constructor(public trellisingObservables: TrellisingObservables,
                public monthsOrderByPipe: MonthsOrderByPipe,
                public alphabeticOrderByPipe: AlphabeticOrderByPipe,
                public intervalsOrderByPipe: IntervalsOrderByPipe) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['height']) {
            this.calculatedHeight = !this.customControlLabel ? this.getHeight() : this.getMultipleHeight();
        }
        if (changes['legend'] || changes['tabId']) {
            if (this.legend.length >= 2 && this.tabId === TabId.TUMOUR_RESPONSE_PRIOR_THERAPY) {
                switch (this.customControlLabel) {
                    case ColorByLabels.TIME_ON_COMPOUND:
                        this.legend = [this.legend[0]];
                        break;
                    case ColorByLabels.PRIOR_THERAPY:
                        this.legend = [this.legend[1]];
                        break;
                    case ColorByLabels.DATE_OF_DIAGNOSIS:
                        this.legend = [this.legend[2]];
                        break;
                }
            }
            this.sortedLegends = map(this.legend, (legendItem: ILegend) => {
                return {
                    title: legendItem.title,
                    entries: this.sortEntries(legendItem.entries, legendItem.title)
                };
            });
        }
    }

    /**
     * Legends can be just strings, date or digital intervals
     * If sorting is required - legends sort by alphabet, dates and digital intervals
     *
     * @param {ILegendEntry[]} entries
     * @param {string} title
     * @returns {ILegendEntry[]}
     */
    sortEntries(entries: ILegendEntry[], title: string): ILegendEntry[] {
        if (this.legendSortingRequired) {
            const alphabeticalSorted = this.alphabeticOrderByPipe.transform(entries, title);
            const monthsSorted = this.monthsOrderByPipe.transform(alphabeticalSorted, title);
            const intervalsSorted = this.intervalsOrderByPipe.transform(monthsSorted, title);
            return intervalsSorted;
        } else {
            return entries;
        }
    }

    private getHeight(): string {
        return this.height ? (this.height - 100).toString() + 'px' : '100px';
    }

    private getMultipleHeight(): string {
        return this.height && this.height > 300 ? (this.height - 300).toString() + 'px' : '100px';
    }
}
