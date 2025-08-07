package com.wg.appframe.core.utils;


import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;

/**
 * XML工具类
 */
@Slf4j
public class XMLUtil {

    public static final String XML_TITLE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    /**
     * 根据对象生成XML字符串
     *
     * @param object
     * @return
     */
    public static String convertToXML(Object object) {
        if (object != null) {
            StringWriter sw = new StringWriter();
            try {
                JAXBContext context = JAXBContext.newInstance(object.getClass());
                Marshaller marshaller = context.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(object, sw);
                return sw.toString();
            } catch (JAXBException e) {
                log.error(e.getMessage());
            }
        } else {
            log.debug("参数：[object:{}]为空", object);
        }
        return null;
    }

    /**
     * 根据对象生成XML字符串并取消XML的头
     *
     * @param object
     * @return
     */
    public static String convertToXMLRemoveHead(Object object) {

        return convertToXML(object).replace(XMLUtil.XML_TITLE, "");
    }


    /**
     * 将对象转化为XML文件并输出到指定位置
     *
     * @param object
     * @param path
     */
    public static void convertToXml(Object object, String path) {
        if (object != null && StringUtils.isNotBlank(path)) {
            FileWriter fw = null;
            try {
                fw = new FileWriter(path);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
                Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(object, fw);
            } catch (JAXBException e) {
                log.error(e.getMessage());
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        log.error(e.getMessage());
                    }
                }
            }
        } else {
            log.debug("参数：[object:{},path:{}]中某一项或多项为空", object, path);
        }
    }

    /**
     * 将XML字符串转化为Object对象
     * @param clazz
     * @param xmlStr
     * @param <T>
     * @return
     */
    /**
     * 特别注意！！！特别注意！！！特别注意！！！特别注意！！！
     * 这里的clazz参数不可以改为T t 否则会转换出错
     */
    public static <T> T convertXmlStrToObject(Class<T> clazz, String xmlStr) {
        if (clazz != null && StringUtils.isNotBlank(xmlStr)) {
            try {
                JAXBContext context = JAXBContext.newInstance(clazz);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                StringReader sr = new StringReader(xmlStr);
                return (T) unmarshaller.unmarshal(sr);
            } catch (JAXBException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

    /**
     * 将file类型的xml转换成对象
     *
     * @param xmlPath
     * @return
     */
    public static <T> T convertXmlFileToObject(Class<T> clazz, String xmlPath) {
        InputStream resourceAsStream = null;
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xmlPath);
            return (T) unmarshaller.unmarshal(resourceAsStream);
        } catch (JAXBException e) {
            log.error(e.getMessage());
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 将json类型的xml字符串转化为xml类型字符串
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
    public static <T> String convertJsonToXmlStr(String json, Class<T> t) {
        if (StringUtils.isNotBlank(json) && t != null) {
            return convertToXMLRemoveHead(JSONObject.parseObject(json, t));
        }
        return null;
    }

    /**
     * 将json类型的xml字符串转化为xml类型对象
     *
     * @param json
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T convertJsonToXml(String json, Class<T> t) {
        if (StringUtils.isNotBlank(json) && t != null) {
            return JSONObject.parseObject(json, t);
        }
        return null;
    }

    /**
     * 将json的xml字符串对象转化为XML文件并输出到指定位置
     *
     * @param json
     * @param t
     * @param path
     * @param <T>
     */
    public static <T> void convertJsonToXmlStr(String json, Class<T> t, String path) {
        T xmlObj = convertJsonToXml(json, t);
        if (xmlObj != null) {
            convertToXml(xmlObj, path);
        } else {
            log.debug("将JSON转化为XML后的字符串为空！");
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File("data_group.xml");
        System.out.println(file.getCanonicalPath());
        System.out.println(file.getAbsoluteFile().getPath().replace("\\", "/"));
        System.out.println(convertXmlFileToObject(Object.class, "table-info.xml"));
    }
}
