package com.project.memmem.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.project.memmem.domain.dto.img.ImgUploadDTO;
import com.project.memmem.domain.entity.ImageEntity.ImageType;

import lombok.RequiredArgsConstructor;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class FileUploadUtils {
	
	//3.0하위에서는  AmazonS3Client s3Client 객체로 처리해야함.
	//S3의 정보가 있어야해요
	private final S3Client s3Client;
	
	
	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;
	@Value("${spring.cloud.aws.s3.upload-temp}")
	private String temp;
	@Value("${spring.cloud.aws.s3.upload-src}")
	private String upload;
	
	public Map<String, String> s3TempUpload(MultipartFile itemFile) throws IOException {
		//System.out.println(">>>>"+s3Client);
		String orgFileName=itemFile.getOriginalFilename();
		String newFileName=newFileName(orgFileName);
		String bucketkey=temp+newFileName;
		
		InputStream is=itemFile.getInputStream();
		PutObjectRequest putObjectRequest=PutObjectRequest.builder()
				.bucket(bucket)
				.key(bucketkey)
				.contentType(itemFile.getContentType())
				.acl(ObjectCannedACL.PUBLIC_READ)
				.build();
		RequestBody requestBody=RequestBody.fromInputStream(is, itemFile.getSize());
		//S3에 파일업로드 처리
		s3Client.putObject(putObjectRequest, requestBody);
		
		String url=s3Client.utilities()
			.getUrl(builder->builder.bucket(bucket).key(bucketkey))
			.toString().substring(6); // https://이미지주소
		
		Map<String, String> resultMap=new HashMap<>();
		resultMap.put("url",url);
		resultMap.put("bucketKey",bucketkey);
		resultMap.put("orgName",orgFileName);
		return resultMap;
	}

	private String newFileName(String orgFileName) {
		int index=orgFileName.lastIndexOf(".");// .위치
		return UUID.randomUUID().toString()
				+ orgFileName.substring(index);// ".png"
	}
	
	
	public String s3TempToImage(String tempKey) {
		
			//tempKey : ex (item/upload/temp/035c686e-4ef4-4c40-981e-8fe3542710dd.jpg)
			String[] str=tempKey.split("/");
			String destinationKey=upload+str[str.length-1];
			
			CopyObjectRequest copyObjectRequest=CopyObjectRequest.builder()
					.sourceBucket(bucket)
					.sourceKey(tempKey)
					.destinationBucket(bucket)
					.destinationKey(destinationKey)
					.acl(ObjectCannedACL.PUBLIC_READ)
					.build();
			
			s3Client.copyObject(copyObjectRequest);
			s3Client.deleteObject(builder->builder.bucket(bucket).key(tempKey)); //삭제할게요
			
			//String url=s3Client.utilities().getUrl(builder->builder.bucket(bucket).key(destinationKey)).toString().substring(6);
			
		
		return destinationKey;
		
	}

	public ImgUploadDTO s3TempToImages(List<String> tempKeys, ImageType imageType) {
		List<String> destinationKeys=new ArrayList<>();
		List<String> uploadUrls=new ArrayList<>();
		tempKeys.forEach(tempKey->{
			//tempKey : ex (item/upload/temp/035c686e-4ef4-4c40-981e-8fe3542710dd.jpg)
			String[] str=tempKey.split("/");
			String destinationKey=upload+str[str.length-1];
			
			CopyObjectRequest copyObjectRequest=CopyObjectRequest.builder()
					.sourceBucket(bucket)
					.sourceKey(tempKey)
					.destinationBucket(bucket)
					.destinationKey(destinationKey)
					.acl(ObjectCannedACL.PUBLIC_READ)
					.build();
			
			s3Client.copyObject(copyObjectRequest);
			s3Client.deleteObject(builder->builder.bucket(bucket).key(tempKey)); //삭제할게요
			
			String url=s3Client.utilities()
					.getUrl(builder->builder.bucket(bucket).key(destinationKey))
					.toString().substring(6);
			uploadUrls.add(url);
			destinationKeys.add(destinationKey);
		});
		
		return ImgUploadDTO.builder()
				.uploadUrls(uploadUrls)
				.uploadKeys(destinationKeys)
				.imageType(imageType)
				.build();
		
	}

}