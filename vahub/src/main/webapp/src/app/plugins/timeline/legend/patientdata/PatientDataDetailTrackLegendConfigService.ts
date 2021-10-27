import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {PatientDataTrackUtils} from '../../track/patientdata/PatientDataTrackUtils';

@Injectable()
export class PatientDataDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Patient data details',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Patient data measurement',
                    color: PatientDataTrackUtils.assignColour(1, PatientDataTrackUtils.LINECHART_LEVEL_TRACK_EXPANSION_LEVEL)
                }
            ]
        };
    }
}
