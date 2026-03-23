package com.ma_sante_assurance.upload;

import com.ma_sante_assurance.upload.controller.UploadController;
import com.ma_sante_assurance.upload.dto.UploadResponseDTO;
import com.ma_sante_assurance.upload.service.CloudinaryUploadService;
import com.ma_sante_assurance.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UploadController.class)
@AutoConfigureMockMvc(addFilters = false)
class UploadControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CloudinaryUploadService cloudinaryUploadService;

    @MockBean
    private JwtService jwtService;

    @Test
    void uploadPhotoShouldReturnPublicUrl() throws Exception {
        Mockito.when(cloudinaryUploadService.uploadImage(Mockito.any()))
                .thenReturn(new UploadResponseDTO("https://res.cloudinary.com/demo/image/upload/photo.jpg", "ma_sante/photo"));

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/api/uploads/photo").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.imageUrl").value("https://res.cloudinary.com/demo/image/upload/photo.jpg"));
    }
}
