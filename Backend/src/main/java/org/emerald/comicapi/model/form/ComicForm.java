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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter @Setter
@UniqueEntity(
        uniqueFields = {"name"},
        entityClass = Comic.class,
        groups = {ClassLevel.class})
public class ComicForm {
    @NotBlank(groups = {FieldLevel.class})
    private String name;
    @NotBlank(groups = {FieldLevel.class})
    private String author;
    @NotBlank(groups = {FieldLevel.class})
    private String demographic;
    @Positive(groups = {FieldLevel.class})
    private Integer year;

    @NotBlank(groups = {FieldLevel.class})
    @JsonArray(groups = {FieldLevel.class})
    private String genres;

    @Image(groups = {FieldLevel.class})
    @NotNull(groups = {FieldLevel.class})
    private MultipartFile coverFile;

    private String description;
}