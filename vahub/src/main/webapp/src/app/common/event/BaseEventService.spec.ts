import {BaseEventService} from './BaseEventService';
import {Subscription} from 'rxjs/Subscription';

class MyBaseEventService extends BaseEventService {

    getDict(): any {
        return this.dict;
    }
}

describe('GIVEN a BaseEventService class', () => {

    let baseEventService: MyBaseEventService;

    beforeEach(() => {
        baseEventService = new MyBaseEventService();
    });

    it('THEN it should remove subscription when adding another', () => {
        const sub1 = new Subscription();
        const sub2 = new Subscription();

        baseEventService.addSubscription('key', sub1);
        baseEventService.addSubscription('key', sub2);

        const gotSub = baseEventService.getDict()['key'];
        console.log(baseEventService.getDict());
        expect(gotSub).toEqual(sub2);
        expect(gotSub).not.toEqual(sub1);
    });
});
