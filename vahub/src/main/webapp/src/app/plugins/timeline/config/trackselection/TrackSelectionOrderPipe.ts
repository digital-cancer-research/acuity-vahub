import {Pipe, PipeTransform} from '@angular/core';

import {ITrack} from '../../store/ITimeline';

@Pipe({ name: 'sort' })
export class TrackSelectionOrderPipe implements PipeTransform {
    transform(array: Array<ITrack>): Array<ITrack> {
        return array.sort((a: ITrack, b: ITrack) => a.name.toString().localeCompare(b.name.toString()));
    }
}
