import {BiomarkerSettingsComponent} from './dataTypes/biomarker/BiomarkerSettingsComponent';
import {DoseProportionalitySettingsComponent} from './dataTypes/dose-proportionality/DoseProportionalitySettingsComponent';
import {ExposureSettingsComponent} from './dataTypes/exposure/ExposureSettingsComponent';
import {PriorTherapySettingsComponent} from './dataTypes/prior-therapy/PriorTherapySettingsComponent';
import {ListSelectionComponent} from './list-selection/ListSelectionComponent';
import {RadioButtonsSettingsComponent} from './radioButtons/RadioButtonsSettingsComponent';
import {PkOverallResponseSettingsComponent} from './dataTypes/pk-overall-response-settings/PkOverallResponseSettingsComponent';
import {AesChordSettingsComponent} from './dataTypes/aes/AesChordSettingsComponent';
import {RangeSettingsComponent} from './range/RangeSettingsComponent';
import {CtDNASettingsComponent} from './dataTypes/ctdna-settings/CtDNASettingsComponent';

export const settingsComponents = [
    ExposureSettingsComponent,
    DoseProportionalitySettingsComponent,
    PkOverallResponseSettingsComponent,
    CtDNASettingsComponent,
    PriorTherapySettingsComponent,
    BiomarkerSettingsComponent,
    AesChordSettingsComponent,
    RadioButtonsSettingsComponent,
    ListSelectionComponent,
    RangeSettingsComponent
];
