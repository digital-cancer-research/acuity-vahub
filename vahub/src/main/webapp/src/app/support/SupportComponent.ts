import {Component, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {ConfigurationService} from '../configuration/ConfigurationService';
import SuperUser = Request.SuperUser;

@Component({
    selector: 'support',
    templateUrl: 'SupportComponent.html',
    styleUrls: ['./SupportComponent.css']
})
export class SupportComponent implements OnDestroy {
    support: any;
    superUsers: SuperUser[];
    private routeFragmentSubscription: Subscription;

    constructor(private route: ActivatedRoute,
                public configurationService: ConfigurationService) {
        this.support = configurationService.brandingProperties.support;
        this.superUsers = configurationService.brandingProperties.superUsers
            .filter(su => su.name.length !== 0 && su.email.length !== 0);
    }

    isSuperUsersEmpty() {
        return this.superUsers.length === 0;
    }

    /*
     * Scroll down to fragment if there is a fragment
     */
    onAnchorClick(): void {
        this.routeFragmentSubscription = this.route.fragment.subscribe(f => {
            const element = document.querySelector('#' + f);
            if (element) {
                element.scrollIntoView();
            }
        });
    }

    ngOnDestroy(): void {
        if (this.routeFragmentSubscription) {
            this.routeFragmentSubscription.unsubscribe();
        }
    }
}
