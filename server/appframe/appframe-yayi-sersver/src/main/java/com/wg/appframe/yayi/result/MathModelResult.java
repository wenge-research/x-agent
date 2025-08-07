package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class MathModelResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = -701703659745894297L;

    private Generate30BResult.GenerateData data;
}
