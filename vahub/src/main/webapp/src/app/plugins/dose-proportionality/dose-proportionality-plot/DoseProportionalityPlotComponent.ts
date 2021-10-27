import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.DOSE_PROPORTIONALITY_BOX_PLOT"></trellising-component>'
})

export class DoseProportionalityPlotComponent extends TChartComponent {

}
