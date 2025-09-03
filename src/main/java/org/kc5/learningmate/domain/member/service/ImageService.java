package org.kc5.learningmate.domain.member.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.kc5.learningmate.common.exception.CommonException;
import org.kc5.learningmate.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ImageService {
    @Value("${image.max-profile-img-size}")
    long maxProfileImgSize;
    @Value("${image.upload-dir}")
    private String uploadDir;
    @Value("${image.image-prefix}")
    private String imagePrefix;
    @Value("${image.allowed-extensions}")
    private List<String> allowedExtensions;
    private Path uploadPath;

    @PostConstruct
    public void init() {
        this.uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            log.error("디렉토리를 생성할 수 없습니다: {}", e.getMessage());
            throw new IllegalStateException("필수 업로드 디렉토리를 생성할 수 없습니다: " + uploadDir, e);
        }
    }

    public String saveImage(InputStream inputStream, String originalFilename, String oldImgUrl) {
        log.info("saveImage 메서드 호출!!!");
        String filename = Paths.get(originalFilename)
                               .getFileName()
                               .toString();

        String extension = extractExtension(filename);
        validateFileExtension(extension);

        String newFilename = UUID.randomUUID() + "." + extension;

        Path path = uploadPath.resolve(newFilename);

        log.info("저장할 이미지의 파일 경로: {}", path);

        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

            if (oldImgUrl != null && !oldImgUrl.isEmpty()) {
                deleteOldImage(oldImgUrl);
            }

            return imagePrefix + newFilename;
        } catch (IOException e) {
            throw new CommonException(ErrorCode.SAVE_IMAGE_FAIL);
        }
    }

    public Resource getImage(String imgUrl) {
        if (imgUrl == null || !imgUrl.startsWith(imagePrefix)) {
            log.error("이미지 로드를 실패했습니다.");
            throw new CommonException(ErrorCode.LOAD_IMAGE_FAIL);
        }


        Path path = getFilepathFromUrl(imgUrl);
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new CommonException(ErrorCode.LOAD_IMAGE_FAIL);
        } catch (MalformedURLException e) {
            log.error("이미지 리소스 생성 실패.");
            throw new CommonException(ErrorCode.LOAD_IMAGE_FAIL);
        }

    }

    public void validateProfileImgSize(MultipartFile file) {
        if (file.getSize() > maxProfileImgSize) {
            throw new CommonException(ErrorCode.PROFILE_IMG_TOO_BIG);
        }
    }

    private void deleteOldImage(String imgUrl) {
        log.info("deleteOldImage 호출!!!");
        if (imgUrl == null || !imgUrl.startsWith(imagePrefix)) {
            return;
        }

        Path path = getFilepathFromUrl(imgUrl);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("프로필 이미지 제거 에러: {}", e.getMessage());
        }

    }

    private Path getFilepathFromUrl(String imgUrl) {
        String fileNameWithPotentialPath = imgUrl.substring(imagePrefix.length());
        String filename = Paths.get(fileNameWithPotentialPath)
                               .getFileName()
                               .toString();
        log.info("파일 이름: {}", filename);
        return this.uploadPath.resolve(filename);
    }

    private String extractExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf('.') + 1)
                           .toLowerCase();
        } catch (StringIndexOutOfBoundsException e) {
            throw new CommonException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFileExtension(String extension) {
        if (!allowedExtensions.contains(extension)) {
            throw new CommonException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }
}
