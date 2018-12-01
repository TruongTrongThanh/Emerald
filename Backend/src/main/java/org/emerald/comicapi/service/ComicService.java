package org.emerald.comicapi.service;

import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.model.data.Paper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Method;

public interface ComicService {
    void validate(Method method, BindingResult bindingResult) throws MethodArgumentNotValidException;
    File[] storeCoverPage(MultipartFile file, Comic comic) throws Exception;
    File[] storeChapterPage(MultipartFile file, Chapter chapter, Paper paper) throws Exception;
    void storePapersFromZipFile(File zipFile, Chapter chapter) throws Exception;
}