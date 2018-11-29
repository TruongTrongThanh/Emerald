package org.emerald.comicapi.controller;

import org.emerald.comicapi.config.GlobalVariable;
import org.emerald.comicapi.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private StorageService storageService;
    @Autowired
    private GlobalVariable globalVariable;

    @GetMapping
    public String home() {
        try {
            URL url = new URL("http", "localhost", 1000, "some/thing");
            return url.toString();
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @PutMapping
    public String testPut(@RequestParam(name = "file") MultipartFile file) {

        return "OK";
    }
}