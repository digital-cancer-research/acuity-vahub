import {Component, OnDestroy} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {Subscription} from 'rxjs/Subscription';
import {ConfigurationService} from '../configuration/module';
@Component({
    selector: 'about',
    templateUrl: 'AboutComponent.html',
    styleUrls: ['./AboutComponent.css']
})
export class AboutComponent implements OnDestroy {
    private routeFragmentSubscription: Subscription;
    public about: { [index: string]: string };

    constructor(private route: ActivatedRoute, private configurationService: ConfigurationService) {
        this.about = configurationService.brandingProperties.about;
    }

    /*
    * Scroll down to fragment if there is a fragment
    */
    onAnchorClick(): void {
        this.routeFragmentSubscription = this.route.fragment.subscribe(f => {
            const element: any = document.querySelector('#' + f);
            if (element) {
                element.scrollIntoView(element);
            }
        });
    }

    ngOnDestroy(): void {
        if (this.routeFragmentSubscription) {
            this.routeFragmentSubscription.unsubscribe();
        }
    }
}
