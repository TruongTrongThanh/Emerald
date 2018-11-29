package org.emerald.comicapi.model.form;

import lombok.Getter;
import lombok.Setter;
import org.emerald.comicapi.validator.annotation.Image;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public class PaperForm {
    private @NotBlank String name;

    private @NotBlank String chapterId;
    private @NotBlank @Image MultipartFile ImageFile;
}
