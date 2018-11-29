package org.emerald.comicapi.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.apache.tika.mime.MimeTypeException;
import org.emerald.comicapi.model.data.Chapter;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import net.lingala.zip4j.exception.ZipException;

public interface StorageService {
    File store(MultipartFile multipartFile, @Nullable String basename, Path storedPath) throws IOException, IllegalStateException, MimeTypeException;
    File createTmpFile(MultipartFile file, @Nullable String basename) throws IOException;
    Path createTmpFolder(@Nullable String folderName) throws IOException;
    File generateThumbnailImage(File image, int width, int height, Path storedPath) throws IOException;
    boolean extractArchiveFile(File archiveFile, Path extractPath) throws IllegalStateException, ZipException;
    Path toLocalPath(URL webPath);
    File toFile(URL link);
    Path prependRoot(Path other);
    URL toWebPath(File file) throws MalformedURLException;
    void delete(File file);
    void deleteFolder(File folder) throws IOException;
}