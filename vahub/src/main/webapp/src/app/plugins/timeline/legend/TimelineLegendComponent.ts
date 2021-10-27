/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Component, Input, ChangeDetectionStrategy, OnChanges, SimpleChanges} from '@angular/core';
import {StatusTrackLegendConfigService} from './status/StatusTrackLegendConfigService';
import {AesTrackLegendConfigService} from './aes/AesTrackLegendConfigService';
import {DoseSummaryTrackLegendConfigService} from './dose/DoseSummaryTrackLegendConfigService';
import {DoseDrugDetailTrackLegendConfigService} from './dose/DoseDrugDetailTrackLegendConfigService';
import {ConmedsTrackLegendConfigService} from './conmeds/ConmedsTrackLegendConfigService';
import {
    TrackName, LabsYAxisValue, SpirometryYAxisValue, EcgWarnings,
    ECG_WARNING_OPTIONS
} from '../store/ITimeline';
import {LabsSummaryTrackLegendConfigService} from './labs/LabsSummaryTrackLegendConfigService';
import {LabsDetailTrackLegendConfigService} from './labs/LabsDetailTrackLegendConfigService';
import {ExacerbationsTrackLegendConfigService} from './exacerbations/ExacerbationsTrackLegendConfigService';
import {SpirometrySummaryTrackLegendConfigService} from './spirometry/SpirometrySummaryTrackLegendConfigService';
import {SpirometryDetailTrackLegendConfigService} from './spirometry/SpirometryDetailTrackLegendConfigService';
import {VitalsSummaryTrackLegendConfigService} from './vitals/VitalsSummaryTrackLegendConfigService';
import {VitalsDetailTrackLegendConfigService} from './vitals/VitalsDetailTrackLegendConfigService';
import {PatientDataSummaryTrackLegendConfigService} from './patientdata/PatientDataSummaryTrackLegendConfigService';
import {PatientDataDetailTrackLegendConfigService} from './patientdata/PatientDataDetailTrackLegendConfigService';
import {EcgSummaryTrackLegendConfigService} from './ecg/EcgSummaryTrackLegendConfigService';
import {EcgDetailTrackLegendConfigService} from './ecg/EcgDetailTrackLegendConfigService';
import {TimelineTrackService} from '../config/trackselection/TimelineTrackService';
import * as  _ from 'lodash';

@Component({
    selector: 'timeline-legend',
    templateUrl: 'TimelineLegendComponent.html',
    styleUrls: ['./TimelineLegend.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineLegendComponent implements OnChanges {
    @Input() loading: boolean;
    @Input() labsYAxisValue: LabsYAxisValue;
    @Input() spirometryYAxisValue: SpirometryYAxisValue;
    @Input() ecgWarnings: EcgWarnings;

    trackName = TrackName;
    ecgWarningsAvailable = true;

    constructor(public statusTrackLegendConfigService: StatusTrackLegendConfigService,
                public aesTrackLegendConfigService: AesTrackLegendConfigService,
                public doseSummaryTrackLegendConfigService: DoseSummaryTrackLegendConfigService,
                public doseDrugDetailTrackLegendConfigService: DoseDrugDetailTrackLegendConfigService,
                public conmedsTrackLegendConfigService: ConmedsTrackLegendConfigService,
                public exacerbationsTrackLegendConfigService: ExacerbationsTrackLegendConfigService,
                public labsSummaryTrackLegendConfigService: LabsSummaryTrackLegendConfigService,
                public labsDetailTrackLegendConfigService: LabsDetailTrackLegendConfigService,
                public spirometrySummaryTrackLegendConfigService: SpirometrySummaryTrackLegendConfigService,
                public spirometryDetailTrackLegendConfigService: SpirometryDetailTrackLegendConfigService,
                public vitalsSummaryTrackLegendConfigService: VitalsSummaryTrackLegendConfigService,
                public vitalsDetailTrackLegendConfigService: VitalsDetailTrackLegendConfigService,
                public patientDataSummaryTrackLegendConfigService: PatientDataSummaryTrackLegendConfigService,
                public patientDataDetailTrackLegendConfigService: PatientDataDetailTrackLegendConfigService,
                public ecgSummaryTrackLegendConfigService: EcgSummaryTrackLegendConfigService,
                public ecgDetailTrackLegendConfigService: EcgDetailTrackLegendConfigService,
                public timelineTrackService: TimelineTrackService) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.ecgWarnings) {
            const warnings = this.ecgWarnings.toJS();
            this.ecgWarningsAvailable = _.some(ECG_WARNING_OPTIONS, (warning) => {
                return warnings[warning.key].available;
            });

        }
    }

    isVisible(trackName: TrackName, expansionLevels: number[]): boolean {
        let visible = false;
        const that = this;

        expansionLevels.forEach((expansionLevel: number) => {
            visible = visible || (that.timelineTrackService.tracksWithExpansion
                && that.timelineTrackService.tracksWithExpansion[trackName.toString() + expansionLevel]);
        });

        return visible;
    }

}
