package org.emerald.comicapi.model.form;

import lombok.Data;

@Data
public class LoginForm {
    public String username;
    public String password;
    public boolean rememberMe;
}
