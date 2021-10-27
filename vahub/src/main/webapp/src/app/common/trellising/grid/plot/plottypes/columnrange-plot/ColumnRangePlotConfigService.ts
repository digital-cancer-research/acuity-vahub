import {Injectable} from '@angular/core';
import {List} from 'immutable';

import {TabId} from '../../../../store';
import {IPlotConfigService} from '../../IPlotConfigService';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {TherapiesType} from '../../../../store';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class ColumnRangePlotConfigService extends AbstractPlotConfigService implements IPlotConfigService {
    createPlotConfig(title: string, height: number, categories: List<string>): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'columnrange',
            },
            xAxis: [{
                categories: categories && categories.toJS()
            }],

        };
        return super.createDefaultPlotConfig(customConfig, height, title);
    }

    additionalOptions(tabId: TabId, therapiesSetting: TherapiesType): UserOptions {
        switch (tabId) {
            case TabId.TUMOUR_RESPONSE_PRIOR_THERAPY:
                return {
                    chart: {
                        isInverted: true,
                        animationTime: 500
                    },
                    title: {
                        text: therapiesSetting === TherapiesType.ALL_PRIOR_THERAPIES
                            ? 'All prior therapies vs Time on compound' : 'Immediate prior therapy vs. time on compound'
                    },
                    tooltip: {
                        formatter: function (): string {
                            let therapiesString = '';
                            if (this.therapies) {
                                this.therapies.forEach((therapy: string, index: number) => {
                                    therapiesString += index ? `; ${therapy}` : `${therapy}`;
                                });
                                return `<span">Subject ID: ${this.category}</span>
                                    <br/>
                                    <span>Therapy: ${therapiesString}</span>
                                    <br/>
                                    <span>Duration of therapy: Weeks from ${this.low} to ${this.high}</span>`;
                            }
                            //tooltip for most recent progression date
                            if (this.marker.name === 'progression') {
                                return `<span>Subject ID: ${this.category}</span>
                                    <br/>
                                    <span>Most recent progression date: ${this.name}</span>
                                    <br/>
                                    <span>Weeks since first treatment: ${this.y}</span>`;
                            }
                            //tooltip for diagnosis date
                            return `<span>Subject ID: ${this.category}</span>
                                    <br/>
                                    <span>Diagnosis date: ${this.name}</span>
                                    <br/>
                                    <span>Weeks since first treatment: ${this.y}</span>`;

                        }
                    }
                };
            default:
                return {};
        }
    }
}
