import {Component, Input, OnInit} from '@angular/core';
import {DetailsOnDemandHeightService} from '../common/trellising/detailsondemand/services/DetailsOnDemandHeightService';

@Component({
    selector: 'details-table',
    templateUrl: 'DetailsTableComponent.html',
    styleUrls: ['../common/trellising/detailsondemand/DetailsOnDemandComponent.css', './DetailsTableComponent.css']
})
export class DetailsTableComponent implements OnInit {
    @Input() details: any;

    constructor(private detailsOnDemandHeightService: DetailsOnDemandHeightService) {

    }

    ngOnInit(): void {
        const $draggable = $('.draggable').draggabilly({
            axis: 'y',
            handle: '.details-area-header'
        });

        this.bindToDrag($draggable);
    }

    openOrClose(): void {
        this.detailsOnDemandHeightService.onExpandCollapseButtonPress();
    }

    onWindowResize($event): void {
        this.detailsOnDemandHeightService.adjustTableHeightAfterWindowResize();
    }

    private bindToDrag($draggable: any): void {
        const that = this;
        const draggie = $draggable.data('draggabilly');
        $draggable.on('dragMove', () => that.detailsOnDemandHeightService.onDragBar(draggie));
        that.detailsOnDemandHeightService.openToPreviousHeight();
    }
}
