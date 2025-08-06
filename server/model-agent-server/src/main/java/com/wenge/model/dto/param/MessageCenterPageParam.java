package com.wenge.model.dto.param;

import com.wg.appframe.core.bean.WgPageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageCenterPageParam extends WgPageInfo {

    private static final long serialVersionUID = 3514272693195916373L;

    private Integer messageStatus; //检索消息状态 0-未读 1-已读 null-全部


}
