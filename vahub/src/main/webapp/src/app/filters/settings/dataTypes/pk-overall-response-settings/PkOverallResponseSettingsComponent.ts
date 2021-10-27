import { Component, OnInit } from '@angular/core';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {ScaleTypes} from '../../../../common/trellising/store';

@Component({
  selector: 'pk-overall-response-settings',
  templateUrl: './PkOverallResponseSettingsComponent.html',
  styleUrls: ['../../../filters.css']
})
export class PkOverallResponseSettingsComponent implements OnInit {

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher) {}

    isOpen = false;
    selectedScale: ScaleTypes;
    previousSelectedScale: ScaleTypes;

    ngOnInit(): void {
        this.trellisingObservables.scaleType.subscribe(s => {
            this.selectedScale = s;
            this.previousSelectedScale = s;
        });
    }

    setSelectedScale(event: any): void {
        this.selectedScale = event.selectedValue;
    }

    apply() {
        if (this.selectedScale !== this.previousSelectedScale) {
            this.trellisingDispatcher.updateScale(this.selectedScale);
            this.previousSelectedScale = this.selectedScale;
        }
    }
}
