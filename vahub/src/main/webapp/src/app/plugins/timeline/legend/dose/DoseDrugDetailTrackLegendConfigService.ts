import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {DoseTrackUtils} from '../../track/dose/DoseTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class DoseDrugDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Drug dose summary',
            items: [
                {
                    type: TrackLegendType.LINE,
                    text: 'Dosing event',
                    color: DoseTrackUtils.DRUG_DETAIL_COLOUR
                },
                {
                    type: TrackLegendType.LINE_AND_CIRCLE,
                    text: 'Discontinuation',
                    color: DoseTrackUtils.DRUG_DETAIL_COLOUR
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '10',
                    width: '14',
                    src: TimelineUtils.STEP3_ONGOING_SYMBOL
                }
            ]
        };
    }
}
