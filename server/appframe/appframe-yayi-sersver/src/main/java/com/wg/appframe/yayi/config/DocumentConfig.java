package com.wg.appframe.yayi.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DocumentConfig extends YayiConfigSupper {

    private static final long serialVersionUID = -2579091525285612905L;

    private String uri;
    private String userId;
    private Integer doLayout;
    private Integer getBbox;
    private Integer tableHtml;
    private Integer watermark;
    private Integer faq;
    private Integer mode;
}
