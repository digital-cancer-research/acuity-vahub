import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.LAB_LINEPLOT"></trellising-component>'
})
export class LabsRangePlotComponent extends TChartComponent { }
