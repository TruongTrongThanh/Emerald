package org.emerald.comicapi.controller;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.exception.EntityNotFoundException;
import org.emerald.comicapi.model.common.ResponseFormat;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.model.form.ComicForm;
import org.emerald.comicapi.model.form.ComicUpdate;
import org.emerald.comicapi.model.options.ComicOptions;
import org.emerald.comicapi.model.options.Options;
import org.emerald.comicapi.repository.ChapterRepository;
import org.emerald.comicapi.repository.ComicRepository;
import org.emerald.comicapi.repository.PaperRepository;
import org.emerald.comicapi.service.ComicService;
import org.emerald.comicapi.service.StorageService;
import org.emerald.comicapi.validator.sequence.ValidationSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comics")
public class ComicController {
    private final Logger logger = LoggerFactory.getLogger(ComicController.class);

    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    ComicRepository comicRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    StorageService storageService;
    @Autowired
    ComicService comicService;

    @GetMapping("")
    public Page<Comic> getAll(
        @Validated ComicOptions comicOptions)
    {
        Comic probe = new Comic(comicOptions);
        Pageable pageRequest = comicOptions.getPageRequest();
        return comicRepository.findByBasicInfo(probe, pageRequest);
    }

    @GetMapping("/text-index")
    public Page<Comic> getAllWithTextIndex(
        @Validated Options options,
        @RequestParam(name = "term") String term)
    {
        Pageable pageRequest = options.getPageRequest();
        return comicRepository.findByTextIndex(term, pageRequest);
    }

    @GetMapping("/{id}")
    public Comic getById(@PathVariable String id) {
        return comicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Comic.class));
    }

    @GetMapping("/{id}/chapters")
    public Page<Chapter> getChapters(
            @Validated Options options,
            @PathVariable String id)
    {
        Comic comic = comicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Comic.class));
        return chapterRepository.findByComicIdWithTotalPages(comic.getId(), options.getPageRequest());
    }

    @PostMapping("")
    public ResponseEntity<?> upload(
        @Validated({ValidationSequence.class}) ComicForm comicForm,
        HttpServletRequest request) throws Exception
    {
        Comic comic = new Comic(comicForm);
        comicRepository.insert(comic);
        comicService.storeCoverPage(comicForm.getCoverFile(), comic);

        ResponseFormat format = new ResponseFormat(200, comic, request);
        return ResponseEntity.ok(format);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
        @Validated({ValidationSequence.class}) ComicUpdate comicUpdate,
        @PathVariable String id,
        HttpServletRequest request) throws Exception
    {
        Optional<Comic> optional = comicRepository.findById(id);
        Comic comic = optional.orElseThrow(() -> new EntityNotFoundException(Comic.class));
        comic.update(comicUpdate);

        if (comicUpdate.getCoverFile() != null) {
            File oldCoverFile = storageService.toFile(comic.getCoverUrl());
            File oldThumbFile = storageService.toFile(comic.getThumbUrl());
            storageService.delete(oldCoverFile);
            storageService.delete(oldThumbFile);
            comicService.storeCoverPage(comicUpdate.getCoverFile(), comic);
        }
        comicRepository.save(comic);
        ResponseFormat format = new ResponseFormat(200, comic, request);
        return ResponseEntity.ok(format);
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteAll() throws Exception {
        if (comicRepository.count() > 0) {
            paperRepository.deleteAll();
            chapterRepository.deleteAll();
            comicRepository.deleteAll();
            storageService.deleteFolder(new File(Comic.ROOT_LOCATION));
            return ResponseEntity.ok("Delete successfully.");
        }
        return ResponseEntity.badRequest().body("Nothing to delete");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
        @PathVariable String id,
        HttpServletRequest request) throws Exception
    {
        Optional<Comic> optional = comicRepository.findById(id);
        Comic comic = optional.orElseThrow(() -> new EntityNotFoundException(Comic.class));

        List<Chapter> list = chapterRepository.findByComicId(comic.getId());
        for (Chapter chapter : list) {
            paperRepository.deletePaperByChapterId(chapter.getId());
        }
        chapterRepository.deleteChapterByComicId(comic.getId());

        File directoryPath = comic.getDirectory();
        storageService.deleteFolder(directoryPath);
        comicRepository.delete(comic);

        ResponseFormat format = new ResponseFormat(200, "Delete success!", request);
        return ResponseEntity.ok(format);
    }
    @DeleteMapping("/{id}/chapters")
    public ResponseFormat deleteChapters(
            @PathVariable String comicId,
            HttpServletRequest request) throws Exception
    {
        Comic comic = comicRepository.findById(comicId).orElseThrow(() -> new EntityNotFoundException(Comic.class));

        List<Chapter> list = chapterRepository.findByComicId(comic.getId());
        for (Chapter chapter : list) {
            paperRepository.deletePaperByChapterId(chapter.getId());
            Path directoryPath = comic.getDirectory().toPath().resolve(chapter.getIdHexString());
            storageService.deleteFolder(directoryPath.toFile());
        }
        chapterRepository.deleteChapterByComicId(comic.getId());

        return new ResponseFormat(200, "Delete success!", request);
    }
}