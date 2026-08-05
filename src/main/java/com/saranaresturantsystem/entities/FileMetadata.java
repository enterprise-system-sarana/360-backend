package com.saranaresturantsystem.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileMetadata {
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
}