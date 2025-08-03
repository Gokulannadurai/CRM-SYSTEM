package com.crm.sales.dto;

import lombok.Data;

@Data
public class AttachmentDTO {
    private String url;
    private String fileName;
    private String fileType;
    private byte[] bytes;
    private int displayOrder;
}
