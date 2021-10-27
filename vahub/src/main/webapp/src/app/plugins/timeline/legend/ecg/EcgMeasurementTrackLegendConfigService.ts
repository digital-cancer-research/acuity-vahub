import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {EcgTrackUtils} from '../../track/ecg/EcgTrackUtils';

@Injectable()
export class EcgMeasurementTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'ECG measurement',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'ECG Measurement at baseline',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
