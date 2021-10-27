import {Component} from '@angular/core';
import {DatasetViews} from '../../../security/module';

@Component({
    template: `<spotfire-component [moduletype]="'Tolerability'"></spotfire-component>`
})
export class TolerabilityComponent {

    constructor(public datasetViews: DatasetViews) { }
}
