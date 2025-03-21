package tn.exemple.medicare.fileServer;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadImpl  {

        private final Cloudinary cloudinary;

        public String uploadImage(MultipartFile file) {
            try {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                return (String) uploadResult.get("url");
            } catch (IOException e) {
                throw new RuntimeException("Erreur de téléchargement", e);
            }
        }


}
