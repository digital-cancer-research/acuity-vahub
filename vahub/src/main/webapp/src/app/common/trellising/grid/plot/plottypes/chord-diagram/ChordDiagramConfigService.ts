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

import {PlotSettings} from '../../../../store';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {IPlotConfigService} from '../../IPlotConfigService';
import {CommonChartUtils} from '../../../../../CommonChartUtils';
import {UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class ChordDiagramConfigService extends AbstractPlotConfigService implements IPlotConfigService {
    createPlotConfig(title: string, height: number, plotSettings: PlotSettings): UserOptions {
        const customConfig: UserOptions = {
            chart: {
                type: 'chord'
            },
            title: {
                text: this.formatTitle(plotSettings)
            },
            tooltip: {
                formatter: function () {
                    let tooltip;
                    if (this.type === 'pie') {
                        tooltip = this.name;
                    } else {
                        const { subjects, name, value } = this.chordData;
                        const numberOfColumns = Math.ceil(subjects.length / 10);
                        const minimalSubjectsLength = 3;
                        const moreThanMinSubjectsLength = subjects.length > minimalSubjectsLength;
                        tooltip = `<div class="custom-tooltip-box">${name.start} to ${name.end}
                        <br/>Number of incidence: ${value}
                        <br/>Subjects affected and # times affected:`;
                        if (moreThanMinSubjectsLength) {
                            tooltip += `<div class="collapsible" style="cursor: pointer; color: #2f5597; margin: 5px 0">
                                    <span class="collapse-text">${CommonChartUtils.EXPAND}</span>
                                    <span class="collapse-arrow glyphicon glyphicon-chevron-down"/></div>`;
                        }

                        tooltip += `<ul class="collapsed-subjects-list" style="margin-top: 5px; margin-bottom: 0; display: block" >`;
                        subjects.slice(0, minimalSubjectsLength).forEach(subject => {
                            tooltip += `<li>${subject[0]} x${subject[1]}</li>`;
                        });
                        tooltip += `</ul>`;

                        if (moreThanMinSubjectsLength) {
                            tooltip += `<ul class="subjects-list hidden"
                                            style="column-count: ${numberOfColumns};
                                             column-gap: 30px; margin-top: 5px; margin-bottom: 0; font-size: 12px; padding-left: 13px">`;
                            subjects.forEach(subject => {
                                tooltip += `<li>${subject[0]} x${subject[1]}</li>`;
                            });
                            tooltip += `</ul>`;
                        }
                        tooltip += `</div>`;
                    }
                    return tooltip;
                },
                onclick: this.toggleTooltipList
            }
        };

        return super.createDefaultPlotConfig(customConfig, height, title);
    }

    private toggleTooltipList (this: HTMLElement, event): void {
        const triggerClasslist = event.target.classList;
        const isCollapseTrigger = triggerClasslist.contains('collapsible')
            || triggerClasslist.contains('collapse-text')
            || triggerClasslist.contains('collapse-arrow');
        if (isCollapseTrigger) {
            const icon = this.querySelector('.collapse-arrow');
            const list = this.querySelector('.subjects-list');
            const collapsedList = this.querySelector('.collapsed-subjects-list');
            if (icon.classList.contains('glyphicon-chevron-up')) {
                icon.classList.remove('glyphicon-chevron-up');
                icon.classList.add('glyphicon-chevron-down');
                list.classList.add('hidden');
                collapsedList.classList.remove('hidden');
            } else {
                icon.classList.remove('glyphicon-chevron-down');
                icon.classList.add('glyphicon-chevron-up');
                collapsedList.classList.add('hidden');
                list.classList.remove('hidden');
            }
        }
    }

    private formatTitle(settings: PlotSettings): string {
        const timeFrame = settings.getIn(['trellisOptions', 'timeFrame']);
        let formattedTitle = `${settings.get('trellisedBy')}'s co-occurring `;
        if (timeFrame === 0) {
            formattedTitle += 'on the same day';
        } else {
            formattedTitle += `within ${timeFrame + 1} days`;
        }
        return formattedTitle;
    }
}
