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

import {Injectable} from '@angular/core';
import {TrackDataTransformer} from '../TrackDataTransformer';
import {TrackData} from '../IDataService';
import {ITrackDataPoint, TrackDataPointRecord} from '../../store/ITimeline';
import SubjectLabsDetail = Request.SubjectLabsDetail;
import SubjectLabsCategories = Request.SubjectLabsCategories;
import Categories = Request.Categories;
import SubjectLabsSummary = Request.SubjectLabsSummary;
import LabsSummaryEvent = Request.LabsSummaryEvent;
import Labcodes = Request.Labcodes;
import LabsDetailsEvent = Request.LabsDetailsEvent;

@Injectable()
export class LabsTrackDataTransformer extends TrackDataTransformer {

    constructor() {
        super();
    }

    transformLabsSummaryTrackData(result: SubjectLabsSummary[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectLabsSummaryTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformLabsCategoryTrackData(result: SubjectLabsCategories[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectLabsCategoriesTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    transformLabsDetailTrackData(result: SubjectLabsDetail[]): TrackData[] {
        return <TrackData[]> result.reduce((data, re) => {
            const newTrackData: TrackData = this.transformSubjectLabsDetailTrackData(re);
            data.push(newTrackData);
            return data;
        }, []);
    }

    private transformSubjectLabsSummaryTrackData(result: SubjectLabsSummary): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.events.forEach((event: LabsSummaryEvent) => {
            trackData.push(this.transformSubjectLabsSummaryEvent(event));
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectLabsSummaryEvent(event: LabsSummaryEvent): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                visitNumber: event.visitNumber,
                numAboveReferenceRange: event.numAboveReferenceRange,
                numBelowReferenceRange: event.numBelowReferenceRange,
                numAboveSeverityThreshold: event.numAboveSeverityThreshold,
                numBelowSeverityThreshold: event.numBelowSeverityThreshold
            }
        });
    }

    private transformSubjectLabsCategoriesTrackData(result: SubjectLabsCategories): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.labcodes.forEach((lab: Categories) => {
            lab.events.forEach((event: LabsSummaryEvent) => {
                trackData.push(this.transformSubjectLabsCategoriesEvent(event, lab));
            });
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectLabsCategoriesEvent(event: LabsSummaryEvent, lab: Categories): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                category: lab.category,
                visitNumber: event.visitNumber,
                numAboveReferenceRange: event.numAboveReferenceRange,
                numBelowReferenceRange: event.numBelowReferenceRange,
                numAboveSeverityThreshold: event.numAboveSeverityThreshold,
                numBelowSeverityThreshold: event.numBelowSeverityThreshold
            }
        });
    }

    private transformSubjectLabsDetailTrackData(result: SubjectLabsDetail): TrackData {
        const trackData: ITrackDataPoint[] = [];

        result.labcodes.forEach((lab: Labcodes) => {
            if (lab.events.length > 0) {
                lab.events.sort((e1, e2) => {
                    return e1.start.dayHour > e2.start.dayHour ? 1 : ((e2.start.dayHour > e1.start.dayHour) ? -1 : 0);
                });
            }

            lab.events.forEach((event: LabsDetailsEvent) => {
                trackData.push(this.transformSubjectLabsDetailEvent(event, lab));
            });
        });

        return {
            subjectId: result.subjectId,
            data: trackData
        };
    }

    private transformSubjectLabsDetailEvent(event: LabsDetailsEvent, labcodes: Labcodes): ITrackDataPoint {
        return <ITrackDataPoint>new TrackDataPointRecord({
            start: this.transformDateDayHour(event.start),
            end: null,
            metadata: {
                code: labcodes.labcode,
                visitNumber: event.visitNumber,
                baselineValue: event.baselineValue,
                baselineFlag: event.baselineFlag,
                valueRaw: event.valueRaw,
                unit: event.unitRaw,
                valueChangeFromBaseline: event.valueChangeFromBaseline,
                unitChangeFromBaseline: event.unitChangeFromBaseline,
                valuePercentChangeFromBaseline: event.valuePercentChangeFromBaseline,
                unitPercentChangeFromBaseline: event.unitPercentChangeFromBaseline,
                referenceRangeHigherLimit: labcodes.refHigh,
                referenceRangeLowerLimit: labcodes.refLow,
                numAboveReferenceRange: event.numAboveReferenceRange,
                numBelowReferenceRange: event.numBelowReferenceRange,
                numAboveSeverityThreshold: event.numAboveSeverityThreshold,
                numBelowSeverityThreshold: event.numBelowSeverityThreshold
            }
        });
    }

}
