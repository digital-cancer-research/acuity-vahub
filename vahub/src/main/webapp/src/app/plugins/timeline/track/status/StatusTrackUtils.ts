import { Injectable } from '@angular/core';
import {EventDateType} from '../../chart/IChartEvent';

@Injectable()
export class StatusTrackUtils {

    public static STATUS_TRACK_NAME = 'Status';
    public static STATUS_SUMMARY_TRACK_NAME = 'Summary';

    // COLOURS
    public static RUN_IN_EVENT_COLOUR = '#4EDC5F';
    public static RANDOMISED_EVENT_COLOUR = '#FF8C5B';
    public static ON_STUDY_DRUG = '#B507AA';

    // Expansion level
    public static SUMMARY_TRACK_EXPANSION_LEVEL = 1;

    static assignColour(type: EventDateType): string {
        if (type === EventDateType.RUN_IN) {
            return StatusTrackUtils.RUN_IN_EVENT_COLOUR;
        } else if (type === EventDateType.RANDOMIZED_DRUG) {
            return StatusTrackUtils.RANDOMISED_EVENT_COLOUR;
        } else if (type === EventDateType.ON_STUDY_DRUG) {
            return StatusTrackUtils.ON_STUDY_DRUG;
        }
    }
}
