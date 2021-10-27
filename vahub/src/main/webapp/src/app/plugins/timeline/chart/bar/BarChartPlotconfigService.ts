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
import {isUndefined} from 'lodash';
import {AbstractChartPlotconfigService} from '../AbstractChartPlotconfigService';
import {positionToolTipForPointDependantOnScreenLocation, TimelineUtils} from '../TimelineUtils';

@Injectable()
export class BarChartPlotconfigService extends AbstractChartPlotconfigService {
    private currentLeftMargin: number;

    constructor() {
        super();
        this.currentLeftMargin = 0;
    }

    createPlotConfig(customPlotConfig: any): any {
        const barChartPlotConfig = {
            chart: {
                type: 'timeline-barchart',
                animationTime: 500,
                disableExport: true,
                inverted: true,
                height: TimelineUtils.BAR_CHART_HEIGHT,
                margin: [0, TimelineUtils.CHART_RIGHT_MARGIN, 0, TimelineUtils.CHART_LEFT_MARGIN],
                spacing: [0, 0, 0, 0]
            },
            tooltip: {
                positioner: function (labelWidth, labelHeight, point): any {
                    return positionToolTipForPointDependantOnScreenLocation(labelWidth, labelHeight, point, this.chart);
                },
                formatter: function (): string {
                    let message = '<div class="timeline-tooltip">';

                    message += 'Subject: <b>' + this.subjectId + '</b>';

                    if (!isUndefined(this.eventType)) {
                        message += '<br/>Event: <b>' + this.eventType + '</b>';
                    }

                    if (this.type === 'columnrange') {
                        if (!isUndefined(this.tooltip)) {
                            message += '<br/>' + this.tooltip;
                        }
                        const eventEnd = this.highAsString || 'Ongoing';
                        message += '<br/></b>Start: <b>' + this.lowAsString + '</b> End: <b>' + eventEnd + '</b>';
                        message += '<br/></b>Study Day Start: <b>' + this.studyLow
                            + '</b> Study Day End: <b>' + this.studyHigh + '</b>';
                    } else {
                        if (!isUndefined(this.tooltip)) {
                            message += '<br/>' + this.tooltip;
                        }
                        message += '<br/></b> Event at day: <b>' + this.yAsString + '</b>';
                        message += '<br/></b> Study day: <b>' + this.studyY + '</b>';
                    }

                    message += '</div>';

                    return message;
                }
            },
        };

        // merge custom plotconfig with default plotconfig
        return super.createPlotConfig(barChartPlotConfig, customPlotConfig);

    }
}
