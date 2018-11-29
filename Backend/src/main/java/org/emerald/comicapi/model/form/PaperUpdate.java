package org.emerald.comicapi.model.form;

import lombok.Getter;
import lombok.Setter;

import org.bson.types.ObjectId;
import org.emerald.comicapi.model.data.Paper;
import org.emerald.comicapi.validator.annotation.Archive;
import org.emerald.comicapi.validator.annotation.Image;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.emerald.comicapi.validator.sequence.ClassLevel;
import org.emerald.comicapi.validator.sequence.FieldLevel;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@UniqueEntity(uniqueFields = {"name", "chapterId"}, entityClass = Paper.class, groups = {ClassLevel.class})
public class PaperUpdate {
    private String name;
    private String chapterId;

    @Image(groups = {FieldLevel.class})
    private MultipartFile ImageFile;
    @Archive(groups = {FieldLevel.class})
    private MultipartFile zipFile;

    public String getName(String fallback) {
        return name != null ? name : fallback;
    }
    public ObjectId getChapterId(ObjectId fallback) {
        return chapterId != null ? new ObjectId(chapterId) : fallback;
    }
}
