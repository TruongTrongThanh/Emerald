package org.emerald.comicapi.model.data;

import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.form.PaperForm;
import org.emerald.comicapi.model.form.PaperUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "papers")
@JsonInclude(Include.NON_EMPTY)
public class Paper {
    private @Id ObjectId id;
    private @Setter @Getter String name;
    private @Setter @Getter URL imageUrl;
    private @Setter @Getter URL thumbUrl;
    private @CreatedDate LocalDateTime createdAt;
    private @LastModifiedDate LocalDateTime ModifiedAt;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private @Getter ObjectId chapterId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private @Getter @Setter Chapter chapter;

    public Paper(){}
    public Paper(String name, String chapterId) {
        this.name = name;
        this.chapterId = new ObjectId(chapterId);
    }
    public Paper(String name, String chapterId, URL imageUrl, URL thumbUrl) {
        this(name, chapterId);
        this.imageUrl = imageUrl;
        this.thumbUrl = thumbUrl;
    }
    public Paper(PaperForm paperForm) {
        name = paperForm.getName();
        chapterId = new ObjectId(paperForm.getChapterId());
    }
    public Paper(PaperUpdate paperUpdate) {
        name = paperUpdate.getName();
        chapterId = paperUpdate.getChapterId(null);
    }
    public void update(PaperUpdate paperUpdate) {
        name = paperUpdate.getName(name);
        chapterId = paperUpdate.getChapterId(chapterId);
    }
}