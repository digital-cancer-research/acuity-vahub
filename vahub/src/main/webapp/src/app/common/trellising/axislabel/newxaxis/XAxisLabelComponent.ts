import {
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnDestroy,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import {cloneDeep, omit} from 'lodash';
import {fromJS} from 'immutable';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {TextUtils} from '../../../utils/TextUtils';

import {AxisLabelComponent} from '../AxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {TabId} from '../../store/ITrellising';
import {generateAvailableOptions, getSelectedOption, NewXAxisLabelService} from './XAxisLabelService';
import {DisplayedGroupBySetting, GroupBySetting, XAxisOptions} from '../../store/actions/TrellisingActionCreator';

@Component({
    selector: 'new-trellis-xaxis',
    templateUrl: 'AxisLabelComponent.html',
    styleUrls: ['XStyle.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('openClose', [
            state('openTop', style({
                opacity: 1,
                bottom: '60px'
            })),
            state('openBottom', style({
                opacity: 1,
                top: '40px'
            })),
            // openTop transitions
            transition('* => openTop', [
                style({
                    opacity: '0',
                    bottom: '0'
                }),
                animate('400ms')
            ]),
            transition('openTop => *', [
                animate('400ms', style({
                    opacity: '0',
                    bottom: '0'
                }))
            ]),
            // openBottom transitions
            transition('* => openBottom', [
                style({
                    opacity: '0',
                    top: '0'
                }),
                animate('400ms')
            ]),
            transition('openBottom => *', [
                animate('400ms', style({
                    opacity: '0',
                    top: '0'
                }))
            ])
        ])
    ]
})
export class NewXAxisLabelComponent extends AxisLabelComponent implements OnInit, OnDestroy, OnChanges {
    textUtils = TextUtils;
    @Input() option: GroupBySetting;
    @Input() options: XAxisOptions;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Input() isOpenBottom: boolean;
    @Output() update: EventEmitter<any> = new EventEmitter<any>();

    selectedOption: DisplayedGroupBySetting;
    availableOptions: DisplayedGroupBySetting[];

    constructor(protected axisLabelService: AxisLabelService) {
        super(axisLabelService);
    }

    ngOnInit(): void {
        this.onInit();
        this.tooltip = this.customTooltip || 'X-Axis settings';
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.options) {
            this.availableOptions = generateAvailableOptions(this.options, this.tabId);
        }
        if (this.option) {
            this.selectedOption = getSelectedOption(this.option);
        }
    }

    changeBinSize() {
        if (this.selectedOption.params.BIN_SIZE <= 1) {
            this.selectedOption.params.BIN_SIZE = 1;
        }
        this.selectedOption.params.BIN_SIZE = Math.round(this.selectedOption.params.BIN_SIZE);
    }

    updateDisplayValue(): void {
        this.selectedOption = cloneDeep(this.availableOptions
            .find((option) => option.displayedOption === this.selectedOption.displayedOption));
        if (!this.selectedOption.params || !(this.selectedOption.params.BIN_SIZE || this.selectedOption.params.DRUG_NAME)) {
            this.updateOption();
        }
    }

    closeControl(): void {
        if (this.option) {
            this.selectedOption = getSelectedOption(this.option);
        }
        this.isOpen = false;
    }

    updateOption(): void {
        this.update.emit(fromJS(omit(this.selectedOption, 'displayedOption')));
        this.isOpen = false;
    }

    getAnimationState(): string {
        if (this.isOpen) {
            return this.isOpenBottom ? 'openBottom' : 'openTop';
        }

        return 'close';
    }
}
