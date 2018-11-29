package org.emerald.comicapi.controller;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.exception.EntityNotFoundException;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.model.data.Paper;
import org.emerald.comicapi.model.form.ChapterForm;
import org.emerald.comicapi.model.form.ChapterUpdate;
import org.emerald.comicapi.model.options.Options;
import org.emerald.comicapi.repository.ChapterRepository;
import org.emerald.comicapi.repository.ComicRepository;
import org.emerald.comicapi.repository.PaperRepository;
import org.emerald.comicapi.service.ComicService;
import org.emerald.comicapi.service.StorageService;
import org.emerald.comicapi.validator.sequence.ValidationSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@RestController
@RequestMapping("/api/chapters")
public class ChapterController {

    @Autowired
    private ComicService comicService;
    @Autowired
    private ComicRepository comicRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private GlobalVariable globalVariable;

    @GetMapping("")
    public Page<Chapter> getAll(@Validated Options options) {
        return chapterRepository.findWithComicInfo(options.getPageRequest());
    }
    @GetMapping("/{id}")
    public Chapter getById(
            @Validated Options options,
            @PathVariable String id)
    {
        return chapterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Chapter.class));
    }
    @GetMapping("/{id}/papers")
    public Page<Paper> getPapers(
            @Validated Options options,
            @PathVariable String id)
    {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Chapter.class));
        return paperRepository.findByChapterId(chapter.getId(), options.getPageRequest());
    }

    @PostMapping("")
    public ResponseEntity<?> upload(
        @Validated({ValidationSequence.class}) ChapterForm chapterForm,
        HttpServletRequest request) throws Exception
    {
        Comic comic = comicRepository.findById(chapterForm.getComicId()).orElseThrow(() -> new EntityNotFoundException(Comic.class));
        Chapter chapter = new Chapter(chapterForm);
        chapterRepository.insert(chapter);

        if (chapterForm.getPageListFile() != null) {
            File tmpZipFile = storageService.createTmpFile(chapterForm.getPageListFile(), null);
            comicService.storePapersFromZipFile(tmpZipFile, chapter);
            ResponseFormat format = new ResponseFormat(
                    202,
                    "Processing uploaded pages. It may takes a few minutes...",
                    chapter, request);
            return ResponseEntity.accepted().body(format);
        }
        else {
            ResponseFormat format = new ResponseFormat(200, chapter, request);
            return ResponseEntity.ok(format);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @Validated({ValidationSequence.class}) ChapterUpdate chapterUpdate,
        @PathVariable String id,
        BindingResult bindingResult,
        HttpServletRequest request)
    {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Chapter.class));
        chapter.update(chapterUpdate);

        chapterRepository.save(chapter);
        ResponseFormat format = new ResponseFormat(200, chapter, request);
        return ResponseEntity.ok(format);
    }

    @DeleteMapping("/{id}")
    public ResponseFormat delete(
            @PathVariable String id,
            HttpServletRequest request) throws Exception
    {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Chapter.class));

        storageService.deleteFolder(chapter.getLocalPath().toFile());
        Long total = paperRepository.deletePaperByChapterId(chapter.getId());
        chapterRepository.delete(chapter);
        return new ResponseFormat(200, "Total deleted pages: " + total, request);
    }
    @DeleteMapping("/{id}/papers")
    public ResponseEntity<?> deletePapers(
            @PathVariable String id,
            HttpServletRequest request) throws Exception
    {
        Chapter chapter = chapterRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Chapter.class));

        storageService.deleteFolder(chapter.getLocalPath().toFile());
        Long total = paperRepository.deletePaperByChapterId(chapter.getId());
        chapterRepository.delete(chapter);

        ResponseFormat format = new ResponseFormat(200, "Total deleted pages: ", request.getRequestURI());
        return ResponseEntity.ok(format);
    }
}