package com.wenge.model.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.query.QueryWrapper;
import com.wg.appframe.mybatisflex.core.FlexModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description:
 * @Author: Levi
 * @Date: 2025/3/27 11:06
 */
@ApiModel
@Data(staticConstructor = "create")
@Table(value = "my_favorite")
@EqualsAndHashCode(callSuper = false)
@Setter
@Getter
public class MyFavorite extends FlexModel<MyFavorite> {

    public MyFavorite() {
    }

    @Column(ignore = true)
    private QueryWrapper currentWrapper;

    /**
     * 主键，自增id，没有业务作用
     */
    @Id(value = "id", keyType = KeyType.Auto)
    @ApiModelProperty(value = "主键，自增id，没有业务作用")
    private Long id;

    /**
     * 业务id
     */
    @Column("favorite_id")
    @ApiModelProperty(value = "业务id")
    private String favoriteId;

    /**
     * 应用id
     */
    @Column("application_id")
    @ApiModelProperty(value = "关联的应用id")
    private String applicationId;

    /**
     * 用户id
     */
    @Column("user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 收藏标识 0：未收藏，1：已收藏
     */
    @Column(value = "favorite_flag")
    @ApiModelProperty(name = "favoriteFlag", value = "收藏标识 0：未收藏，1：已收藏", dataType = "Integer")
    private Integer favoriteFlag;

    /**
     * 创建时间
     */
    @Column("create_time")
    @ApiModelProperty(name = "createTime", value = "更新时间", dataType = "String")
    private String createTime;

    /**
     * 更新时间
     */
    @Column("update_time")
    @ApiModelProperty(name = "updateTime", value = "更新时间", dataType = "String")
    private String updateTime;

    /**
     * 模块类型
     */
    @Column("type")
    @ApiModelProperty(name = "type", value = "模块类型", dataType = "Integer")
    private Integer type;

}