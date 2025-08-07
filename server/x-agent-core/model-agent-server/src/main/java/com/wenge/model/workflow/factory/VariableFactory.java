package com.wenge.model.workflow.factory;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wenge.model.enums.VariableStatusEnum;
import com.wenge.model.workflow.component.exception.NodeException;
import com.wenge.model.workflow.component.file.File;
import com.wenge.model.workflow.component.file.FileTransferMethod;
import com.wenge.model.workflow.component.file.FileType;
import com.wenge.model.workflow.component.variable.*;
import com.wenge.model.workflow.enums.MetaParamEnum;
import com.wg.appframe.core.constant.StringConstant;
import com.wg.appframe.yayi.result.DocumentResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 变量工厂
 */
@Slf4j
public class VariableFactory {

    /**
     * 创建变量，默认变量状态为完成
     * @param valueType
     * @param nodeId
     * @param name
     * @param value
     * @return
     */
    public static Variable createVariable(String nodeName, String valueType, String nodeId, String name, Object value) {
        return createVariable(nodeName, valueType, nodeId, name, value, VariableStatusEnum.COMPLETE);
    }

    /**
     * 创建变量
     *
     * @param valueType
     * @param nodeId
     * @param name
     * @param value
     * @param statusEnum
     * @return
     */
    public static Variable createVariable(String nodeName, String valueType, String nodeId, String name, Object value, VariableStatusEnum statusEnum) {
        if (StringUtils.isBlank(valueType)) {
            throw new NodeException(nodeId, new IllegalArgumentException("变量类型不能为空"));
        }

        MetaParamEnum type1 = MetaParamEnum.getType(valueType);
        Segments<?> segments = null;

        switch (type1) {
            case OBJECT:
                Object o = caseObj(nodeName, name, value);
                segments = new ObjectSegment(name, o);
                break;
            case ARRAY_OBJ:
                List<Object> objects = caseArrayObj(nodeName, name, value);
                segments = new ArrayObjectSegment(name, objects);
                break;
            case STRING:
                String str = caseString(nodeName, name, value);
                segments = new StringSegment(name, str);
                break;
            case NUMBER:
                Number number = caseNumber(nodeName, name, value);
                if (null != number) {
                    segments = new FloatSegment(name, number.doubleValue());
                } else {
                    segments = new FloatSegment(name, null);
                }
                break;
            case INTEGER:
                Long aLong = caseLong(nodeName, name, value);
                segments = new IntegerSegment(name, aLong);
                break;
            case FILE:
                File file = caseFile(nodeName, name, value);
                segments = new FileSegment(name, file);
                break;
            case ARRAY_FILE:
                List<File> fileList = caseArrayFile(nodeName, name, value);
                segments = new ArrayFileSegment(name, fileList);
                break;
            case ARRAY_STR:
                List<String> strings = caseArrayStr(nodeName, name, value);
                segments = new ArrayStringSegment(name, strings);
                break;
            case ARRAY_INT:
                List<Long> longList = caseArrayInt(nodeName, name, value);
                segments = new ArrayIntegerSegment(name, longList);
                break;
            case ARRAY_NUMBER:
                List<Number> numbers = caseArrayNum(nodeName, name, value);
                segments = new ArrayNumberSegment(name, numbers);
                break;
            case ARRAY_BOOL:
                segments = new ArrayBooleanSegment(name, (List<Boolean>) value);
                break;
            case BOOLEAN:
                segments = new BooleanSegment(name, Boolean.parseBoolean(value.toString()));
                break;
            default:
                throw new NodeException(nodeName, name + ":Unexpected value: " + valueType);
        }
        segments.setStatusEnum(statusEnum);
        return Variable.builder().nodeId(nodeId).name(name).segments(segments).build();
    }

    /**
     * 构建文件
     * @param entries
     * @return
     */
    private static File buildFile(JSONObject entries) {
        File bean = entries.toBean(File.class, true);
        FileType type = FileType.getFileType(entries.getStr("type"));
        bean.setType(type);
        FileTransferMethod transferMethod = FileTransferMethod.getFileTransferMethod(entries.getStr("transferMethod"));
        bean.setTransferMethod(transferMethod);
        return bean;
    }

    /**
     * 节点入参类型转换,string
     *
     * @param var
     * @param value
     * @return
     */
    public static String caseString(String nodeName, String var, Object value) {
        String newValue = null;
        if (null == value) {
            return StringConstant.BLANK;
        }
        try {
            if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                newValue = value.toString();
            } else if (value instanceof List || value instanceof Map || value instanceof File) {
                newValue = JSONUtil.toJsonStr(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为字符串：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,long
     * @param var
     * @param value
     * @return
     */
    public static Long caseLong(String nodeName, String var, Object value) {
        Long newValue = null;
        if (null == value) {
            return newValue;
        }
        try {
            if (value instanceof String && NumberUtil.isInteger(value.toString())) {
                newValue = Long.parseLong(value.toString());
            } else if (value instanceof Number) {
                Number number = (Number) value;
                newValue = number.longValue();
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为整数：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,number
     * @param var
     * @param value
     * @return
     */
    public static Number caseNumber(String nodeName, String var, Object value) {
        Number newValue = null;
        if (null == value) {
            return newValue;
        }
        try {
            if (value instanceof String && NumberUtil.isNumber(value.toString())) {
                newValue = NumberUtil.parseNumber(value.toString());
            } else if (value instanceof Number) {
                newValue = (Number) value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为数字：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,file
     * @param var
     * @param value
     * @return
     */
    public static File caseFile(String nodeName, String var, Object value) {
        File newValue = null;
        if (null == value) {
            return newValue;
        }

        try {
            if (value instanceof String && JSONUtil.isTypeJSONObject(value.toString())) {
                JSONObject entries = JSONUtil.parseObj(value.toString());
                newValue = entries.toBean(File.class);
                if (StringUtils.isBlank(newValue.getRemoteUrl())) {
                    newValue = null;
                } else {
                    newValue = buildFile(entries);
                }
            } else if (value instanceof File) {
                newValue = (File) value;
            } else if (value instanceof Map) {
                JSONObject entries = JSONUtil.parseObj(JSONUtil.toJsonStr(value));
                newValue = buildFile(entries);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为文件：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,object
     * @param var
     * @param value
     * @return
     */
    public static Object caseObj(String nodeName, String var, Object value) {
        if (null == value) {
            return value;
        }
        Object newValue = null;
        if (value instanceof String && JSONUtil.isTypeJSONObject(value.toString())) {
            newValue = JSONUtil.parseObj(value.toString());
        } else if (value instanceof Map || value instanceof File) {
            newValue = value;
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为对象：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,array[object]
     *
     * @param var
     * @param value
     * @return
     */
    public static List<Object> caseArrayObj(String nodeName, String var, Object value) {
        List newValue = null;
        if (null == value) {
            return Lists.newArrayList();
        }
        try {
            if (value instanceof String && JSONUtil.isTypeJSONArray(value.toString())) {
                JSONArray array = JSONUtil.parseArray(value.toString());
                if (CollectionUtil.isNotEmpty(array)) {
                    Object item = array.get(0);
                    if (item instanceof String && JSONUtil.isTypeJSONObject(item.toString())) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(p -> JSONUtil.parseObj(p.toString()))
                                .collect(Collectors.toList());
                    } else if (item instanceof Map) {
                        newValue = array.toList(JSONObject.class);
                    } else if (item instanceof File) {
                        newValue = array.toList(File.class);
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            } else if (value instanceof List) {
                if (CollectionUtil.isNotEmpty((List) value)) {
                    Object item = ((List) value).get(0);
                    if (item instanceof String && JSONUtil.isTypeJSONObject(item.toString())) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(p -> JSONUtil.parseObj(p.toString()))
                                .collect(Collectors.toList());
                    } else if (item instanceof Map) {
                        newValue = (List<Map>) value;
                    }  else if (item instanceof List) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(p -> caseArrayObj(nodeName, var, p))
                                .collect(Collectors.toList());
                        // newValue = (List<List>) value;
                    } else if (item instanceof File) {
                        newValue = (List<File>) value;
                    } else if (item instanceof  DocumentResult.FileContent) {
                        newValue =  (List<DocumentResult.FileContent>)value;
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为对象数组：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,array[int]
     * @param var
     * @param value
     * @return
     */
    public static List<Long> caseArrayInt(String nodeName, String var, Object value) {
        List<Long> newValue = null;
        if (null == value) {
            return Lists.newArrayList();
        }
        try {
            if (value instanceof String && JSONUtil.isTypeJSONArray(value.toString())) {
                JSONArray array = JSONUtil.parseArray(value.toString());
                if (CollectionUtil.isNotEmpty(array)) {
                    Object item = array.get(0);
                    if (item instanceof String && NumberUtil.isNumber(item.toString())) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(p -> NumberUtil.parseNumber(p.toString()).longValue())
                                .collect(Collectors.toList());
                    } else if (item instanceof Number) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(p -> ((Number) p).longValue())
                                .collect(Collectors.toList());
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            } else if (value instanceof List) {
                if (CollectionUtil.isNotEmpty((List) value)) {
                    Object item = ((List) value).get(0);
                    if (item instanceof String && NumberUtil.isNumber(item.toString())) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(p -> NumberUtil.parseNumber(p.toString()).longValue())
                                .collect(Collectors.toList());
                    } else if (item instanceof Number) {
                        newValue = ((List<Number>) value).stream()
                                .filter(Objects::nonNull)
                                .map(Number::longValue)
                                .collect(Collectors.toList());
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为整数数组：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,array[number]
     * @param var
     * @param value
     * @return
     */
    public static List<Number> caseArrayNum(String nodeName, String var, Object value) {
        List<Number> newValue = null;
        if (null == value) {
            return Lists.newArrayList();
        }

        try {
            if (value instanceof String && JSONUtil.isTypeJSONArray(value.toString())) {
                JSONArray array = JSONUtil.parseArray(value.toString());
                if (CollectionUtil.isNotEmpty(array)) {
                    Object item = array.get(0);
                    if (item instanceof String && NumberUtil.isNumber(item.toString())) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(p -> NumberUtil.parseNumber(p.toString()))
                                .collect(Collectors.toList());
                    } else if (item instanceof Number) {
                        newValue = array.toList(Number.class);
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            } else if (value instanceof List) {
                if (CollectionUtil.isNotEmpty((List) value)) {
                    Object item = ((List) value).get(0);
                    if (item instanceof String && NumberUtil.isNumber(item.toString())) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(p -> NumberUtil.parseNumber(p.toString()))
                                .collect(Collectors.toList());
                    } else if (item instanceof Number) {
                        newValue = (List<Number>) value;
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为数字数组：", var));
        }
        return newValue;
    }

    /**
     * 节点入参类型转换,array[string]
     *
     * @param var
     * @param value
     * @return
     */
    public static List<String> caseArrayStr(String nodeName, String var, Object value) {
        List<String> newValue = null;
        if (null == value) {
            return Lists.newArrayList();
        }
        try {
            if (value instanceof String && JSONUtil.isTypeJSONArray(value.toString())) {
                JSONArray array = JSONUtil.parseArray(value.toString());
                // newValue = array.toList(String.class);
                if (CollectionUtil.isNotEmpty(array)) {
                    Object item = array.get(0);
                    if (item instanceof Number || item instanceof Boolean || item instanceof String) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    } else if (item instanceof File || item instanceof Map || item instanceof List) {
                        newValue = array.stream()
                                .filter(Objects::nonNull)
                                .map(JSONUtil::toJsonStr)
                                .collect(Collectors.toList());
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            } else if (value instanceof List) {
                if (CollectionUtil.isNotEmpty((List) value)) {
                    Object item = ((List) value).get(0);
                    if (item instanceof Number || item instanceof Boolean || item instanceof String) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(Object::toString)
                                .collect(Collectors.toList());
                    } else if (item instanceof File || item instanceof Map || item instanceof List || item instanceof DocumentResult.FileContent) {
                        newValue = ((List<Object>) value).stream()
                                .filter(Objects::nonNull)
                                .map(JSONUtil::toJsonStr)
                                .collect(Collectors.toList());
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为文本数组：", var));
        }

        return newValue;
    }

    /**
     * 节点入参类型转换,array[file]
     * @param var
     * @param value
     * @return
     */
    public static List<File> caseArrayFile(String nodeName, String var, Object value) {
        List<File> newValue = null;
        if (null == value) {
            return Lists.newArrayList();
        }
        try {
            if (value instanceof String && JSONUtil.isTypeJSONArray(value.toString())) {
                JSONArray array = JSONUtil.parseArray(value.toString());
                List<JSONObject> list = array.toList(JSONObject.class);
                newValue = array.toList(File.class);
                boolean anyMatch = newValue.stream().anyMatch(p -> StringUtils.isBlank(p.getRemoteUrl()));
                if (anyMatch) {
                    newValue = Lists.newArrayList();
                } else {
                    newValue = list.stream()
                            .filter(Objects::nonNull)
                            .map(VariableFactory::buildFile)
                            .collect(Collectors.toList());
                }
            } else if (value instanceof List) {
                List<Object> list = (List<Object>) value;
                if (CollectionUtil.isNotEmpty(list)) {
                    Object item = list.get(0);
                    if (item instanceof String && JSONUtil.isTypeJSONObject(item.toString())) {
                        newValue = list.stream()
                                .filter(Objects::nonNull)
                                .map(p -> {
                                    JSONObject entries = JSONUtil.parseObj(p.toString());
                                    return buildFile(entries);
                                }).collect(Collectors.toList());
                    } else if (item instanceof File) {
                        newValue = (List<File>) value;
                    } else if (item instanceof Map) {
                        newValue = list.stream()
                                .filter(Objects::nonNull)
                                .map(p -> {
                                    JSONObject entries = JSONUtil.parseObj(JSONUtil.toJsonStr(p));
                                    return buildFile(entries);
                                }).collect(Collectors.toList());
                    }
                } else {
                    newValue = Lists.newArrayList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == newValue) {
            throw new NodeException(nodeName, StrUtil.format("参数【{}】无法转为文件数组：", var));
        }

        return newValue;
    }

}
