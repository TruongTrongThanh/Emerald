package org.emerald.comicapi.model.form;

import lombok.Getter;
import lombok.Setter;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.validator.annotation.Image;
import org.emerald.comicapi.validator.annotation.JsonArray;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.emerald.comicapi.validator.sequence.ClassLevel;
import org.emerald.comicapi.validator.sequence.FieldLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Positive;

@Getter @Setter
@UniqueEntity(
        uniqueFields = {"name"},
        entityClass = Comic.class,
        groups = {ClassLevel.class})
public class ComicUpdate {
    private String name;
    private String author;
    private String demographic;
    private String description;

    @JsonArray(groups = {FieldLevel.class})
    private String genres;
    @Positive(groups = {FieldLevel.class})
    private Integer year;
    @Image(groups = {FieldLevel.class})
    private MultipartFile coverFile;

    public String getNameFallback(String fallback) {
        return name != null ? name : fallback;
    }
    public String getAuthorFallback(String fallback) {
        return author != null ? author : fallback;
    }
    public String getDemographicFallback(String fallback) {
        return demographic != null ? demographic : fallback;
    }
    public String getDescriptionFallback(String fallback) {
        return description != null ? description : fallback;
    }
    public Integer getYearFallback(Integer fallback) {
        return year != null ? year : fallback;
    }
}
