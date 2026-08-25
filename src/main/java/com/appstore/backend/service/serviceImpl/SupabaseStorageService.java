package com.appstore.backend.service.serviceImpl;

import com.appstore.backend.service.AlmacenamientoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Profile("prod")
public class SupabaseStorageService implements AlmacenamientoService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service.key}")
    private String supabaseServiceKey;

    @Value("${supabase.bucket}")
    private String bucket;

    private final RestClient restClient = RestClient.create();

    @Override
    public String subirArchivo(MultipartFile file, String rutaRelativa) throws IOException {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + rutaRelativa;

        restClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseServiceKey)
            .header("apikey", supabaseServiceKey)
            .contentType(MediaType.parseMediaType(file.getContentType()))
            .body(file.getBytes())
            .retrieve()
            .toBodilessEntity();

        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + rutaRelativa;
    }

    @Override
    public void eliminarArchivo(String rutaRelativa) {
        String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + rutaRelativa;

        restClient.method(HttpMethod.DELETE)
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseServiceKey)
            .header("apikey", supabaseServiceKey)
            .retrieve()
            .toBodilessEntity();
    }
}