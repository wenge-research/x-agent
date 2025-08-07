package com.wg.appframe.yayi.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChartResult extends YayiResult implements Serializable {

    private static final long serialVersionUID = 1217520228577842317L;

    private Generate30BResult.GenerateData data;
}
