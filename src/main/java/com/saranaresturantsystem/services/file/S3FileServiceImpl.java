package com.saranaresturantsystem.services.file;
import com.saranaresturantsystem.entities.FileMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class S3FileServiceImpl implements S3FileService {
    private final S3Client s3Client;

    @Override
    public FileMetadata uploadFile(String bucketName, MultipartFile file) {
        // Generate name with uuid
        String originalFileName = file.getOriginalFilename();
        String fileName = UUID.randomUUID() + "." + StringUtils.getFilenameExtension(originalFileName);
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to RustFS", e);
        }

        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v2/files/preview-file/" + fileName)
                .toUriString();

        return FileMetadata.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .build();
    }

    @Override
    public List<FileMetadata> uploadMultipleFiles(String bucketName, List<MultipartFile> files) {
        return files.stream()
                .map(file -> uploadFile(bucketName, file))
                .toList();
    }

    @Override
    public Resource getFileByFileName(String bucketName, String key) {
        try {
            InputStream inputStream = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build()
            );

            // Wrap the InputStream in a Spring Resource and return it to controller
            return new InputStreamResource(inputStream);

        } catch (NoSuchKeyException e) {
            throw new RuntimeException("File not found: " + key, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from RustFS", e);
        }
    }

    @Override
    public List<FileMetadata> getAllFiles(String bucketName) {
        try {
            ListObjectsV2Response response = s3Client.listObjectsV2(
                    ListObjectsV2Request.builder()
                            .bucket(bucketName)
                            .build()
            );

            return response.contents().stream()
                    .map(s3Object -> {
                        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                .path("/api/v2/files/preview-file/" + s3Object.key())
                                .queryParam("bucketName", bucketName)
                                .toUriString();

                        return FileMetadata.builder()
                                .fileName(s3Object.key())
                                .fileUrl(fileUrl)
                                .fileSize(s3Object.size())
                                .build();
                    })
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException("Failed to list files from bucket: " + bucketName, e);
        }
    }
}