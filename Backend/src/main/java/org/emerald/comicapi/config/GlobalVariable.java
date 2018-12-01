package org.emerald.comicapi.config;

import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
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