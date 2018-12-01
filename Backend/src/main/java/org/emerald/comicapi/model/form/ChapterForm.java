package org.emerald.comicapi.model.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.validator.annotation.Archive;
import org.emerald.comicapi.validator.annotation.MongoId;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.emerald.comicapi.validator.sequence.ClassLevel;
import org.emerald.comicapi.validator.sequence.FieldLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@UniqueEntity(
        uniqueFields = {"name", "comicId"},
        entityClass = Chapter.class,
        groups = {ClassLevel.class})
public class ChapterForm {
    @NotBlank(groups = {FieldLevel.class})
    private String name;
    @Archive(groups = {FieldLevel.class})
    private MultipartFile pageListFile;

    @NotBlank(groups = {FieldLevel.class})
    @MongoId(groups = {FieldLevel.class})
    private String comicId;

    @JsonIgnore
    public ObjectId getNullableComicId() {
        return comicId == null ? null : new ObjectId(comicId);
    }
}