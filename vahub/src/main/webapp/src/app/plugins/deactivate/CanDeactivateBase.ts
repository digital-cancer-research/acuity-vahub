import {Observable} from 'rxjs/Observable';
import {Subject} from 'rxjs/Subject';
import {Observer} from 'rxjs/Observer';
import {isEqual} from 'lodash';
import {Router, NavigationStart, NavigationEnd} from '@angular/router';
import {PopulationFiltersModel} from '../../filters/module';
import {FiltersService} from '../../data/FiltersService';
import {FilterId} from '../../common/module';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {TabId} from '../../common/trellising/store/ITrellising';
import {AbstractFiltersModel} from '../../filters/dataTypes/AbstractFiltersModel';

export abstract class CanDeactivateBase {
    protected eventFiltersModel: AbstractFiltersModel;
    protected canDeactivateObservable: Observable<boolean> = new Observable<boolean>();
    protected eventFiltersState: any = null;
    public deactivateWarning: Subject<any> = new Subject<any>();
    public filterId: FilterId;
    public canDeactivateObserver: Observer<boolean>;
    private destinationRoute: string;
    private navigateToTimeline: TabId;

    constructor(filtersModel: AbstractFiltersModel,
                filterId: FilterId,
                protected filtersService: FiltersService,
                protected populationFiltersModel: PopulationFiltersModel,
                protected router: Router,
                protected timelineConfigService: TimelineConfigService) {
        this.eventFiltersModel = filtersModel;
        this.filterId = filterId;
        this.subscribeToRouter();
        this.subscribeToTimelineNavigation();
        this.subscribeToEventFilterReset();
    }

    setAsPopulation(): void {
        console.log('Set as population');
        this.filtersService.getSubjectsInFilters(this.filterId).subscribe((subjectIds) => {
            this.populationFiltersModel.setAsPopulation(subjectIds);
        });
    }

    updateFiltersState(): void {
        this.eventFiltersState = this.eventFiltersModel.transformFiltersToServer();
    }

    protected canDeactivateBase(): Observable<boolean> | boolean {
        if (
            localStorage.getItem('doNotShowAgain') !== 'true'
            && !isEqual(this.eventFiltersModel.transformFiltersToServer(), this.eventFiltersState)
            && !isEqual(this.eventFiltersModel.transformFiltersToServer(), {})
            && this.destinationRoute.indexOf('plugins') !== -1
            && !this.navigateToTimeline
        ) {
            const canDeactivateObservable = new Observable<boolean>((observer: Observer<boolean>) => {
                    this.canDeactivateObserver = observer;
                }
            ).take(1);
            this.deactivateWarning.next(null);
            return canDeactivateObservable;
        } else {
            return true;
        }
    }

    private subscribeToRouter(): void {
        this.router.events.subscribe((event: any) => {
            if (event instanceof NavigationStart) {
                this.destinationRoute = event.url;
            }
            if (event instanceof NavigationEnd) {
                this.navigateToTimeline = null;
            }
        });
    }

    private subscribeToTimelineNavigation(): void {
        this.timelineConfigService.navigateToTimeline.subscribe((tabId: TabId) => {
            this.navigateToTimeline = tabId;
        });
    }

    private subscribeToEventFilterReset(): void {
        // we want to update state when event filter is reseted
        // so we will show modal window when event filter is removed and applied once again
        // and not show it when nothing happened with event filter
        this.eventFiltersModel.resetEventFilterEvent.subscribe(() => {
            this.updateFiltersState();
        });
    }
}
