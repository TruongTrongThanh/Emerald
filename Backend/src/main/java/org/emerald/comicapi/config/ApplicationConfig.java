package org.emerald.comicapi.config;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.catalina.Context;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.bson.types.ObjectId;
import org.emerald.comicapi.ComicApiApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.io.IOException;

@Configuration
@ComponentScan
public class ApplicationConfig {
    @Autowired
    GlobalVariable globalVariable;

    private final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    @Bean
    public TaskExecutor paperExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("PaperExecutor-");
        threadPoolTaskExecutor.setCorePoolSize(2);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(50);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver() {
            private static final String POST_METHOD = "POST";
            private static final String PUT_METHOD = "PUT";

            @Override
            public boolean isMultipart(HttpServletRequest request) {
                boolean isMultipartRequest = false;
                if (request != null) {
                    if (POST_METHOD.equalsIgnoreCase(request.getMethod()) || PUT_METHOD.equalsIgnoreCase(request.getMethod())) {
                        isMultipartRequest = FileUploadBase.isMultipartContent(new ServletRequestContext(request));
                    }
                }
                return isMultipartRequest;
            }
        };
        multipartResolver.setMaxUploadSize(globalVariable.getSizeLimit());
        multipartResolver.setMaxUploadSizePerFile(globalVariable.getSizeLimit());
        return multipartResolver;
    }

    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                final long cacheSize = 1024 * 100;
                StandardRoot root = new StandardRoot(context);
                root.setCacheMaxSize(cacheSize);
                context.setResources(root);
            }
        };
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> {
            // ObjectId Serialization
            JsonSerializer<ObjectId> objIdSerializer = new JsonSerializer<ObjectId>() {
                @Override
                public void serialize(ObjectId value,
                                      JsonGenerator gen,
                                      SerializerProvider serializers) throws IOException {
                    gen.writeString(value == null ? null : value.toHexString());
                }
            };
            // Page<> Serialization
            JsonSerializer<Page> pageSerializer = new JsonSerializer<Page>() {
                @Override
                public void serialize(Page value,
                                      JsonGenerator gen,
                                      SerializerProvider serializers) throws IOException {
                    gen.writeStartObject();
                    gen.writeObjectField("content", value.getContent());
                    gen.writeNumberField("totalPages", value.getTotalPages());
                    gen.writeNumberField("numberOfElements", value.getTotalElements());
                    gen.writeEndObject();
                }
            };
            jacksonObjectMapperBuilder.serializerByType(ObjectId.class, objIdSerializer);
            jacksonObjectMapperBuilder.serializerByType(Page.class, pageSerializer);
        };
    }
}
