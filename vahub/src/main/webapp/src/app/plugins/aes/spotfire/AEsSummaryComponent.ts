import {Component} from '@angular/core';
import {DatasetViews} from '../../../security/module';

@Component({
    template: `<spotfire-component [moduletype]="'AESummaries'"></spotfire-component>`
})
export class AEsSummaryComponent {

    constructor(public datasetViews: DatasetViews) { }
}
