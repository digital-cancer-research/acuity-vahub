import {async, TestBed} from '@angular/core/testing';
import {DropdownComponent} from './DropdownComponent';
import {DropdownItem} from './DropdownItem';
import {DropdownService} from './DropdownService';

describe('GIVEN DropdownComponent', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                {provide: DropdownService, useClass: DropdownService}
            ],
            declarations: [DropdownComponent]
        });
    });

    const values: Array<DropdownItem> = [
        {displayName: 'displayName1', serverName: 'serverName1'},
        {displayName: 'displayName2', serverName: 'serverName2'},
        {displayName: 'displayName3', serverName: 'serverName3'}
    ];

    function initFixture(fixture, currentSelectedItem, avaliableItems, callback): any {
        fixture.componentInstance.name = 'x';
        fixture.componentInstance.currentSelectedItem = currentSelectedItem;
        fixture.componentInstance.avaliableItems = avaliableItems;
        if (callback) {
            fixture.componentInstance.dropdownChanged.subscribe(callback);
        }

        // fixture.componentInstance.ngOnInit();
        fixture.componentInstance.ngOnChanges();
        return fixture.nativeElement;
    }

    describe('WHEN the component is loaded', () => {

        let validator: any;

        beforeEach(() => {
            validator = jasmine.createSpyObj('validator', ['called']);
        });

        it('THEN the drop downs are populated with empty selected',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(DropdownComponent);

                    initFixture(rootTC, null, values, null);

                    rootTC.detectChanges();

                    expect(rootTC.componentInstance.currentSelectedItem).toEqual(values[0]);
                });
            }));

        it('THEN the drop downs are populated with correct html dropdown list',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(DropdownComponent);
                    const element = initFixture(rootTC, null, values, null);

                    rootTC.detectChanges();
                    element.querySelectorAll('.dropdown-toggle')[0].click();
                    rootTC.detectChanges();
                    expect(element.querySelector('button').textContent).toContain('displayName1');
                    expect(element.querySelectorAll('li').length).toEqual(3);
                    expect(element.querySelector('a').textContent).toContain('displayName1');
                    expect(element.querySelectorAll('a')[1].textContent).toContain('displayName2');
                });
            }));

        it('THEN when clicking on a item the eventemitter should be called',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const rootTC = TestBed.createComponent(DropdownComponent);
                    const element = initFixture(rootTC, null, values, null);

                    rootTC.componentInstance.dropdownChanged.subscribe((result) => {
                        console.log('subscribe to dropdown' + JSON.stringify(result));
                        validator.called(result);
                    });

                    rootTC.detectChanges();

                    expect(rootTC.componentInstance.currentSelectedItem).toEqual(values[0]);
                    element.querySelectorAll('.dropdown-toggle')[0].click();
                    rootTC.detectChanges();
                    element.querySelectorAll('a')[1].click();
                    rootTC.detectChanges();
                    rootTC.detectChanges();

                    console.log('checking subscribe to dropdown');
                    // expect(validator.called).toHaveBeenCalledWith(values[1]);
                    expect(rootTC.componentInstance.currentSelectedItem).toEqual(values[1]);
                });
            }));
    });
});
