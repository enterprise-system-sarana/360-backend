package com.saranaresturantsystem.services.file;

import com.saranaresturantsystem.entities.FileMetadata;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3FileService {
    FileMetadata uploadFile(String bucketName, MultipartFile file);

    List<FileMetadata> uploadMultipleFiles(String bucketName, List<MultipartFile> files);

    Resource getFileByFileName(String bucketName, String fileName);

    List<FileMetadata> getAllFiles(String bucketName);
}