package org.emerald.comicapi.model.form;

import lombok.Getter;
import lombok.Setter;
import org.emerald.comicapi.model.data.User;
import org.emerald.comicapi.validator.annotation.UniqueEntity;
import org.emerald.comicapi.validator.sequence.ClassLevel;
import org.emerald.comicapi.validator.sequence.FieldLevel;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
@UniqueEntity(
        uniqueFields = {"username"},
        entityClass = User.class,
        groups = {ClassLevel.class})
public class UserForm {
    @Size(
        message = "Username must be over 4 characters.",
        min = 4,
        groups = {FieldLevel.class})
    @NotBlank(groups = {FieldLevel.class})
    private String username;

    @NotBlank(groups = {FieldLevel.class})
    private String password;
    @NotBlank(groups = {FieldLevel.class})
    private String confirmPassword;
    @Email(groups = {FieldLevel.class})
    private String email;

    @AssertTrue(
        message = "Wrong confirm password.",
        groups = {FieldLevel.class})
    public boolean confirmPassword() {
        if (password != null && confirmPassword != null)
            return password.equals(confirmPassword);
        return true;
    }
}
