import {Injectable} from '@angular/core';

@Injectable()
export class ConmedsTrackUtils {

    public static CONMEDS_TRACK_NAME = 'Conmeds';
    public static CONMEDS_SUMMARY_SUB_TRACK_NAME = 'Summary';
    public static MEDICATION_CLASS_SUB_TRACK_NAME = 'Medication Class';
    public static MEDICATION_LEVEL_SUB_TRACK_NAME = 'Medication Level';

    // COLOURS
    public static ONE_CONMED_COLOUR = '#99D9EA';
    public static TWO_TO_FIVE_CONMED_COLOR = '#00A2E8';
    public static SIX_TO_TEN_CONMED_COLOR = '#3030DC';
    public static TEN_CONMED_COLOR = '#05076B';
    public static MEDICATION_LEVEL_COLOR = '#99D9EA';

    // Expansion level
    public static CONMEDS_SUMMARY_TRACK_EXPANSION_LEVEL = 1;
    public static MEDICATION_CLASS_TRACK_EXPANSION_LEVEL = 2;
    public static MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL = 3;

    static assignColour(numberOfConmeds: number, trackLevel: number): string {
        if (trackLevel === ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL) {
            return ConmedsTrackUtils.MEDICATION_LEVEL_COLOR;
        }
        if (numberOfConmeds <= 1) {
            return ConmedsTrackUtils.ONE_CONMED_COLOUR;
        }
        if (numberOfConmeds > 1 && numberOfConmeds <= 5) {
            return ConmedsTrackUtils.TWO_TO_FIVE_CONMED_COLOR;
        }
        if (numberOfConmeds > 5 && numberOfConmeds <= 10) {
            return ConmedsTrackUtils.SIX_TO_TEN_CONMED_COLOR;
        }
        if (numberOfConmeds > 10) {
            return ConmedsTrackUtils.TEN_CONMED_COLOR;
        }

    }
}
