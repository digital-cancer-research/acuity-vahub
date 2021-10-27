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

import {List} from 'immutable';
import {ITrack, ITrackDataPoint, TrackName} from '../store/ITimeline';
import {TimelineUtils} from '../chart/TimelineUtils';
import * as  _ from 'lodash';

export class TrackUtils {

    public static GAP_BETWEEN_TRACKS = 4;
    public static GAP_BETWEEN_SUB_TRACKS = 2;
    static getHeightOfTracks(tracks: List<ITrack>): number {
        let height = 0;

        tracks.forEach(track => {
            height += this.getHeightOfTrack(track);
        });

        if (tracks.size > 0) {
            height += (tracks.size - 1) * TrackUtils.GAP_BETWEEN_TRACKS;
        }

        return height;
    }

    static countVisibleTracks(tracks: List<ITrack>): number {
        return tracks.reduce((count, track) => {
            if (track.data) {
                count++;
            }
            return count;
        }, 0);
    }

    static getHeightOfTrack(track: ITrack): number {
        let height = 0;
        switch (track.name) {
            case TrackName.SUMMARY:
                height = TrackUtils.getSummaryTrackHeight(track);
                break;
            case TrackName.AES:
                height = TrackUtils.getAesTrackHeight(track);
                break;
            case TrackName.DOSE:
                height = TrackUtils.getDoseTrackHeight(track);
                break;
            case TrackName.CONMEDS:
                height = TrackUtils.getConmedsTrackHeight(track);
                break;
            case TrackName.ECG:
                height = TrackUtils.getECGTrackHeight(track);
                break;
            case TrackName.EXACERBATION:
                height = TrackUtils.getExacerbationTrackHeight(track);
                break;
            case TrackName.HEALTHCARE_ENCOUNTERS:
                height = TrackUtils.getHCETrackHeight(track);
                break;
            case TrackName.LABS:
                height = TrackUtils.getLabsTrackHeight(track);
                break;
            case TrackName.SPIROMETRY:
                height = TrackUtils.getSpirometryTrackHeight(track);
                break;
            case TrackName.VITALS:
                height = TrackUtils.getVitalsTrackHeight(track);
                break;
            case TrackName.PRD:
                height = TrackUtils.getPatientDataTrackHeight(track);
                break;
            default:
                height = TimelineUtils.BAR_CHART_HEIGHT;
                break;
        }

        return height;
    }

    static getSummaryTrackHeight(track: ITrack): number {
        return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
    }

    static getAesTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'pt', TimelineUtils.BAR_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getDoseTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'drug', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'drug', TimelineUtils.STEPPED_LINE_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getConmedsTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'conmedClass', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'conmedMedication', TimelineUtils.BAR_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getECGTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'testName', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'testName', TimelineUtils.LINE_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getExacerbationTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            default:
                break;
        }
    }

    static getHCETrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'category', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'hce', TimelineUtils.BAR_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getLabsTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'category', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'code', TimelineUtils.BAR_CHART_HEIGHT);
            case 4:
                return TrackUtils.getGroupedTrackHeight(track, 'code', TimelineUtils.LINE_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getSpirometryTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'code', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'code', TimelineUtils.LINE_CHART_HEIGHT);
            default:
                break;
        }
    }

    static getVitalsTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'testName', TimelineUtils.BAR_CHART_HEIGHT);
            case 3:
                return TrackUtils.getGroupedTrackHeight(track, 'testName', TimelineUtils.LINE_CHART_HEIGHT);
            default:
                break;
        }
    }
    static getPatientDataTrackHeight(track: ITrack): number {
        switch (track.expansionLevel) {
            case 1:
                return TrackUtils.getChartHeight(TimelineUtils.BAR_CHART_HEIGHT, 1, 0);
            case 2:
                return TrackUtils.getGroupedTrackHeight(track, 'testName', TimelineUtils.LINE_CHART_HEIGHT);
            default:
                break;
        }
    }

    /**
     * Get diff multiplied by multiplier
     * @param min {number}
     * @param max {number}
     * @param multiplier {number}
     * @returns {number}
     */
    static getDiff(min: number, max: number, multiplier: number = 0.05): number {
        return (max - min) * multiplier;
    }

    private static getGroupedTrackHeight(track: ITrack, groupName: string, chartHeight: number): number {
        const trackData: List<ITrackDataPoint> = track.data;
        if (trackData) {
            let groups: string[] = [];
            trackData.forEach(dataPoint => {
                groups.push(dataPoint.metadata[groupName]);
            });

            groups = _.uniq(groups);
            return TrackUtils.getChartHeight(chartHeight, groups.length, groups.length - 1);
        } else {
            return TimelineUtils.BAR_CHART_HEIGHT;
        }
    }

    private static getChartHeight(chartHeight: number, numberOfCharts: number, numberOfGaps: number): number {
        return chartHeight * numberOfCharts + TrackUtils.GAP_BETWEEN_SUB_TRACKS * numberOfGaps;
    }
}
