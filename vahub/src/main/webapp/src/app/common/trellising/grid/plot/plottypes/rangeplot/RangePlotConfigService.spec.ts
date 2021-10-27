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

import {RangePlotConfigService} from './RangePlotConfigService';
import {TabId} from '../../../../store';
import {inject, TestBed} from '@angular/core/testing';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';

class MockConfigurationService {
}

describe('GIVEN RangePlotConfigService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [],
            providers: [
                { provide: ExportChartService, useValue: {}},
                { provide: ConfigurationService, useClass: MockConfigurationService },
                RangePlotConfigService,
            ]
        });
    });
    describe('WHEN Lab line plot and y axis is normalised to the reference range', () => {
        it('THEN two horizontal lines added',   inject([RangePlotConfigService], (configService: RangePlotConfigService) => {
            const config = configService.createPlotConfig('', '', '', '', 'Ref range norm. value', 0, TabId.LAB_LINEPLOT);
            expect(config.plotLines).toEqual([{
                value: 0,
                axis: 'y',
                color: 'red',
                width: 1
            }, {
                value: 1,
                axis: 'y',
                color: 'red',
                width: 1
            }]);
        }));
    });
});
