package org.emerald.comicapi.service;

import net.bytebuddy.utility.RandomString;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.repository.ChapterRepository;
import org.emerald.comicapi.repository.PaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImpl implements StorageService {
    private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    @Autowired
    private GlobalVariable globalVariable;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private FileDetector fileDetector;

    private static final String GENERATED_IMAGE_TYPE = "jpg";

    private Path getRoot() {
        return Paths.get(globalVariable.STORE_PATH).toAbsolutePath();
    }

    @Override
    public Path prependRoot(Path other) {
        if (!other.isAbsolute()) {
            Path root = getRoot();
            return root.resolve(other);
        }
        return other;
    }

    @Override
    public URL toWebPath(File file) throws MalformedURLException {
        File absFile = file;
        if (!file.isAbsolute()) {
            absFile = file.getAbsoluteFile();
        }
        if (absFile.isFile()) {
            String root = getRoot().toString();

            String relativePath = absFile.getPath()
                .replace(getRoot().toString(), "")
                .replace("\\", "/");

            return new URL(
                "http",
                globalVariable.SERVER_HOSTNAME,
                globalVariable.SERVER_PORT,
                relativePath
            );
        }
        throw new RuntimeException("Invalid file");
    }
    @Override
    public Path toLocalPath(URL webPath) {
        return prependRoot(Paths.get(webPath.getFile()));
    }

    @Override
    public File toFile(URL link) {
        return new File(toLocalPath(link).toString());
    }

    @Override
    public File store(MultipartFile multipartFile, @Nullable String basename, Path storedPath)
            throws IOException, IllegalStateException {
        String filename;
        if (basename == null)
            filename = new RandomString(7).nextString();
        else
            filename = basename;

        // Set extension
        filename += fileDetector.detectExtension(multipartFile);

        // Create folder
        Path absStoredPath = prependRoot(storedPath);
        new File(absStoredPath.toString()).mkdirs();

        // Store file
        File storeFile = new File(absStoredPath + "\\" + filename);
        multipartFile.transferTo(storeFile);

        return storeFile;
    }

    @Override
    public File createTmpFile(MultipartFile file, @Nullable String basename) throws IOException {
        String prefix;
        if (basename == null)
            prefix = new RandomString(5).nextString();
        else
            prefix = basename;
        File tmpFile = File.createTempFile(prefix, null, null);
        file.transferTo(tmpFile);
        return tmpFile;
    }

    @Override
    public Path createTmpFolder(@Nullable String folderName) throws IOException {
        String prefix;
        if (folderName == null)
            prefix = "COMIC-";
        else
            prefix = folderName;
        return Files.createTempDirectory(prefix);
    }

    @Override
    public File generateThumbnailImage(File image, int width, int height, Path storedPath) throws IOException {
        Image originalImage = ImageIO.read(image);
        Image resizeImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage buffer = new BufferedImage(
                resizeImage.getWidth(null),
                resizeImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        buffer.getGraphics().drawImage(resizeImage, 0, 0, null);
        buffer.getGraphics().dispose();

        // Store thumbnail image at path/to/image/t_imageName.png
        String thumbnailFileName = FilenameUtils.getBaseName(image.getName()) + "." + GENERATED_IMAGE_TYPE;
        File storedThumbnailFile = prependRoot(storedPath).resolve("t_" + thumbnailFileName).toFile();
        ImageIO.write(buffer, GENERATED_IMAGE_TYPE, storedThumbnailFile);

        return storedThumbnailFile;
    }

    @Override
    public boolean extractArchiveFile(File archiveFile, Path extractPath) throws IllegalStateException, ZipException {
        ZipFile zipFile = new ZipFile(archiveFile.getAbsolutePath());

        if (zipFile.isEncrypted()) {
            return false;
        }
        String path = extractPath.toAbsolutePath().toString();
        logger.debug("Extracting files at: " + path);
        zipFile.extractAll(path);
        logger.debug("Extracting files have done.");

        return true;
    }

//
//    @Override
//    public Stream<Path> extractArchiveFile(File archiveFile, String... directories)
//            throws IOException, IllegalStateException, ZipException {
//        String extractPath = globalVariable.buildPath(true, directories);
//        extractPath += "\\extract";
//
//        ZipFile zipFile = new ZipFile(archiveFile.getAbsolutePath());
//
//        if (zipFile.isEncrypted()) {
//
//            throw new RuntimeException("Zip file is encrypted.");
//        }
//
//        logger.info("Start to extract files...");
//        zipFile.extractAll(extractPath);
//        logger.info("Extract files have done.");
//        if (!archiveFile.delete()) {
//            throw new RuntimeException("Can't delete archive file");
//        }
//
//        return Files.walk(Paths.get(extractPath));
//    }
//    @Override
//    public List<String> storeMassPagesFromFolder(Stream<Path> imagePathListStream, Chapter chapter)
//            throws Exception {
//
//        AtomicInteger count = new AtomicInteger(); // Keep track number of valid pages
//        List<String> errorList = new LinkedList<>(); // Keep track error file list
//
//        String relativePathToChapterFolder = globalVariable.buildPath(false,
//                                                                      "imgs/mgs",
//                                                                      chapter.getComicId().toHexString(),
//                                                                      chapter.getId().toHexString());
//        String relativePathToStoreFolder = relativePathToChapterFolder + "/p";
//        String relativePathToStoreThumbnailFolder = relativePathToChapterFolder + "/t";
//
//        // Create 2 folder: imgs/mgs/comicId/chapterId/p and imgs/mgs/comicId/chapterId/t
//        logger.info("Create 2 folder");
//        new File(globalVariable.getStorePath(true, relativePathToStoreFolder)).mkdirs();
//        new File(globalVariable.getStorePath(true, relativePathToStoreThumbnailFolder)).mkdirs();
//
//        // Go through path list
//        imagePathListStream.forEach(path -> {
//            // Create File Instance for current path
//            File file = new File(path.toAbsolutePath().toString());
//            logger.info("Process file: " + file.getAbsolutePath());
//
//            // Check if it's a file
//            if (file.isFile()) {
//                String contentType;
//                String fileName = path.getFileName().toString();
//
//                // Validate file
//                logger.info("Validate process file");
//                try { contentType = validator.detect(file); }
//                catch (IOException ex) {
//                    ex.printStackTrace();
//                    throw new RuntimeException("Can't access file to detect type.");
//                }
//                if (globalVariable.getAcceptImageTypes().contains(contentType)) {
//                    // Increase count to keep track the number
//                    logger.info(Integer.toString(count.getAndIncrement()));
//
//                    // Separate basename and extension from original filename
//                    String basename = FilenameUtils.getBaseName(fileName);
//                    String extension;
//                    try {
//                        MimeType mimeType = MimeTypes.getDefaultMimeTypes().forName(contentType);
//                        extension = mimeType.getExtension();
//                    }
//                    catch (MimeTypeException ex) {
//                        ex.printStackTrace();
//                        throw new RuntimeException("Can't cast MIME type to extension.");
//                    }
//
//                    // Create Page Instance with name as basename of original filename
//                    Paper page = new Paper(basename, chapter.getId().toHexString());
//                    paperRepository.insert(page);
//
//                    /* Create random name for image file and join it with
//                        extension of original filename */
//                    String randomBasename = new RandomString(7).nextString();
//                    String generatedFileName = randomBasename + extension;
//                    String generatedThumbFileName = "t_" + randomBasename + "." + GENERATED_IMAGE_TYPE;
//
//                    // Relative path to stored file
//                    String relativePathToFile = globalVariable.buildPath(false,
//                                                                         relativePathToStoreFolder,
//                                                                         generatedFileName);
//                    // Relative path to stored thumbnail file
//                    String relativePathToThumbnailFile = globalVariable.buildPath(false,
//                                                                                 relativePathToStoreThumbnailFolder,
//                                                                                 generatedThumbFileName);
//                    // Move file from process path to store path
//                    logger.info("Move file");
//                    File renamedFile = new File(globalVariable.getStorePath(true, relativePathToFile));
//                    if (!file.renameTo(renamedFile))
//                        throw new RuntimeException("Something's wrong in StoreMassPages method of LocalStorage");
//
//                    // Generate thumbnail image
//                    logger.info("Generate thumbnail image");
//                    try {
//                        this.generateThumbnailImage(renamedFile, 300, -1, globalVariable.STORE_PATH,
//                                                                         relativePathToStoreThumbnailFolder);
//                    }
//                    catch (IOException ex) {
//                        throw new RuntimeException("Something's wrong in StoreMassPages method of LocalStorage");
//                    }
//
//                    // Set image URL and thumbnail image URL for Page
//                    logger.info("Set image URL and thumbnail image URL");
//                    String url = globalVariable.getServerURL(true, relativePathToFile);
//                    String thumbnailUrl = globalVariable.getServerURL(true, relativePathToThumbnailFile);
//                    page.setImageUrl(url);
//                    page.setThumbUrl(thumbnailUrl);
//                    paperRepository.save(page);
//
//                    logger.info("Result: GOOD");
//                }
//                else {
//                    errorList.add("Error File: " + fileName);
//                    logger.info("Result: BAD");
//                }
//            }
//        });
//        FileUtils.deleteDirectory(new File(globalVariable.getProcessPath(true, relativePathToChapterFolder)));
//
//        if (count.get() == 0) errorList.add("No image files can be found.");
//        return errorList;
//    }
//    @Override
//    @Async("requestExecutor")
//    public void processArchiveFile(File archiveFile, Chapter chapter, String... relativePaths) throws Exception {
//        logger.info("Starting to process archive file...");
//
//        Stream<Path> imagePathListStream = this.extractArchiveFile(archiveFile, relativePaths);
//        List<String> errorList = this.storeMassPagesFromFolder(imagePathListStream, chapter);
//
//        if (!errorList.isEmpty()) {
//            chapter.setState(Chapter.State.ERROR);
//            chapter.setErrors(errorList);
//            logger.error("invalid file list: ");
//            logger.error(errorList.toString());
//        }
//        else {
//            chapter.setState(Chapter.State.READY);
//        }
//        logger.info("Processing archive file have done.");
//        chapterRepository.save(chapter);
//    }

    @Override
    public void delete(File file) {
        if (!file.delete()) {
            throw new RuntimeException("Can't delete file: " + file.getAbsolutePath());
        }
    }
    @Override
    public void deleteFolder(File folder) throws IOException {
        File absFolder = new File(prependRoot(folder.toPath()).toString());

        if (absFolder.isDirectory())
            FileUtils.deleteDirectory(absFolder);
        else
            throw new RuntimeException("Input is not a folder");
    }
}