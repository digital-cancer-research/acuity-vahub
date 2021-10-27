package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class EventWrapper<T extends HasStringId> implements HasStringId {
    private T event;

    public EventWrapper(T event) {
        Validate.notNull(event);
        this.event = event;
    }

    public String getId() {
        return getEvent().getId();
    }

}
