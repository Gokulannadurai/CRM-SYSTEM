package com.crm.sales.mapper;

import com.crm.sales.entity.Attachment;
import org.springframework.stereotype.Component;

@Component
public class AttachmentMapper {

    public Attachment converToAttachment(String url, int displayOrder,
                                        String fileName) {
        Attachment attachment = new Attachment();
        attachment.setUrl(url);
        attachment.setDisplayOrder(displayOrder);
        attachment.setFileName(fileName);
        attachment.setFileType(fileName.substring(fileName.lastIndexOf("."), fileName.length()));
        return attachment;
    }
}
