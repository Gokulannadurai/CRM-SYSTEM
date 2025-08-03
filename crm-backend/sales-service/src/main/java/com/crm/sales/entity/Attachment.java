package com.crm.sales.entity;

import lombok.Data;

@Data
public class Attachment extends BaseEntity {

    private String url;

    private String fileName;

    private String fileType;

    private int displayOrder;

    /**
     * Default constructor
     */
    public Attachment() {
    }
}
