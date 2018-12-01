package org.emerald.comicapi.model.options;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Positive;

@Getter
@Setter
public class ComicOptions extends Options {
    private String name;
    private String author;
    private String demographic;
    private String genres;
    private String description;
    private @Positive Integer year;

    @PersistenceConstructor
    public ComicOptions(@Nullable String name,
                        @Nullable String author,
                        @Nullable String demographic,
                        @Nullable String genres,
                        @Nullable String description,
                        @Nullable Integer page,
                        @Nullable Integer size,
                        @Nullable String direction,
                        @Nullable String sortOrder,
                        @Nullable Integer year) {
        super(page, size, direction, sortOrder);

        this.name = name;
        this.author = author;
        this.demographic = demographic;
        this.genres = genres;
        this.description = description;
        this.year = year;
    }
}