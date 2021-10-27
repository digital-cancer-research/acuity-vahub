import {Component} from '@angular/core';
import {DatasetViews} from '../../security/DatasetViews';
import {UserPermissions} from '../../security/UserPermissions';
import {ExposureComponent} from '../exposure';

@Component({
    templateUrl: '../exposure/ExposureComponent.html'
})

export class PkResultWithResponseComponent extends ExposureComponent {
    constructor(public datasetViews: DatasetViews, public userPermissions: UserPermissions) {
        super(datasetViews, userPermissions);
    }
}
