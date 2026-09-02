package com.saranaresturantsystem.controllers;

import com.saranaresturantsystem.dto.response.ApiResponse;
import com.saranaresturantsystem.entities.FileMetadata;
import com.saranaresturantsystem.services.file.S3FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("api/v1/files")
@RequiredArgsConstructor
@Tag(name = "S3 File Management", description = "APIs for uploading, previewing, and downloading files from S3/RustFS buckets")
public class S3FileController {
    private final S3FileService s3FileService;

    @Operation(summary = "Upload a file to a bucket")
    @PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponse<FileMetadata>> uploadFile(
            @Parameter(description = "Target bucket name") @RequestParam String bucketName,
            @Parameter(description = "File to upload") @RequestParam MultipartFile file) {

        FileMetadata fileMetadata = s3FileService.uploadFile(bucketName, file);

        ApiResponse<FileMetadata> response = ApiResponse.<FileMetadata>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .message("File uploaded successfully to bucket: " + bucketName)
                .payload(fileMetadata)
                .Instant(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Upload multiple files to a bucket")
    @PostMapping(value = "/upload-multiple-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('file:upload')")
    public ResponseEntity<ApiResponse<List<FileMetadata>>> uploadMultipleFiles(
            @Parameter(description = "Target bucket name") @RequestParam String bucketName,
            @Parameter(description = "Files to upload") @RequestParam List<MultipartFile> files) {

        List<FileMetadata> fileMetadataList = s3FileService.uploadMultipleFiles(bucketName, files);

        ApiResponse<List<FileMetadata>> response = ApiResponse.<List<FileMetadata>>builder()
                .success(true)
                .status(HttpStatus.CREATED)
                .message(files.size() + " file(s) uploaded successfully to bucket: " + bucketName)
                .payload(fileMetadataList)
                .Instant(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Preview a file from a bucket")
    @GetMapping("/preview-file/{file-name}")
    public ResponseEntity<Resource> getFileByFileName(
            @Parameter(description = "Bucket name") @RequestParam String bucketName,
            @Parameter(description = "File name") @PathVariable("file-name") String fileName) {
        Resource resource = s3FileService.getFileByFileName(bucketName, fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }

    @Operation(summary = "Download a file from a bucket")
    @GetMapping("/download-file/{file-name}")
    @PreAuthorize("hasAuthority('file:read')")
    public ResponseEntity<Resource> downloadFileByFileName(
            @Parameter(description = "Bucket name") @RequestParam String bucketName,
            @Parameter(description = "File name") @PathVariable("file-name") String fileName) {
        Resource resource = s3FileService.getFileByFileName(bucketName, fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @Operation(summary = "List all files in a bucket", description = "Returns metadata for all files stored in the specified bucket")
    @GetMapping
    @PreAuthorize("hasAuthority('file:read')")
    public ResponseEntity<ApiResponse<List<FileMetadata>>> getAllFiles(
            @Parameter(description = "Bucket name") @RequestParam String bucketName) {

        List<FileMetadata> files = s3FileService.getAllFiles(bucketName);

        ApiResponse<List<FileMetadata>> response = ApiResponse.<List<FileMetadata>>builder()
                .success(true)
                .status(HttpStatus.OK)
                .message("Found " + files.size() + " file(s) in bucket: " + bucketName)
                .payload(files)
                .Instant(Instant.now())
                .build();

        return ResponseEntity.ok(response);
    }

}