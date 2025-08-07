package com.wenge.model.dto.param;

import com.wg.appframe.core.dto.params.WGParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TranslateParam extends WGParam {

    private static final long serialVersionUID = 2572595348654734873L;

    /**
     * 文本
     */
    private String text;

    /**
     * 源语言，zh-中文，en-英文
     */
    private String srcLang;

    /**
     * 目标语言，zh-中文，en-英文
     */
    private String tgtLang;

//    /**
//     * 翻译类型  file-文件 text-文本
//     */
//    private String translateType = "text";
//
//    /**
//     * 文件 pdf word txt execl 类型
//     */
//    private MultipartFile file;
//
//    /**
//     * 文件 pdf doc docx txt xlsx xls 类型
//     */
//    private String fileType;

}
