import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {DatasetViews, UserPermissions} from '../../../security/module';

@Component({
    selector: 'plugins-side-nav-bar',
    templateUrl: 'PluginsSideNavBarComponent.html',
    styleUrls: ['../SideNavBarStyles.css']
})
export class PluginsSideNavBarComponent {

    constructor(public userPermissions: UserPermissions,
                public datasetViews: DatasetViews,
                public router: Router) {
    }

    isActive(value: string): boolean {
        return this.router.isActive(value, false);
    }

}
