import {Component} from '@angular/core';
import {DatasetViews} from '../../security/DatasetViews';
import {UserPermissions} from '../../security/module';
import {ExposureComponent} from '../exposure';

@Component({
    templateUrl: '../exposure/ExposureComponent.html'
})
// TODO remove ExposureComponent extending & UserPermissions after moving onco-permissions to the has*Data methods
export class PkResultComponent extends ExposureComponent {
    constructor(public datasetViews: DatasetViews, public userPermissions: UserPermissions) {
        super(datasetViews, userPermissions);
    }
}
