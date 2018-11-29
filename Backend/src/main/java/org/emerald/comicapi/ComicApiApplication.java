package org.emerald.comicapi;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.model.data.Paper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.rest.RepositoryRestMvcAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.CloseableIterator;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.MalformedURLException;
import java.net.URL;

@SpringBootApplication(exclude = RepositoryRestMvcAutoConfiguration.class)
@EnableAsync
@EnableMongoAuditing
public class ComicApiApplication {
    private final Logger logger = LoggerFactory.getLogger(ComicApiApplication.class);

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    GlobalVariable globalVariable;

    public static void main(String[] args) {
		SpringApplication.run(ComicApiApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
    public void afterStartup() {
        final String protocol = "http";
        final String hostname = String.format("%s:%s", globalVariable.SERVER_HOSTNAME, globalVariable.SERVER_PORT);

        CloseableIterator<Comic> comicIterator = mongoTemplate.stream(new Query(), Comic.class);
        comicIterator.forEachRemaining(comic -> {
            URL coverUrl = comic.getCoverUrl();
            URL thumbUrl = comic.getThumbUrl();
            String dbHostname = String.format("%s:%s", coverUrl.getHost(), coverUrl.getPort());

            if (!hostname.equals(dbHostname)) {
                try {
                    URL modifiedCoverUrl = new URL(
                            protocol,
                            globalVariable.SERVER_HOSTNAME,
                            globalVariable.SERVER_PORT,
                            coverUrl.getFile()
                    );
                    URL modifiedThumbUrl = new URL(
                            protocol,
                            globalVariable.SERVER_HOSTNAME,
                            globalVariable.SERVER_PORT,
                            thumbUrl.getFile()
                    );
                    comic.setCoverUrl(modifiedCoverUrl);
                    comic.setThumbUrl(modifiedThumbUrl);
                    mongoTemplate.save(comic);
                }
                catch (MalformedURLException ex) {
                    throw new RuntimeException("Can't construct URL");
                }
            }
        });
        comicIterator.close();

        CloseableIterator<Paper> paperIterator = mongoTemplate.stream(new Query(), Paper.class);
        paperIterator.forEachRemaining(paper -> {
            URL coverUrl = paper.getImageUrl();
            URL thumbUrl = paper.getThumbUrl();
            String dbHostname = String.format("%s:%s", coverUrl.getHost(), coverUrl.getPort());

            if (!hostname.equals(dbHostname)) {
                try {
                    URL modifiedCoverUrl = new URL(
                            protocol,
                            globalVariable.SERVER_HOSTNAME,
                            globalVariable.SERVER_PORT,
                            coverUrl.getFile()
                    );
                    URL modifiedThumbUrl = new URL(
                            protocol,
                            globalVariable.SERVER_HOSTNAME,
                            globalVariable.SERVER_PORT,
                            thumbUrl.getFile()
                    );
                    paper.setImageUrl(modifiedCoverUrl);
                    paper.setThumbUrl(modifiedThumbUrl);
                    mongoTemplate.save(paper);
                }
                catch (MalformedURLException ex) {
                    throw new RuntimeException("Can't construct URL");
                }
            }
        });
        paperIterator.close();
    }
}
