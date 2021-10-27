import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Subscription} from 'rxjs/Subscription';
import {TabName} from '../common/trellising/store/ITrellising';
import {NavigationStart, Router} from '@angular/router';
import * as _ from 'lodash';

@Injectable()
export class PluginsService {

    selectedTab: BehaviorSubject<TabName> = new BehaviorSubject<TabName>(null);
    routerSubscription: Subscription;

    constructor(private router: Router) {
    }

    subscribeToRouter(): void {
        this.routerSubscription = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationStart) {
                const isOnAesPlugin = _.some(['aes', 'cvot', 'cerebrovascular', 'cievents'], (path) => {
                    return window.location.hash.indexOf(path) !== -1;
                });
                const isOnSsvPlugin = window.location.hash.indexOf('singlesubject') !== -1;
                if (event.url.indexOf('aes') !== -1 && !isOnAesPlugin || event.url.indexOf('singlesubject') !== -1 && !isOnSsvPlugin) {
                    this.selectedTab.next(null);
                }
            }
        });
    }

    unsubscribeFromRouter(): void {
        if (this.routerSubscription) {
            this.routerSubscription.unsubscribe();
        }
    }

}
