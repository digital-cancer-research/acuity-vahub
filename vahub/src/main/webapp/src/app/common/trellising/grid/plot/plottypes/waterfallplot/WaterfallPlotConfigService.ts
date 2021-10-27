import {Injectable} from '@angular/core';

import {ScaleTypes, TabId} from '../../../../store';
import {IPlotConfigService} from '../../IPlotConfigService';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {SentenceCasePipe} from '../../../../../pipes';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class WaterfallPlotConfigService extends AbstractPlotConfigService implements IPlotConfigService {

    constructor(private sentenceCasePipe: SentenceCasePipe,
                private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
        super(exportChartsService, configService);
    }

    createPlotConfig(title: string, height: number): UserOptions {
        const customConfig = {
            chart: {
                type: 'waterfall',
            },
            xAxis: [{
                type: ScaleTypes.CATEGORY_SCALE
            }]
        };
        const formattedTitle = `% change in sum of target lesion diameters, ${title.toLowerCase()}`;
        return super.createDefaultPlotConfig(customConfig, height, formattedTitle);
    }

    additionalOptions(tabId: TabId, colorByValue: string): UserOptions {
        const that = this;
        switch (tabId) {
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                return {
                    tooltip: {
                        formatter: function (): string {
                            return `
                                <div>
                                    <span>Subject ID: ${this.category}</span>
                                    <br/>
                                    <span>% change in sum of target lesion diameters: ${this.y}</span>
                                    <br/>
                                    <span>${that.sentenceCasePipe.transform(colorByValue)}: ${this.name}</span>
                                </div>
                            `;
                        }
                    },
                };
            default:
                return {};
        }
    }
}
