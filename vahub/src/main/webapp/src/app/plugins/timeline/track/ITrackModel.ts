import {ITrackPlotDetail} from './ITrackPlotDetail';
import {ITrack} from './../store/ITimeline';

export interface ITrackModel {
    canExpand(): boolean;
    canCollapse(): boolean;
    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[];
}
