package com.appstore.backend.service.serviceImpl;

import com.appstore.backend.service.AlmacenamientoService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Profile("dev")   // ← solo se activa cuando el perfil "dev" está activo
public class AlmacenamientoLocalService implements AlmacenamientoService {

    private static final String UPLOAD_DIR = "uploads/";

    @Override
    public String subirArchivo(MultipartFile file, String rutaRelativa) throws IOException {
        Path rutaDestino = Paths.get(UPLOAD_DIR + rutaRelativa);
        Files.createDirectories(rutaDestino.getParent());
        Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);
        return rutaRelativa;   // en dev, guardas solo la ruta relativa, como ya hacías
    }

    @Override
    public void eliminarArchivo(String rutaRelativa) {
        try {
            Path ruta = Paths.get(UPLOAD_DIR).resolve(rutaRelativa);
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar el archivo local: " + rutaRelativa);
        }
    }
}
