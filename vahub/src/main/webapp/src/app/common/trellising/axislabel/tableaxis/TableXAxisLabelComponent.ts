import {
    Component, animate, state, style, trigger, transition, Input, OnChanges,
    Output, EventEmitter, OnInit, OnDestroy, SimpleChanges
} from '@angular/core';
import {XAxisLabelComponent} from '../xaxis/XAxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {TabId, DynamicAxis} from '../../store/ITrellising';
import {XAxisLabelService} from '../xaxis/XAxisLabelService';

@Component({
    selector: 'trellis-table-xaxis',
    templateUrl: '../AxisLabelComponent.html',
    styleUrls: ['TableStyle.css'],
    animations: [
        trigger('openClose', [
            state('collapsed, void', style({opacity: '0', bottom: '0px'})),
            state('expanded', style({opacity: '1', bottom: '120px'})),
            transition('collapsed <=> expanded', [animate(500, style({opacity: '1', bottom: '120px'}))])
        ])
    ]
})
export class TableXAxisLabelComponent extends XAxisLabelComponent implements OnInit, OnDestroy, OnChanges {

    @Input() options: any;
    @Input() option: DynamicAxis | string;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Output() update: EventEmitter<DynamicAxis> = new EventEmitter<DynamicAxis>();

    constructor(protected axisLabelService: AxisLabelService,
                protected xAxisLabelService: XAxisLabelService) {
        super(axisLabelService, xAxisLabelService);
        this.tooltip = 'X-Axis settings';
    }

    ngOnInit(): void {
        this.onInit();

        this.tooltip = this.customTooltip || this.tooltip;
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.localOption = <DynamicAxis>this.option;
        if (this.options && this.localOption) {
            this.availableValueStrings = this.xAxisLabelService.generateAvailableValueStrings(this.options);
            this.updateAdditionalSelection();
        }
    }
}
