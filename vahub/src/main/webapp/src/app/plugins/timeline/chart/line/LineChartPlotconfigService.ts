import {Injectable} from '@angular/core';
import {AbstractChartPlotconfigService} from '../AbstractChartPlotconfigService';
import {positionToolTipForPointDependantOnScreenLocation, TimelineUtils} from '../TimelineUtils';

@Injectable()
export class LineChartPlotconfigService extends AbstractChartPlotconfigService {
    private currentLeftMargin: number;

    constructor() {
        super();
        this.currentLeftMargin = 0;
    }

    createPlotConfig(customPlotConfig: any): any {
        const barChartPlotConfig = {
            chart: {
                height: TimelineUtils.LINE_CHART_HEIGHT,
                margin: [TimelineUtils.CHART_TOP_MARGIN, TimelineUtils.CHART_RIGHT_MARGIN, TimelineUtils.CHART_BOTTOM_MARGIN,
                    TimelineUtils.CHART_LEFT_MARGIN],
                type: 'timeline-linechart',
                disableExport: true,
                animationTime: 500
            },
            tooltip: {
                positioner: function (labelWidth, labelHeight, point): any {
                    return positionToolTipForPointDependantOnScreenLocation(labelWidth, labelHeight, point, this.chart);
                },
                formatter: function (): string {
                    let message = '<div class="timeline-tooltip">';

                    message += 'Subject: <b>' + this.subjectId + '</b>';

                    if (this.tooltip) {
                        message += '<br/>' + this.tooltip;
                    }
                    message += '<br/></b> Event at day <b>' + this.xAsString + '</b>';
                    message += '<br/></b> Study day: <b>' + this.studyXAsString + '</b>';

                    message += '</div>';

                    return message;
                }
            },
        };

        // merge custom plotconfig with default plotconfig
        return super.createPlotConfig(barChartPlotConfig, customPlotConfig);
    }
}
