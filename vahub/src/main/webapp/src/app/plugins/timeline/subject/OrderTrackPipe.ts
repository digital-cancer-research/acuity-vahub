import { Pipe, PipeTransform } from '@angular/core';
import {ITrack} from '../store/ITimeline';
import {List} from 'immutable';
import * as  _ from 'lodash';

@Pipe({
    name: 'ordertrack'
})
export class OrderTrackPipe implements PipeTransform {
    /**
     * Sort tracks by order value or by name if order value isn't provided
     * (e. g. when Show on Timeline button is used on one of Respiratory tabs requiring more than 1 Timeline track)
     * @param tracks - tracks to sort
     */
    transform(tracks: List<ITrack>): ITrack[] {
        return _.sortBy(tracks.toArray(), 'order', 'name');
    }
}
