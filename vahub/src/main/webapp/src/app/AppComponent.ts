import {AfterViewInit, Component, OnDestroy} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';

import {SessionEventService} from './session/event/SessionEventService';
import {UserActivityService} from './UserActivityService';


@Component({
    selector: 'app',
    templateUrl: 'AppComponent.html',
    styleUrls: ['./AppComponent.css']
})
export class AppComponent implements OnDestroy, AfterViewInit {
    //refreshModal
    private routerSub: Subscription;

    constructor(public sessionEventService: SessionEventService,
                private router: Router,
                private userActivityService: UserActivityService) {
    }


    ngAfterViewInit(): void {
        this.routerSub = this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationEnd) {
                if (event.url.indexOf('plugins') !== -1) {
                    this.userActivityService.send(event.urlAfterRedirects);
                }
            }
        });
    }

    ngOnDestroy(): void {
        this.routerSub.unsubscribe();
    }
}
