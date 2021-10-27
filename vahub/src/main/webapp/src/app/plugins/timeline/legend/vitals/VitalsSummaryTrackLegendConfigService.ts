import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {VitalsTrackUtils} from '../../track/vitals/VitalsTrackUtils';

@Injectable()
export class VitalsSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Vitals summary',
            items: [
                {
                    type: TrackLegendType.GRADIENT,
                    text: '% change from baseline',
                    colorStart: VitalsTrackUtils.MINIMAL_COLOUR,
                    color: VitalsTrackUtils.BASELINE_COLOUR,
                    colorEnd: VitalsTrackUtils.MAXIMAL_COLOUR
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Vitals Measurement',
                    height: '10',
                    width: '10',
                    color: VitalsTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Vitals Measurement at baseline',
                    height: '10',
                    width: '10',
                    color: VitalsTrackUtils.BASELINE_COLOUR
                },
            ]
        };
    }
}
