package org.emerald.comicapi.model.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.emerald.comicapi.model.form.ComicForm;
import org.emerald.comicapi.model.form.ComicUpdate;
import org.emerald.comicapi.model.options.ComicOptions;
import org.json.JSONArray;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Document(collection = "comics")
@JsonInclude(Include.NON_EMPTY)
@Getter
public class Comic {

    public static final String ROOT_LOCATION = "imgs\\mgs";

    private @Id ObjectId id;
    private @Setter @TextIndexed String name;
    private @Setter @TextIndexed String author;
    private @Setter String demographic;
    private @Setter List<String> genres;
    private @Setter @TextIndexed String description;
    private @Setter Integer year;
    private @Setter URL coverUrl;
    private @Setter URL thumbUrl;
    private @Getter @CreatedDate LocalDateTime createdAt;
    private @Getter @LastModifiedDate LocalDateTime modifiedAt;

    public Comic(){}
    public Comic(ComicForm comicForm) {
        name = comicForm.getName();
        author = comicForm.getAuthor();
        demographic = comicForm.getDemographic();
        setGenresFromString(comicForm.getGenres());
        description = comicForm.getDescription();
        year = comicForm.getYear();
    }
    public Comic(ComicOptions comicOptions) {
        if (comicOptions != null) {
            name = comicOptions.getName();
            author = comicOptions.getAuthor();
            demographic = comicOptions.getDemographic();
            setGenresFromString(comicOptions.getGenres());
            description = comicOptions.getDescription();
            year = comicOptions.getYear();
        }
    }
    public Comic(ComicUpdate comicUpdate) {
        if (comicUpdate != null) {
            name = comicUpdate.getName();
            author = comicUpdate.getAuthor();
            demographic = comicUpdate.getDemographic();
            setGenresFromString(comicUpdate.getGenres());
            description = comicUpdate.getDescription();
            year = comicUpdate.getYear();
        }
    }

    @JsonIgnore
    public String getIdHexString() {
        return id == null ? null : id.toHexString();
    }

    @JsonIgnore
    public File getDirectory() {
        return new File(String.format("%s\\%s", ROOT_LOCATION, getIdHexString()));
    }

    private void setGenresFromString(String genres) {
        if (genres != null) {
            JSONArray array = new JSONArray(genres);
            this.genres = array.toList()
                    .stream()
                    .map(Objects::toString)
                    .collect(Collectors.toList());
        }
    }

    public void update(ComicUpdate comicUpdate) {
        name = comicUpdate.getNameFallback(name);
        author = comicUpdate.getAuthorFallback(author);
        demographic = comicUpdate.getDemographicFallback(demographic);
        description = comicUpdate.getDescriptionFallback(description);
        year = comicUpdate.getYearFallback(year);

        if (comicUpdate.getGenres() != null)
            setGenresFromString(comicUpdate.getGenres());
    }
}