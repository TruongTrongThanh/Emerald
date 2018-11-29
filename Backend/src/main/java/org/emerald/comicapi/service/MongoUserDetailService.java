package org.emerald.comicapi.service;

import org.emerald.comicapi.model.data.User;
import org.emerald.comicapi.model.form.UserForm;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MongoUserDetailService extends UserDetailsService {
    void hashPassword(User user);
    User register(UserForm form, String role);
}
