package com.wenge.model.workflow.component.exception;


import org.apache.commons.lang3.StringUtils;

/**
 * 工作流异常类
 */
public class NodeException extends RuntimeException {
    private final String nodeName;
    private final String msg;

    public NodeException(String nodeName, Exception e) {
        super(e);
        this.nodeName = nodeName;
        this.msg = e.getMessage();
    }

    public NodeException(String nodeName, String msg) {
        super(msg);
        this.nodeName = nodeName;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return "【" + nodeName + "】节点发生异常:" + (StringUtils.isNotBlank(msg) ? msg : super.getMessage());
    }

}
