import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {Injectable} from '@angular/core';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import * as  _ from 'lodash';
import {DatasetViews} from '../../../../../../security/DatasetViews';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class ScatterPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    constructor(private datasetViews: DatasetViews,
                private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
            super(exportChartsService, configService);
    }

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number): UserOptions {
        let customConfig: CustomPlotConfig = {
            chart: {
                type: 'scatter',
                animationTime: 500
            },
            tooltip: {
                formatter: function (): string {
                    const y = Math.round(this.y * 100) / 100;
                    const x = Math.round(this.x * 100) / 100;
                    return `${yAxisLabel}: <b>${y}</b><br>
                            ${xAxisLabel}: <b>${x}</b>`;
                }
            }
        };
        customConfig = super.createDefaultPlotConfig(customConfig, height, title);
        return _.merge(customConfig, this.additionalOptions());
    }

    private additionalOptions(): CustomPlotConfig {
        return {
            plotLines: [
                {
                    color: 'black',
                    axis: 'y',
                    value: 3.0,
                    width: 0.5,
                    zIndex: 5,
                    styles: {
                        'stroke-dasharray': 5,
                        'fill': 'none'
                    }
                },
                {
                    color: 'black',
                    axis: 'x',
                    value: 2.0,
                    width: 0.5,
                    zIndex: 5,
                    styles: {
                        'stroke-dasharray': 5,
                        'fill': 'none'
                    }
                }
            ]
        };
    }
}
