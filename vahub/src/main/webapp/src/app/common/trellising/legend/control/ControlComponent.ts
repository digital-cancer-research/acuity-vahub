import {ChangeDetectionStrategy, Component, Input, OnChanges, SimpleChanges} from '@angular/core';
import {List} from 'immutable';
import {ColorByLabels, ITrellises, TabId, TrellisCategory} from '../../store';
import {Trellising} from '../../store/Trellising';

@Component({
    selector: 'trellising-control',
    templateUrl: 'ControlComponent.html',
    styleUrls: ['ControlComponent.css'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class ControlComponent implements OnChanges {
    static ALL = 'All';
    static NONE = 'NONE';

    open = false;
    enableComponent = false;
    option: string;
    options: string[];

    @Input() noStyles: boolean;
    @Input() baseTrellising: List<ITrellises>;
    @Input() trellising: List<ITrellises>;
    @Input() isAllColoringOptionAvailable: boolean;
    @Input() customControlLabel: ColorByLabels; // if some explanation to what the control is controlling is needed
    @Input() tabId: TabId;

    get staticAll(): string {
        return ControlComponent.ALL;
    }

    constructor(private trellisingMiddleware: Trellising) {
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.trellising) {
            this.option = this.currentSeries();
        }
        if (this.baseTrellising) {
            this.options = this.series();
            this.enableComponent = this.hasSeries();
        }
    }

    updateSeries(event: any): void {
        const newTrellising = <List<ITrellises>>this.trellising.filter((x: ITrellises) => {
            return x.get('category') !== TrellisCategory.NON_MANDATORY_SERIES;
        });
        const filteredBaseTrellising = <List<ITrellises>>this.baseTrellising.filter((trellising: ITrellises) => {
            return trellising.get('trellisedBy') !== ControlComponent.NONE
                && trellising.get('trellisedBy') === event.target.value;
        });
        this.trellisingMiddleware.updateTrellisingOptions(<List<ITrellises>>newTrellising.concat(filteredBaseTrellising), this.tabId);
    }

    toggle(): void {
        this.open = !this.open;
    }

    private series(): string[] {
        return this.baseTrellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).map((x: ITrellises) => x.get('trellisedBy')).toArray();
    }

    private currentSeries(): string {
        const series = this.trellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).map((x: ITrellises) => x.get('trellisedBy')).join(', ');
        return series ? series : this.isAllColoringOptionAvailable ? ControlComponent.ALL : ControlComponent.NONE;
    }

    private hasSeries(): boolean {
        return this.baseTrellising && this.baseTrellising.filter((x: ITrellises) => {
            return x.get('category') === TrellisCategory.NON_MANDATORY_SERIES;
        }).size > 0 && (this.customControlLabel !== ColorByLabels.PRIOR_THERAPY
            && this.customControlLabel !== ColorByLabels.DATE_OF_DIAGNOSIS);
    }
}
