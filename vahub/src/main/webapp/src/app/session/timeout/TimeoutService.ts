import {Injectable, EventEmitter} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {SessionHttpService} from '../http/SessionHttpService';
/**
 * Calls whoami incrementally to check the user has not timed out
 */
@Injectable()
export class TimeoutService {

    public currentState: EventEmitter<number> = new EventEmitter<number>(false);

    constructor(private sessionHttpService: SessionHttpService) {
        /* gets the first status response */
        this.sessionHttpService.getActivityStatusObservable().map((res: any) => res).first().subscribe((res: any) => {
            this.currentState.next(res.status);
        }, (err) => {
            this.currentState.next(err.status);
        });
        this.isUserTimedOut();
    }

    isUserTimedOut(): void {
        const that = this;
        Observable.interval((15 * 60 * 1000) /* 15 minutes*/)
            .timeInterval()
            .takeWhile(() => {
                return true;
            })
            .map(() => {
                return this.sessionHttpService.getActivityStatusObservable().map((res: any) => res).subscribe((res) => {
                    that.currentState.next(res.status);
                }, (err) => {
                    that.currentState.next(err.status);
                });
            }).subscribe();
    }
}
