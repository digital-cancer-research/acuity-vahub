import {Injectable} from '@angular/core';

import {BehaviorSubject} from 'rxjs/BehaviorSubject';

@Injectable()
export class XAxisCoordinateService {
    public cursorXCoordinate = new BehaviorSubject<number>(null);
}
