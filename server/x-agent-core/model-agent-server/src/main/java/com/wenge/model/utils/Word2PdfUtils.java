package com.wenge.model.utils;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.wenge.model.constants.FileTypeConstant;
import org.apache.http.entity.ContentType;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @Author: zs
 * @Create: 10:53 2024/9/6
 * @Description: Word2PdfUtils
 */
public class Word2PdfUtils {

    public static MultipartFile docToPdf(String name, byte[] bytes) throws Exception {
        //转换word为PDF
        ByteArrayInputStream docStream = new ByteArrayInputStream(bytes);
        Document doc = new Document(docStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        doc.save(byteArrayOutputStream, SaveFormat.PDF);

        byte[] pdfBytes = byteArrayOutputStream.toByteArray();
        InputStream pdfStream = new ByteArrayInputStream(pdfBytes);
        String pdfName = name.substring(0, name.lastIndexOf(".")) + FileTypeConstant.PDF_SUFFIX;
        return new MyMultipartFileVo(pdfName, pdfName, ContentType.APPLICATION_OCTET_STREAM.toString(), pdfStream);
    }

}
