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
