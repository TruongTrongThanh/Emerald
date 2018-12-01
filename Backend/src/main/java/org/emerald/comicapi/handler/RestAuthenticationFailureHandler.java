package org.emerald.comicapi.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(400);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseFormat format = new ResponseFormat(400, "Username or password is wrong", request);
        String res = objectMapper.writeValueAsString(format);
        response.getOutputStream().print(res);
    }
}
