import {TestBed, inject} from '@angular/core/testing';
import {DropdownService} from './DropdownService';
import {DropdownModel} from './DropdownModel';

describe('DropdownService class', () => {
    describe('WHEN initialized', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event emitter should be defined', inject([DropdownService], (service: DropdownService) => {
            expect(service).toBeDefined();
        }));
    });

    describe('WHEN opening a dropdown item', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event should be emitted', inject([DropdownService], (service: DropdownService) => {

            const validator = jasmine.createSpyObj('validator', ['called']);
            service.event.subscribe(
                (models: any) => {
                    validator.called(models);
                }
            );
            const model = new DropdownModel();
            service.add(model);
            service.open('1');

            expect(validator.called).toHaveBeenCalledWith([model]   );
        }));
    });

    describe('WHEN close all is called', () => {
        beforeEach(() => {
            TestBed.configureTestingModule({
                providers: [
                    DropdownService
                ]
            });
        });
        it('THEN the event should be emitted', inject([DropdownService], (service: DropdownService) => {

            const validator = jasmine.createSpyObj('validator', ['called']);
            service.event.subscribe(
                (models: any) => {
                    validator.called(models);
                }
            );
            const model = new DropdownModel();
            service.add(model);
            service.open('1');

            expect(validator.called).toHaveBeenCalledWith([model]   );
        }));
    });
});

