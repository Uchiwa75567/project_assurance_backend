package com.ma_sante_assurance.upload.controller;

import com.ma_sante_assurance.common.ApiResponse;
import com.ma_sante_assurance.upload.dto.UploadResponseDTO;
import com.ma_sante_assurance.upload.service.CloudinaryUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "Gestion des fichiers medias")
public class UploadController {

    private final CloudinaryUploadService cloudinaryUploadService;

    public UploadController(CloudinaryUploadService cloudinaryUploadService) {
        this.cloudinaryUploadService = cloudinaryUploadService;
    }

    @PostMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Uploader une photo", description = "Envoie une image vers Cloudinary et retourne son URL publique.")
    public ApiResponse<UploadResponseDTO> uploadPhoto(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok("Photo téléversée", cloudinaryUploadService.uploadImage(file));
    }
}
