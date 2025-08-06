package com.wenge.model.dto.result;

import com.wg.appframe.core.dto.params.WGParam;
import com.wg.appframe.core.dto.results.WGResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

@EqualsAndHashCode(callSuper = true)
@Data
public class TranslateResult extends WGResult {

    private static final long serialVersionUID = 2572595348654734872L;

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

    /**
     * 翻译类型  file-文件 text-文本
     */
    private String translateType = "text";


    private String srcFileUrl;

    private String tgtFileUrl;


}
