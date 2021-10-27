export abstract class BaseRangeComponent {

    protected nullIfUnchanged(newValue: number|string, oldValue: number|string): number|string {
        if (String(newValue) === String(oldValue)) {
            return null;
        }

        return newValue;
    }
}
