package org.emerald.comicapi.model.form;

import java.lang.reflect.Field;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.validator.annotation.Archive;
import org.emerald.comicapi.validator.annotation.JsonArray;
import org.emerald.comicapi.validator.annotation.MongoId;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.emerald.comicapi.validator.sequence.ClassLevel;
import org.emerald.comicapi.validator.sequence.FieldLevel;
import org.json.JSONArray;
import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@UniqueEntity(
        uniqueFields = {"name", "comicId"},
        entityClass = Chapter.class,
        groups = {ClassLevel.class})
public class ChapterUpdate {
    private String name;

    @JsonArray(groups = {FieldLevel.class})
    private String errors;
    @MongoId(groups = {FieldLevel.class})
    private String comicId;

    @JsonIgnore
    public ObjectId getNullableComicId() {
        return comicId == null ? null : new ObjectId(comicId);
    }

    public String getName(String fallback) {
        return name != null ? name : fallback;
    }
    public ObjectId getComicId(ObjectId fallback) {
        return comicId != null ? new ObjectId(comicId) : fallback;
    }
}