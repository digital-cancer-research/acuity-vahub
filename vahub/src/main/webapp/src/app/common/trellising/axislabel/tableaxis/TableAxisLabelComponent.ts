import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {List} from 'immutable';

import {AbstractColorBy} from '../AbstractColorBy';
import {ColorByService} from '../ColorByService';
import {ITrellises} from '../../store';

/**
 * This component is used only in the population table.
 *
 * @param trellising - currently selected trellising option
 * @param baseTrellising - list of all available trellising options
 * @param selectedDrug - currently selected drug option (not all trellising options have drugs support)
 * @param customTooltip - variable which provides ability to override default tooltip
 * @param isAllColoringOptionAvailable - variable which is responsible for default option in select (ALL)
 */
@Component({
    selector: 'trellis-table-axis',
    templateUrl: 'TableAxisLabelComponent.html',
    styleUrls: ['TableStyle.css'],
    animations: [
        trigger('enterLeaveTrigger', [
            transition(':enter', [
                style({
                    opacity: '0',
                    top: '0'
                }),
                animate('400ms', style({
                    opacity: 1,
                    top: '40px'
                })),
            ]),
            transition(':leave', [
                animate('400ms', style({
                    opacity: 0,
                    top: '0'
                }))
            ])
        ])
    ]
})
export class TableAxisLabelComponent extends AbstractColorBy implements OnInit, OnChanges {

    tooltip: string;
    drugOption: string;

    @Input() trellising: List<ITrellises>;
    @Input() baseTrellising: List<ITrellises>;
    @Input() selectedDrug: string;
    @Input() customTooltip: string; // looks like useless, we use this in one place, so we can just define tooltip in class
    @Input() isAllColoringOptionAvailable: boolean;

    constructor(public colorByService: ColorByService) {
        super(colorByService);
    }

    ngOnInit(): void {
        this.tooltip = this.customTooltip || '';
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.trellising) {
            this.option = this.colorByService.getSelectedOption(this.trellising, this.isAllColoringOptionAvailable);
            this.drugOption = this.colorByService.getDrugOption(this.selectedDrug, this.option);
        }
        if (this.baseTrellising) {
            this.options = this.colorByService.getAllAvailableOptions(this.baseTrellising);
        }
    }

    openState(): string {
        return this.isOpen ? 'expanded' : 'collapsed';
    }

    closeControl(): void {
        this.option = this.colorByService.getSelectedOption(this.trellising, this.isAllColoringOptionAvailable);
        this.drugOption = this.colorByService.getDrugOption(this.selectedDrug, this.option);
        this.toggle();
    }

    hasSecondRow(): boolean {
        return !!this.option.drugs;
    }
}
