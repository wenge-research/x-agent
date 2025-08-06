package com.wenge.model.dto.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 阿里云解析格式对象
 */
@Data
public class PolicyAliyunAnalysis implements Serializable {

    private static final long serialVersionUID = 658332528240124906L;

    private String markdownContent;
}
