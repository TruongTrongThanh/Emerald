package org.emerald.comicapi.controller;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.exception.EntityNotFoundException;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.model.data.Paper;
import org.emerald.comicapi.model.form.PaperForm;
import org.emerald.comicapi.model.form.PaperUpdate;
import org.emerald.comicapi.repository.ChapterRepository;
import org.emerald.comicapi.repository.PaperRepository;
import org.emerald.comicapi.service.ComicService;
import org.emerald.comicapi.service.StorageService;
import org.emerald.comicapi.validator.sequence.ValidationSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

@RestController
@RequestMapping("/api/papers")
public class PaperController {

    @Autowired
    ComicService comicService;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    StorageService storageService;

    @PostMapping("")
    public ResponseEntity<?> upload(
            @Validated({ValidationSequence.class}) PaperForm paperForm,
            HttpServletRequest request) throws Exception
    {
        Chapter chapter = chapterRepository.findById(paperForm.getChapterId()).orElseThrow(() -> new EntityNotFoundException(Chapter.class));
        Paper paper = new Paper(paperForm);
        comicService.storeChapterPage(paperForm.getImageFile(), chapter, paper);

        ResponseFormat format = new ResponseFormat(201, paper, request);
        return ResponseEntity.status(201).body(format);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @Validated({ValidationSequence.class}) PaperUpdate paperUpdate,
            @PathVariable String id,
            HttpServletRequest request) throws Exception
    {
        Chapter chapter = chapterRepository.findById(paperUpdate.getChapterId()).orElseThrow(() -> new EntityNotFoundException(Chapter.class));
        Paper paper = paperRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Paper.class));

        paper.update(paperUpdate);
        if (paperUpdate.getZipFile() != null) {
            File tmpZipFile = storageService.createTmpFile(paperUpdate.getZipFile(), null);
            comicService.storePapersFromZipFile(tmpZipFile, chapter);

            ResponseFormat format = new ResponseFormat(202, paper, request);
            return ResponseEntity.accepted().body(format);
        }
        else if (paperUpdate.getImageFile() != null) {
            comicService.storeChapterPage(paperUpdate.getImageFile(), chapter, paper);
        }
        ResponseFormat format = new ResponseFormat(200, paper, request);
        return ResponseEntity.ok(format);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable String id,
            HttpServletRequest request)
    {
        Paper paper = paperRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Paper.class));
        Path imgLocalPath = storageService.toLocalPath(paper.getImageUrl());
        Path thumbLocalPath = storageService.toLocalPath(paper.getThumbUrl());
        storageService.delete(imgLocalPath.toFile());
        storageService.delete(thumbLocalPath.toFile());
        paperRepository.delete(paper);

        ResponseFormat format = new ResponseFormat(200, "Delete success!", request);
        return ResponseEntity.ok(format);
    }
}
