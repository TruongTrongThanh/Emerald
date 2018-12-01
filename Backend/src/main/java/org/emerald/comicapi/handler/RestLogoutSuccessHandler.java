package org.emerald.comicapi.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestLogoutSuccessHandler implements LogoutSuccessHandler {

    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        ResponseFormat format = new ResponseFormat(200, "Logout success!", request);
        String result = objectMapper.writeValueAsString(format);
        response.getOutputStream().print(result);
    }
}
