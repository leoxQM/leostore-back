package com.appstore.backend.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface AlmacenamientoService {
    String subirArchivo(MultipartFile file, String rutaRelativa) throws IOException;
    void eliminarArchivo(String rutaRelativa);
}
