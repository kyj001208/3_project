package com.project.memmem.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class FileUploadUtil {
    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.upload-temp}")
    private String temp;

    @Value("${spring.cloud.aws.s3.upload-src}")
    private String upload;

    public Map<String, String> s3TempUpload(MultipartFile file) throws IOException {
        String orgFileName = file.getOriginalFilename();
        String newFileName = newFileName(orgFileName);
        String bucketKey = temp + newFileName;

        try (InputStream is = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(bucketKey)
                    .contentType(file.getContentType())
                    .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();
            RequestBody requestBody = RequestBody.fromInputStream(is, file.getSize());
            s3Client.putObject(putObjectRequest, requestBody);
        }

        String url = s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucket).key(bucketKey))
                .toString().substring(6); // https://이미지주소

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("url", url);
        resultMap.put("bucketKey", bucketKey);
        resultMap.put("orgName", orgFileName);
        return resultMap;
    }

    private String newFileName(String orgFileName) {
        int index = orgFileName.lastIndexOf(".");
        return UUID.randomUUID().toString() + orgFileName.substring(index);
    }

    public List<String> s3TempToImages(List<String> tempKeys) {
        List<String> uploadKeys = new ArrayList<>();
        tempKeys.forEach(tempKey -> {
            try {
                String objectKey = extractObjectKey(tempKey);
                String fileName = objectKey.substring(objectKey.lastIndexOf('/') + 1);
                
                // upload-src 설정을 그대로 사용
                String destinationKey = upload + "/" + fileName;

                System.out.println("Copying from: " + objectKey + " to: " + destinationKey);

                CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                		.sourceBucket(bucket)
                        .sourceKey(objectKey)
                        .destinationBucket(bucket)
                        .destinationKey(destinationKey)
                        .acl(ObjectCannedACL.PUBLIC_READ)  // 공개 읽기 권한 추가
                        .build();

                s3Client.copyObject(copyObjectRequest);
                s3Client.deleteObject(builder -> builder.bucket(bucket).key(objectKey));

                uploadKeys.add(destinationKey);
            } catch (Exception e) {
                System.err.println("Error processing key: " + tempKey);
                e.printStackTrace();
            }
        });

        return uploadKeys;
    }

    private String extractObjectKey(String tempKey) {
        if (tempKey.startsWith("http://") || tempKey.startsWith("https://")) {
            String[] parts = tempKey.split("/");
            return String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
        }
        return tempKey;
    }
}
