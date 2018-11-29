package org.emerald.comicapi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Component
public class GlobalVariable implements InitializingBean {

    public String SERVER_HOSTNAME;
    @Value("${server.port}")
    public Integer SERVER_PORT;
    @Value("${app.server.localhost}")
    public boolean USE_LOCALHOST;

    @Value("${app.store.path}")
    public String STORE_PATH;

    @Value("${app.image.type}")
    public String[] ACCEPT_IMAGE_TYPE;
    @Value("${app.archive.type}")
    public String[] ACCEPT_ARCHIVE_TYPE;

    @Value("${app.page.limit.chapters}")
    public Integer PAGE_LIMIT_CHAPTERS;

    @Value("${app.upload.size.limit}")
    public String SIZE_LIMIT;

    @Value("${app.secret.key}")
    public String SECRET_KEY;

    private @Getter List<String> acceptImageTypes;
    private @Getter List<String> acceptArchiveType;
    private @Getter Long sizeLimit;

    public void setSizeLimit() {
        long number = Long.parseLong(SIZE_LIMIT.replaceAll("[a-zA-Z]+", ""));
        String symbol = SIZE_LIMIT.replaceAll("\\d+", "");

        switch (symbol) {
            case "KB":
                number *= 1000;
                break;
            case "MB":
                number *= 1000000;
                break;
            case "GB":
                number *= 1000000000;
                break;
            default:
                throw new RuntimeException("Can't detect symbol size limit in application.properties.");
        }
        sizeLimit = number;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SERVER_HOSTNAME = USE_LOCALHOST ? "localhost" : InetAddress.getLocalHost().getHostAddress();
        acceptImageTypes = Arrays.asList(ACCEPT_IMAGE_TYPE);
        acceptArchiveType = Arrays.asList(ACCEPT_ARCHIVE_TYPE);
        setSizeLimit();
    }
}