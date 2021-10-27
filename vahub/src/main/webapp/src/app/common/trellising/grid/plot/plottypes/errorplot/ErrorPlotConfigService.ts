import {Injectable} from '@angular/core';
import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class ErrorPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number): UserOptions {
        const customConfig: UserOptions = {
            chart: {
                type: 'errorbar',
                animationTime: 500
            },
            tooltip: {
                formatter: function (): string {
                    const low = (this.low !== undefined) ? parseFloat(this.low.toFixed(2)) : '';
                    const high = (this.high !== undefined) ? parseFloat(this.high.toFixed(2)) : '';
                    const x = (this.x !== undefined) ? parseFloat(this.x.toFixed(2)) : '';
                    return  `${yAxisLabel}<br/>
                             Min: <b>${low}</b>, Max: <b>${high}</b><br/>
                             ${globalXAxisLabel}: <b>${x}</b>`;
               }
            }
        };
        return super.createDefaultPlotConfig(customConfig, height, title);
    }

}
