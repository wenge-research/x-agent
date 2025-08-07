package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Table;
import com.wg.appframe.mybatisflex.handler.FieldInsertListener;
import com.wg.appframe.mybatisflex.handler.FieldUpdateListener;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类表示银行网点的基本信息。
 */
@ApiModel
@Data
@Table(value = "lhmz_bank_branches", onInsert = FieldInsertListener.class, onUpdate = FieldUpdateListener.class)
@EqualsAndHashCode(callSuper = false)
public class BankBranch {

    /**
     * 序号，主键ID，自增。
     */
    @Column("id")
    private Long id;

    /**
     * 银行网点名称，不能为空。
     */
    @Column("branch_name")
    private String branchName;

    /**
     * 银行网点地址，不能为空。
     */
    @Column("address")
    private String address;

    /**
     * 联系人，不能为空。
     */
    @Column("contact_person")
    private String contactPerson;

    /**
     * 联系电话，不能为空。
     */
    @Column("contact_phone")
    private String contactPhone;
}
