package org.emerald.comicapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.emerald.comicapi.model.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service("rememberMeServices")
public class CustomRememberMeServices extends PersistentTokenBasedRememberMeServices {

    @Autowired
    ObjectMapper objectMapper;

    public CustomRememberMeServices(@Value("{app.secret.key}") String key,
                                    UserDetailsService userDetailsService,
                                    PersistentTokenRepository tokenRepository) {
        super(key, userDetailsService, tokenRepository);
    }

    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter) {
        LoginForm loginForm = (LoginForm) request.getAttribute("loginForm");
        return loginForm.isRememberMe();
    }
}
