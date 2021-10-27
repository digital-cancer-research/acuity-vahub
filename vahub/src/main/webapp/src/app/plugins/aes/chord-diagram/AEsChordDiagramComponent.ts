import {Component} from '@angular/core';
import {TChartComponent} from '../../TChartComponent';

@Component({
    template: '<trellising-component [tabId]="tabId.AES_CHORD_DIAGRAM"></trellising-component>'
})
export class AEsChordDiagramComponent extends TChartComponent { }
