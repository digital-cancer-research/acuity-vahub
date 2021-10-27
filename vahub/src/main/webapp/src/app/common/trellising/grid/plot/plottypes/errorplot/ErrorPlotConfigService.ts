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
