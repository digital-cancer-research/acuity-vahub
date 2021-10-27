import {Subscription} from 'rxjs/Subscription';

/**
 * Base class for all event services, this is to solve the bug https://github.com/angular/angular/pull/6460
 */
export abstract class BaseEventService {

    // To solve the bug of https://github.com/angular/angular/pull/6460
    // ngOnDestroy not been called
    // so keep a map reference to check if its been called already and destroy that if there is one
    protected dict: { [key: string]: Subscription; } = {};

    /**
     * Adds a new Subscription but removes and cancels an old one because of a bug with ngOnDestroy
     */
    addSubscription(key: string, sub: Subscription): void {
        const gotSub = this.dict[key];
        if (gotSub) {
            gotSub.unsubscribe();
            delete this.dict[key];
        }
        this.dict[key] = sub;
    }
}
