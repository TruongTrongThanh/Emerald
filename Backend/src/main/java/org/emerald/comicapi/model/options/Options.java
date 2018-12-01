package org.emerald.comicapi.model.options;

import lombok.Getter;
import org.emerald.comicapi.validator.annotation.ArrayFormat;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.lang.Nullable;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Positive;

@Getter
public class Options {
    private @Positive Integer page;
    private @Positive Integer size;
    private String direction;
    private @ArrayFormat String sortOrder;

    @PersistenceConstructor
    public Options(@Nullable Integer page,
                   @Nullable Integer size,
                   @Nullable String direction,
                   @Nullable String sortOrder) {
        this.page = page;
        this.size = size;
        this.direction = direction;
        this.sortOrder = sortOrder;
    }

    public PageRequest getPageRequest() {
        return PageRequest.of(page == null ? 0 : page - 1,
                size == null ? Integer.MAX_VALUE : size,
                this.getSort());
    }

    public String[] sortOrderToArray() {
        return sortOrder != null ? sortOrder.split(",") : new String[]{"id"};
    }

    public Sort getSort() {
        Direction direct = direction == null ? Direction.ASC : Direction.fromString(direction);
        String[] sort = this.sortOrderToArray();
        return Sort.by(direct, sort);
    }

    @AssertTrue(message = "Bad direction string")
    private boolean direction() {
        if (direction != null) {
            try {
                Direction.fromString(direction);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }
        return true;
    }
}