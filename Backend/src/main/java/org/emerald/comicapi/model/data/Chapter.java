package org.emerald.comicapi.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.form.ChapterForm;
import org.emerald.comicapi.model.form.ChapterUpdate;
import org.json.JSONArray;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Document(collection = "chapters")
@JsonInclude(Include.NON_EMPTY)
public class Chapter {
    private @Id @Getter ObjectId id;
    private @Getter String name;
    private @Getter @Setter boolean isProcessing;
    private @Getter @Setter ObjectId comicId;
    private @Getter @Setter List<String> errors = new ArrayList<>();
    private @Getter @CreatedDate LocalDateTime createdAt;
    private @Getter @LastModifiedDate LocalDateTime modifiedAt;

    @JsonProperty(access = Access.READ_ONLY)
    private @Getter @Setter Comic comic;
    @JsonProperty(access = Access.READ_ONLY)
    private @Getter @Setter Integer totalPages;

    public Chapter(){}
    public Chapter(ChapterForm chapterForm) {
        name = chapterForm.getName();
        isProcessing = false;
        comicId = chapterForm.getNullableComicId();
    }
    public Chapter(ChapterUpdate chapterUpdate) {
        if (chapterUpdate != null) {
            name = chapterUpdate.getName();
            comicId = chapterUpdate.getNullableComicId();
        }
    }
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    public void addError(String error) {
        errors.add(error);
    }
    public void removeError(String error) {
        errors.remove(error);
    }

    public void update(ChapterUpdate chapterUpdate) {
        name = chapterUpdate.getName(name);
        comicId = chapterUpdate.getComicId(comicId);

        if (chapterUpdate.getErrors() != null)
            setErrorsFromString(chapterUpdate.getErrors());
    }

    @JsonIgnore
    public String getIdHexString() {
        return id == null ? null : id.toHexString();
    }

    @JsonIgnore
    public Path getLocalPath() {
        return Paths.get(Comic.ROOT_LOCATION, getComicId().toHexString(), getIdHexString());
    }

    private void setErrorsFromString(String errors) {
        if (errors != null) {
            JSONArray array = new JSONArray(errors);
            this.errors = array.toList()
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.toList());
        }
    }
}