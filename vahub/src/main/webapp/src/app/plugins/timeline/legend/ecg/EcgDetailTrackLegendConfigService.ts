import {Injectable} from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {EcgTrackUtils} from '../../track/ecg/EcgTrackUtils';

@Injectable()
export class EcgDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'ECG detail',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'ECG Measurement',
                    height: '10',
                    width: '10',
                    color: EcgTrackUtils.ECG_MEASUREMENT_COLOUR
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Baseline',
                    color: EcgTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Reference Line',
                    color: EcgTrackUtils.REFERENCE_LINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'High QTcF, significant or abnormal evaluation',
                    height: '10',
                    width: '10',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                }

            ]
        };
    }
}
