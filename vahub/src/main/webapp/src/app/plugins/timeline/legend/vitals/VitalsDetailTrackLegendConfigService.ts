import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {VitalsTrackUtils} from '../../track/vitals/VitalsTrackUtils';

@Injectable()
export class VitalsDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Vitals detail',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Vitals Measurement',
                    height: '10',
                    width: '10',
                    color: VitalsTrackUtils.VITALS_MEASUREMENT_COLOUR
                },
                {
                    type: TrackLegendType.STAR,
                    text: 'Vitals Baseline Measurement',
                    height: '10',
                    width: '10',
                    style: {
                        fill: VitalsTrackUtils.VITALS_MEASUREMENT_COLOUR
                    }
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Vitals Baseline',
                    height: '10',
                    width: '10',
                    color: VitalsTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
