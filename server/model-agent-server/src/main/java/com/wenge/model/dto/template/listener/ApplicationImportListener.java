package com.wenge.model.dto.template.listener;
import com.alibaba.nacos.api.utils.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import javax.imageio.ImageIO;

public class ApplicationImportListener {
    public static void main(String[] args) throws IOException, InvalidFormatException {
        /*try {
            // 创建一个空的Word文档对象
            XWPFDocument doc = new XWPFDocument();

            // 创建一个段落
            XWPFParagraph paragraph = doc.createParagraph();
            // 设置段落的样式
            paragraph.setStyle("Heading1");
            // 添加文本到段落
            XWPFRun run = paragraph.createRun();
            run.setText("Hello, World!");
            run.setFontSize(16); // 设置字体大小
            run.setBold(true); // 设置粗体
            run.setColor("0000FF"); // 设置字体颜色为蓝色

            // 创建另一个段落，并添加文本
            paragraph = doc.createParagraph();
            run = paragraph.createRun();
            run.setText("This is another paragraph.");
            run.setFontSize(14); // 设置字体大小
            run.setItalic(true); // 设置斜体

            // 将文档写入文件系统
            FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\jjj\\example.docx");
            doc.write(out);
            out.close();
            doc.close();
            System.out.println("Document created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        downloadInterceptWordDataTemp();
    }
    public static void downloadInterceptWordDataTemp() {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            DefaultResourceLoader resourceLoader = new DefaultResourceLoader();

            // 从类路径加载资源
            //Resource classpathResource = loader.getResource("classpath:application.properties");
            Resource resource = resourceLoader.getResource("classpath:temp/applicationStatdocx.docx");
            Resource resource1 = resourceLoader.getResource("classpath:temp/ai1.jpg");
            Resource resource2 = resourceLoader.getResource("classpath:temp/ai2.jpg");
            Resource resource3 = resourceLoader.getResource("classpath:temp/ai3.jpg");
            Resource resource4 = resourceLoader.getResource("classpath:temp/ai4.jpg");
            XWPFDocument document = new XWPFDocument(resource.getInputStream());
            // 替换占位符
            Map<String, String> replacements = new HashMap<>();
            replacements.put("x1", "31");
            replacements.put("y1", "351");
            replacements.put("x2", "352");
            replacements.put("y2", "352");
            replacePlaceholder(document, replacements);
            List<InputStream>inputStreamList=new ArrayList<>();
            inputStreamList.add(resource1.getInputStream());
            inputStreamList.add(resource2.getInputStream());
            inputStreamList.add(resource3.getInputStream());
            inputStreamList.add(resource4.getInputStream());
            insertImage(document,null,inputStreamList);
            FileOutputStream fos = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\jjj\\example2.docx");
            document.write(fos);
            fos.close();
            /*// 设置响应头
            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
            // 获取文件名，并进行UTF-8编码
            String fileName = URLEncoder.encode("测评模板下载.xlsx", StandardCharsets.UTF_8.toString());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

            // 将文件写入响应输出流
            outputStream = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != outputStream) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private static void replacePlaceholder(XWPFDocument document, Map<String, String> replacements) {
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : replacements.entrySet()) {
                        text = text.replace(entry.getKey(), entry.getValue());
                    }
                    run.setText(text, 0);
                }
            }
        }
    }
    private static void insertImage(XWPFDocument document, String imagePath,List<InputStream> inputStream) throws IOException, InvalidFormatException {
        List<String>images=new ArrayList();
        images.add("image1.png");
        images.add("image2.png");
        images.add("image3.png");
        images.add("image4.png");
        for (XWPFParagraph itemParagraph : document.getParagraphs()) {
            for (XWPFRun itemRun : itemParagraph.getRuns()) {
                for (XWPFPicture itemEmbeddedPicture : itemRun.getEmbeddedPictures()) {
                    // 确定某个图片（这里的图片之所以有自定义的描述，是因为图片是通过代码插入的）
                    String string=itemEmbeddedPicture.getPictureData().getFileName();
                    for (int i=0;i<images.size();i++) {
                        if (StringUtils.equals(images.get(i), itemEmbeddedPicture.getPictureData().getFileName())) {
                            // 待替换的图片文件
                            //File toReplaceImage = new File("xxx.jpg");
                            try{
                                // 替换图片
                                itemEmbeddedPicture.getCTPicture().getBlipFill().getBlip().setEmbed(document.addPictureData(
                                        inputStream.get(i),
                                        XWPFDocument.PICTURE_TYPE_JPEG));
                                // 根据图片修改大小
                                //BufferedImage bufferedImage = ImageIO.read(inputStream);
                                //itemRun.getCTR().getDrawingArray(0).getInlineList().get(0).getExtent().setCx(5000000L);
                                //itemRun.getCTR().getDrawingArray(0).getInlineList().get(0).getExtent().setCy(5000000L * bufferedImage.getHeight() / bufferedImage.getWidth());
                                //itemEmbeddedPicture.getCTPicture().getSpPr().getXfrm().getExt().setCx(5000000L);
                                //itemEmbeddedPicture.getCTPicture().getSpPr().getXfrm().getExt().setCy(5000000L * bufferedImage.getHeight() / bufferedImage.getWidth());
                            } catch (InvalidFormatException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}

