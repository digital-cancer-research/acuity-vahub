import {Component} from '@angular/core';
@Component({
    selector: 'timeline-configurations',
    templateUrl: 'TimelineConfigurationsComponent.html',
    styleUrls: ['../../../filters/filters.css']
})
export class TimelineConfigurationsComponent {
    openElement: string;

    openContent($event, elementName): void {
        const filterTitleParent = $($event.target).closest('.configuration-item');
        const isOpen = filterTitleParent.hasClass('active');
        $('.configuration-item').removeClass('active');
        this.openElement = elementName === this.openElement ? null : elementName;
        if (isOpen) {
            filterTitleParent.removeClass('active');
        } else {
            filterTitleParent.addClass('active');
        }
    }
}
