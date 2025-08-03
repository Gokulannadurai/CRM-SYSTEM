package com.crm.sales.common;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.crm.sales.constants.SalesConstants;
import com.crm.sales.dto.AttachmentDTO;
import com.crm.sales.dto.LeadDto;
import com.crm.sales.dto.TaskDto;
import com.crm.sales.entity.Lead;
import com.crm.sales.entity.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Component
public class AwsFileService {

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3-bucket-name}")
    private String bucketName;


    @Autowired
    public AwsFileService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }


    public String uploadFile(MultipartFile file) {
        Instant instant = Instant.now();
        File fileObj = convertMultipartFileToFile(file);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                .withZone(ZoneId.of(SalesConstants.TIMEZONE_UTC));
        String timestamp = formatter.format(instant);
        String fileName = concatString( UUID.randomUUID()
                        +  SalesConstants.UNDER_SCORE + timestamp, SalesConstants.UNDER_SCORE,
                file.getOriginalFilename());
        s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        return s3Client.getUrl(bucketName, fileName).toString();
    }

    private File convertMultipartFileToFile(MultipartFile file) {
        File convertedFile = new File(file.getOriginalFilename());

        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(file.getBytes());
        } catch (IOException exception) {
            log.info(exception.getMessage());
        }
        return convertedFile;
    }

    public AttachmentDTO retrieveFile(String key) {
        AttachmentDTO attachmentDTO = new AttachmentDTO();
        try {
            S3Object s3Object = s3Client.getObject(bucketName, key);
            S3ObjectInputStream s3ObjectInputStream = s3Object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(s3ObjectInputStream);
            s3ObjectInputStream.close();
            attachmentDTO.setBytes(bytes);
        } catch (SdkClientException | IOException exception) {
            log.info(exception.getMessage());
        }
        return attachmentDTO;
    }


    public TaskDto setAttachmentsToTask(Task task,
                                           TaskDto taskDto) {
        List<AttachmentDTO> list = new ArrayList<>();

        if (task.getAttachments()!=null && !task.getAttachments().isEmpty()) {
            task.getAttachments().forEach(attachment -> {
                if (Objects.nonNull(attachment.getUrl())) {
                    try {
                        String hostname = String.format(SalesConstants.S3_HOST_NAME,
                                bucketName, s3Client.getRegionName());
                        String decodedUrl = URLDecoder.decode(attachment.getUrl(), StandardCharsets.UTF_8);
                        String key = decodedUrl.split(hostname)[1];
                        AttachmentDTO attachmentDTO = retrieveFile(key);
                        attachmentDTO.setUrl(attachment.getUrl());
                        attachmentDTO.setFileName(attachment.getFileName());
                        attachmentDTO.setFileType(attachment.getFileType());
                        attachmentDTO.setDisplayOrder(attachment.getDisplayOrder());
                        list.add(attachmentDTO);
                    } catch (Exception e) {
                        log.error("Error processing attachment URL: {}", attachment.getUrl(), e);
                        // Add attachment without bytes if URL processing fails
                        AttachmentDTO attachmentDTO = new AttachmentDTO();
                        attachmentDTO.setUrl(attachment.getUrl());
                        attachmentDTO.setFileName(attachment.getFileName());
                        attachmentDTO.setFileType(attachment.getFileType());
                        attachmentDTO.setDisplayOrder(attachment.getDisplayOrder());
                        list.add(attachmentDTO);
                    }
                }
            });
        }
        taskDto.setAttachments(list);
        return taskDto;
    }

    public LeadDto setAttachmentsToLead(Lead lead,
                                        LeadDto leadDto) {
        List<AttachmentDTO> list = new ArrayList<>();

        if (lead.getAttachments()!=null && !lead.getAttachments().isEmpty()) {
            lead.getAttachments().forEach(attachment -> {
                if (Objects.nonNull(attachment.getUrl())) {
                    try {
                        String hostname = String.format(SalesConstants.S3_HOST_NAME,
                                bucketName, s3Client.getRegionName());
                        String decodedUrl = URLDecoder.decode(attachment.getUrl(), StandardCharsets.UTF_8);
                        String key = decodedUrl.split(hostname)[1];
                        AttachmentDTO attachmentDTO = retrieveFile(key);
                        attachmentDTO.setUrl(attachment.getUrl());
                        attachmentDTO.setFileName(attachment.getFileName());
                        attachmentDTO.setFileType(attachment.getFileType());
                        attachmentDTO.setDisplayOrder(attachment.getDisplayOrder());
                        list.add(attachmentDTO);
                    } catch (Exception e) {
                        log.error("Error processing attachment URL: {}", attachment.getUrl(), e);
                        // Add attachment without bytes if URL processing fails
                        AttachmentDTO attachmentDTO = new AttachmentDTO();
                        attachmentDTO.setUrl(attachment.getUrl());
                        attachmentDTO.setFileName(attachment.getFileName());
                        attachmentDTO.setFileType(attachment.getFileType());
                        attachmentDTO.setDisplayOrder(attachment.getDisplayOrder());
                        list.add(attachmentDTO);
                    }
                }
            });
        }
        leadDto.setAttachments(list);
        return leadDto;
    }

    public static String concatString(String... args) {
        StringJoiner buildString = new StringJoiner(SalesConstants.EMPTY);
        for (String arg : args) {
            buildString.add(arg);
        }
        return buildString.toString();
    }
}
