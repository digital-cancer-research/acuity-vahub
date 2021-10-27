import {Component} from '@angular/core';

import {DatasetViews, UserPermissions} from '../../security/module';
import {AbstractPluginComponent} from '../AbstractPluginComponent';

@Component({
    templateUrl: 'ExposureComponent.html'
})
export class ExposureComponent extends AbstractPluginComponent {
    constructor(public datasetViews: DatasetViews, public userPermissions: UserPermissions) {
        super();
    }
}
