package org.emerald.comicapi.service;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.io.FilenameUtils;
import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.model.data.Chapter;
import org.emerald.comicapi.model.data.Comic;
import org.emerald.comicapi.model.data.Paper;
import org.emerald.comicapi.repository.ChapterRepository;
import org.emerald.comicapi.repository.ComicRepository;
import org.emerald.comicapi.repository.PaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
public class ComicServiceImpl implements ComicService {

    private final Logger logger = LoggerFactory.getLogger(ComicService.class);

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
    FileDetector fileDetector;

    @Override
    public void validate(Method method, BindingResult bindingResult) throws MethodArgumentNotValidException {
        if (bindingResult.hasErrors()) {
            MethodParameter parameter = new MethodParameter(method, 0);
            throw new MethodArgumentNotValidException(parameter, bindingResult);
        }
    }

    @Override
    public File[] storeCoverPage(MultipartFile file, Comic comic) throws Exception {
        File[] files = new File[2];

        Path storedPath = Paths.get(Comic.ROOT_LOCATION, comic.getIdHexString());
        files[0] = storageService.store(file, null, storedPath);
        files[1] = storageService.generateThumbnailImage(files[0], 400, -1, storedPath);
        comic.setCoverUrl(storageService.toWebPath(files[0]));
        comic.setThumbUrl(storageService.toWebPath(files[1]));

        comicRepository.save(comic);
        return files;
    }

    @Override
    public File[] storeChapterPage(MultipartFile file, Chapter chapter, Paper paper) throws Exception {
        File[] files = new File[2];

        Path storedPath = chapter.getLocalPath();
        files[0] = storageService.store(file, null, storedPath);
        files[1] = storageService.generateThumbnailImage(files[0], 400, -1, storedPath);
        paper.setImageUrl(storageService.toWebPath(files[0]));
        paper.setThumbUrl(storageService.toWebPath(files[1]));

        paperRepository.save(paper);
        return files;
    }

    @Override
    @Async("paperExecutor")
    public void storePapersFromZipFile(File zipFile, Chapter chapter) throws Exception {
        chapter.setProcessing(true);
        chapterRepository.save(chapter);

        Path extractFolder = storageService.createTmpFolder(null);
        boolean isNotEncrypted = storageService.extractArchiveFile(zipFile, extractFolder);
        if (isNotEncrypted) {
            Stream<Path> fileList = Files.walk(extractFolder);
            final AtomicInteger count = new AtomicInteger();
            fileList.forEach(processExtractFile(chapter, count));
            storageService.deleteFolder(extractFolder.toFile());
        }
        else
            chapter.addError("Uploaded zip file is encrypted");
        storageService.delete(zipFile);

        chapter.setProcessing(false);
        chapterRepository.save(chapter);
    }

    private Consumer<Path> processExtractFile(Chapter chapter, AtomicInteger count) {
        Path chapterFolderPath = storageService.prependRoot(chapter.getLocalPath().resolve("p"));
        final File chapterFolder = chapterFolderPath.toFile();
        chapterFolder.mkdirs();
        final File chapterThumbFolder = chapterFolderPath.resolve("..\\t").normalize().toFile();
        chapterThumbFolder.mkdirs();
        final RandomString randomStr = new RandomString(7);

        return path -> {
            File file = path.toFile();
            logger.info("Processing file: " + file.getName());
            try {
                if (file.isFile()) {
                    if (fileDetector.isImage(file)) {
                        String basename = FilenameUtils.getBaseName(file.getName());
                        String ext = FilenameUtils.getExtension(file.getName());
                        String filename = randomStr.nextString() + "." + ext;

                        File persistentFile = new File(chapterFolder, filename);
                        if (file.renameTo(persistentFile)) {
                            URL imageUrl = storageService.toWebPath(persistentFile);

                            File thumbFile = storageService.generateThumbnailImage(
                                    persistentFile, 300, -1, chapterThumbFolder.toPath());
                            URL thumbUrl = storageService.toWebPath(thumbFile);

                            Paper paper = new Paper(
                                    basename,
                                    chapter.getIdHexString(),
                                    imageUrl,
                                    thumbUrl);
                            paperRepository.insert(paper);
                            count.incrementAndGet();
                        }
                        else
                            logger.debug("Can't move file: " + file.getPath());
                    }
                    else
                        chapter.addError("Invalid Image: " + file.getName());
                }
            }
            catch (IOException ex) {
                logger.debug(ex.getMessage());
            }
        };
    }

//    @Override
//    public Page<Comic> findComicByBasicInfo(ComicOptions comicOptions) {
//        Comic comic = new Comic(comicOptions);
//        Pageable pageRequest = comicOptions.getPageRequest();
//        return comicRepository.findByBasicInfo(comic, pageRequest);
//    }
//    @Override
//    public Page<Comic> findComicByTextIndex(String term, Options options) {
//        Pageable pageRequest = options.getPageRequest();
//        return comicRepository.findByTextIndex(term, pageRequest);
//    }
//    @Override
//    public Comic findComicById(String id) {
//        return comicRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Comic.class));
//    }
//    @Override
//    public Comic addComic(ComicForm comicForm) throws Exception {
//        Comic comic = new Comic(comicForm);
//        comicRepository.insert(comic);
//
//        Path storedPath = Paths.get(Comic.ROOT_LOCATION, comic.getIdHexString());
//        File coverFile = storageService.store(comicForm.getCoverFile(), null, storedPath);
//        File thumbFile = storageService.generateThumbnailImage(coverFile, 400, -1, storedPath);
//        comic.setCoverUrl(storageService.toWebPath(coverFile));
//        comic.setThumbUrl(storageService.toWebPath(thumbFile));
//
//        comicRepository.save(comic);
//        return comic;
//    }
//

//    @Override
//    public Comic addComic(ComicForm comicForm) throws Exception {
//        Comic comic = new Comic(comicForm);
//        comicRepository.insert(comic);
//
//        String relativePaths = globalVariable.buildPath(false, "imgs/mgs", comic.getId().toHexString());
//        File storedCoverFile = storageService.store(comicForm.getCoverFile(), null, globalVariable.STORE_PATH,
//                                                                                    relativePaths);
//        File storedThumbFile = storageService.generateThumbnailImage(storedCoverFile, -1, 400, globalVariable.STORE_PATH,
//                                                                                              relativePaths);
//
//        comic.setCoverUrl(globalVariable.getServerURL(true, relativePaths, storedCoverFile.getName()));
//        comic.setThumbUrl(globalVariable.getServerURL(true, relativePaths, storedThumbFile.getName()));
//        comicRepository.save(comic);
//
//        return comic;
//    }
//    @Override
//    public Comic updateComic(String comicId, ComicUpdate comicUpdate) throws Exception {
//        Optional<Comic> optional = comicRepository.findById(comicId);
//        Comic oldComic = optional.orElseThrow(() -> new EntityNotFoundException(Comic.class));
//        oldComic.clone(comicUpdate);
//
//        if (comicUpdate.getCoverFile() != null) {
//            File oldCoverFile = globalVariable.transformURLToFile(oldComic.getCoverUrl());
//            File oldThumbFile = globalVariable.transformURLToFile(oldComic.getThumbUrl());
//            storageService.delete(oldCoverFile);
//            storageService.delete(oldThumbFile);
//
//            String relativePaths = globalVariable.buildPath(false, "imgs/mgs", oldComic.getId().toHexString());
//            File storedCoverFile = storageService.store(comicUpdate.getCoverFile(), null, globalVariable.STORE_PATH,
//                    relativePaths);
//            File storedThumbFile = storageService.generateThumbnailImage(storedCoverFile, -1, 400, globalVariable.STORE_PATH,
//                    relativePaths);
//            oldComic.setCoverUrl(globalVariable.getServerURL(true, relativePaths, storedCoverFile.getName()));
//            oldComic.setThumbUrl(globalVariable.getServerURL(true, relativePaths, storedThumbFile.getName()));
//        }
//        return comicRepository.save(oldComic);
//    }
//    @Override
//    public void deleteComic(String comicId) throws Exception {
//        Optional<Comic> optional = comicRepository.findById(comicId);
//        Comic comic = optional.orElseThrow(() -> new EntityNotFoundException(Comic.class));
//
//        logger.info("Starting delete operation: " + comic.getName());
//        File directoryPath = new File(globalVariable.getStorePath(true, "imgs/mgs", comic.getId().toHexString()));
//
//        List<Chapter> list = chapterRepository.findByComicId(comic.getId());
//        for (Chapter chapter : list) {
//            logger.info("Delete papers in chapter " + chapter.getName());
//            Long number = paperRepository.deletePaperByChapterId(chapter.getId());
//            logger.info("Total deleted page: " + number);
//        }
//        logger.info("Delete all chapters from " + comic.getName());
//        chapterRepository.deleteChapterByComicId(comic.getId());
//        logger.info("Delete folder resource: " + directoryPath);
//        FileUtils.deleteDirectory(directoryPath);
//        comicRepository.delete(comic);
//        logger.info("delete operation has finished.");
//    }
//    @Override
//    public void deleteAllComics() throws Exception {
//        if (comicRepository.count() > 0) {
//            paperRepository.deleteAll();
//            chapterRepository.deleteAll();
//            comicRepository.deleteAll();
//            storageService.deleteFolder(new File(globalVariable.getStorePath(true, Comic.ROOT_LOCATION)));
//        }
//        else
//            throw new EntityNotFoundException(Comic.class);
//    }
//
//    @Override
//    public Page<Chapter> findChapterWithComicInfo(Options query) {
//        return chapterRepository.findWithComicInfo(query.getPageRequest());
//    }
//    @Override
//    public Optional<Chapter> findChapterById(String id) {
//        return chapterRepository.findById(id);
//    }
//    @Override
//    public Page<Chapter> findChapterByComicId(String comicId, Options option) {
//        Optional<Comic> optional = comicRepository.findById(comicId);
//        if (optional.isPresent()) {
//            return chapterRepository.findByComicId(new ObjectId(comicId), option.getPageRequest());
//        }
//        else
//            throw new EntityNotFoundException(Comic.class);
//    }
//    @Override
//    public Chapter addChapter(ChapterForm chapterForm) throws Exception {
//        Optional<Comic> optional = comicRepository.findById(chapterForm.getComicId());
//        if (optional.isPresent()) {
//            Chapter chapter = new Chapter(chapterForm);
//            if (chapterForm.getPageListFile() != null) {
//                chapter.setState(Chapter.State.PROCESSING);
//                chapterRepository.insert(chapter);
//
//                String relativePaths = globalVariable.buildPath(false, "imgs/mgs",
//                                                                chapter.getComicId().toHexString(),
//                                                                chapter.getId().toHexString());
//                File archiveFile = storageService.store(chapterForm.getPageListFile(), null, globalVariable.PROCESS_PATH,
//                                                                                            relativePaths);
//                storageService.processArchiveFile(archiveFile, chapter, globalVariable.PROCESS_PATH,
//                                                                        relativePaths);
//            }
//            else {
//                chapter.setState(Chapter.State.EMPTY);
//                chapterRepository.insert(chapter);
//            }
//            return chapter;
//        }
//        throw new EntityNotFoundException(Comic.class);
//    }
//    @Override
//    public Chapter updateChapter(String chapterId, ChapterUpdate chapterUpdate) throws Exception {
//        Optional<Chapter> optional = chapterRepository.findById(chapterId);
//        if (optional.isPresent()) {
//            Chapter oldChapter = optional.get();
//            oldChapter.clone(chapterUpdate);
//            if (chapterUpdate.getPageListFile() != null) {
//                oldChapter.setState(Chapter.State.PROCESSING);
//                chapterRepository.save(oldChapter);
//                paperRepository.deletePaperByChapterId(oldChapter.getId());
//
//                String relativePaths = globalVariable.buildPath(false, "imgs/mgs",
//                                                                oldChapter.getComicId().toHexString(),
//                                                                oldChapter.getId().toHexString());
//                File chapterFolder = new File(globalVariable.getStorePath(true, relativePaths));
//                storageService.deleteFolder(chapterFolder);
//
//                File archiveFile = storageService.store(chapterUpdate.getPageListFile(), null, globalVariable.PROCESS_PATH,
//                                                                                            relativePaths);
//                storageService.processArchiveFile(archiveFile, oldChapter, globalVariable.PROCESS_PATH,
//                                                                           relativePaths);
//            }
//            else {
//                chapterRepository.save(oldChapter);
//            }
//            return oldChapter;
//        }
//        throw new EntityNotFoundException(Chapter.class);
//    }
//    @Override
//    public void deleteChapter(String chapterId) throws Exception {
//        Optional<Chapter> optional = chapterRepository.findById(chapterId);
//        if (optional.isPresent()) {
//            Chapter chapter = optional.get();
//            paperRepository.deletePaperByChapterId(new ObjectId(chapter.getId().toHexString()));
//            chapterRepository.delete(chapter);
//            File folder = new File(globalVariable.getStorePath(true, "imgs/mgs",
//                                                                                  chapter.getComicId().toHexString(),
//                                                                                  chapter.getId().toHexString()));
//            storageService.deleteFolder(folder);
//        }
//        else
//            throw new EntityNotFoundException(Chapter.class);
//    }
//    @Override
//    public void deleteChapterByComicId(String comicId) throws Exception {
//        Optional<Comic> optional = comicRepository.findById(comicId);
//        if (optional.isPresent()) {
//            List<Chapter> list = chapterRepository.findByComicId(new ObjectId(comicId));
//            if (list.isEmpty())
//                throw new EntityNotFoundException(Chapter.class);
//
//            for (Chapter chapter : list) {
//                paperRepository.deletePaperByChapterId(chapter.getId());
//                File folder = new File(globalVariable.getStorePath(true, "imgs/mgs",
//                        chapter.getComicId().toHexString(),
//                        chapter.getId().toHexString()));
//                storageService.deleteFolder(folder);
//            }
//            chapterRepository.deleteChapterByComicId(new ObjectId(comicId));
//        }
//        else
//            throw new EntityNotFoundException(Comic.class);
//    }
//
//    @Override
//    public Page<Paper> findPaperByChapterId(String chapterId, Options options) {
//        Optional<Chapter> optional = chapterRepository.findById(chapterId);
//        if (optional.isPresent()) {
//            return paperRepository.findByChapterId(new ObjectId(chapterId), options.getPageRequest());
//        }
//        else
//            throw new EntityNotFoundException(Chapter.class);
//    }
//    @Override
//    public Paper addPaper(PaperForm paperForm) throws Exception {
//        Optional<Chapter> optional = chapterRepository.findById(paperForm.getChapterId());
//        if (optional.isPresent()) {
//            Chapter chapter = optional.get();
//            Paper paper = new Paper(paperForm);
//            paperRepository.insert(paper);
//
//            String relativePaths = globalVariable.buildPath(false, globalVariable.STORE_PATH, "imgs/mgs", chapter.getComicId().toHexString(), chapter.getId().toHexString());
//            File storedFile = storageService.store(paperForm.getImageFile(), paper.getName(), relativePaths, "p");
//            File thumbFile = storageService.generateThumbnailImage(storedFile, -1, 300, relativePaths, "t");
//
//            paper.setImageUrl(globalVariable.getServerURL(true, relativePaths, "p", storedFile.getName()));
//            paper.setThumbUrl(globalVariable.getServerURL(true, relativePaths, "t", thumbFile.getName()));
//            return paperRepository.save(paper);
//        }
//        throw new EntityNotFoundException(Chapter.class);
//    }
//    @Override
//    public Paper updatePaper(String paperId, PaperUpdate paperUpdate) throws Exception {
//        Optional<Paper> optional = paperRepository.findById(paperId);
//        if (optional.isPresent()) {
//            Paper oldPaper = optional.get();
//            oldPaper.clone(paperUpdate);
//
//            if (paperUpdate.getImageFile() != null) {
//                String relativePaths = globalVariable.getUrlParentFilePath(oldPaper.getImageUrl(), 2);
//
//                File oldStoredFile = globalVariable.transformURLToFile(oldPaper.getImageUrl());
//                File oldThumbFile = globalVariable.transformURLToFile(oldPaper.getThumbUrl());
//                storageService.delete(oldStoredFile);
//                storageService.delete(oldThumbFile);
//
//                File storedFile = storageService.store(paperUpdate.getImageFile(), null, relativePaths, "p");
//                File thumbFile = storageService.generateThumbnailImage(storedFile, -1, 300, relativePaths, "t");
//                oldPaper.setImageUrl(globalVariable.getServerURL(true, relativePaths, "p", storedFile.getName()));
//                oldPaper.setThumbUrl(globalVariable.getServerURL(true, relativePaths, "t", thumbFile.getName()));
//            }
//            return paperRepository.save(oldPaper);
//        }
//        throw new EntityNotFoundException(Paper.class);
//    }
//
//    @Override
//    public void deletePaper(String paperId) throws Exception {
//        Optional<Paper> optional = paperRepository.findById(paperId);
//        if (optional.isPresent()) {
//            Paper paper = optional.get();
//            File storedFile = globalVariable.transformURLToFile(paper.getImageUrl());
//            File thumbFile = globalVariable.transformURLToFile(paper.getThumbUrl());
//            storageService.delete(storedFile);
//            storageService.delete(thumbFile);
//
//            paperRepository.delete(paper);
//        }
//        else
//            throw new EntityNotFoundException(Paper.class);
//    }
//
//    @Override
//    public void deletePaperByChapterId(String chapterId) throws Exception {
//        Optional<Chapter> optional = chapterRepository.findById(chapterId);
//        if (optional.isPresent()) {
//            Chapter chapter = optional.get();
//            paperRepository.deletePaperByChapterId(new ObjectId(chapterId));
//            File folder = new File(globalVariable.getStorePath(true, chapter.getResourceLocation()));
//            storageService.deleteFolder(folder);
//        }
//        else
//            throw new EntityNotFoundException(Chapter.class);
//    }
}