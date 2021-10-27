import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.CARDIAC_BOXPLOT"></trellising-component>'
})
export class CardiacBoxPlotComponent extends TChartComponent { }
