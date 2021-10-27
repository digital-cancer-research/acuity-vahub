import {Component, OnInit} from '@angular/core';

import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {ScaleTypes} from '../../../../common/trellising/store';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';


@Component({
    selector: 'dose-proportionality-settings',
    templateUrl: 'DoseProportionalitySettingsComponent.html',
    styleUrls: ['../../../filters.css']
})
export class DoseProportionalitySettingsComponent implements OnInit {

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher) {}

    isOpen = false;
    selectedScale: ScaleTypes;
    previousSelectedScale: ScaleTypes;

    ngOnInit(): void {
        this.trellisingObservables.scaleType.subscribe(s => this.selectedScale = s);
        this.previousSelectedScale = this.selectedScale;
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
