package com.wenge.oauth.util;

import cn.hutool.core.collection.CollectionUtil;
import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryCondition;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.wenge.oauth.constants.MybatisFiledConstant;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.enums.OwnerTypeEnum;
import com.wenge.oauth.enums.PowerTypeEnum;
import com.wenge.oauth.holder.AppContextHolder;
import com.wenge.oauth.service.TablePermissionService;
import com.wg.appframe.core.constant.enums.YesNoEnum;
import com.wg.appframe.mybatisflex.core.FlexModel;
import com.wg.appframe.mybatisflex.core.Wrappers;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.wenge.oauth.entity.table.GrantDataTableDef.GRANT_DATA;


public class PermissionControlUtils {


    /**
     * 构建权限(包含管理员数据)
     * tablePermissionEnum
     *
     * @param wrappers * @param superAdmin 超级管理员列表
     */
    public static void buildPermission2(Wrappers<Object> wrappers, TablePermissionService tablePermissionService
            , List<String> superAdmin) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            wrappers.and(true, and -> {
                initExit2(null, and, tablePermissionService, superAdmin);
            });
        }
    }

    /**
     * 构建权限
     tablePermissionEnum
     * @param wrappers
     */
    public static void buildPermission(Wrappers<Object> wrappers, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            wrappers.and(true, and -> {
                initExit(null, and, tablePermissionService, OwnerTypeEnum.PERSONAL_GRANT_TENANT);
            });
        }
    }

    /**
     * 构建权限
     * tablePermissionEnum
     *
     * @param wrappers
     */
    public static void buildPermission(Wrappers<Object> wrappers, TablePermissionService tablePermissionService, OwnerTypeEnum ownerType) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (OwnerTypeEnum.OFFICIAL.equals(ownerType)
                || OwnerTypeEnum.PERSONAL.equals(ownerType)
                || !PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            wrappers.and(true, and -> {
                initExit(null, and, tablePermissionService, ownerType);
            });
        }
    }

    /**
     * 构建权限-不区分普通管理员
     tablePermissionEnum
     * @param wrappers
     */
    public static void buildNoConditionPermission(Wrappers<Object> wrappers, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            wrappers.and(true, and -> {
                initExitQueryCondition(null, and, tablePermissionService, OwnerTypeEnum.PERSONAL_GRANT_TENANT);
            });
        }
    }


    /**
     * 构建权限-不区分普通管理员
     tablePermissionEnum
     * @param wrappers
     */
    public static void buildNoConditionPermission(Wrappers<Object> wrappers, TablePermissionService tablePermissionService, OwnerTypeEnum ownerType) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (OwnerTypeEnum.OFFICIAL.equals(ownerType)
                || OwnerTypeEnum.PERSONAL.equals(ownerType)
                || !PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            wrappers.and(true, and -> {
                initExitQueryCondition(null, and, tablePermissionService, ownerType);
            });
        }
    }

    /**
     * 构建权限
     *
     * @param flexModel
     */
    public static void buildPermission(FlexModel flexModel, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            initExit(flexModel, null, tablePermissionService, OwnerTypeEnum.PERSONAL_GRANT_TENANT);
        }
    }

    /**
     * 构建权限
     *
     * @param flexModel
     */
    public static void buildPermission(FlexModel flexModel, TablePermissionService tablePermissionService, OwnerTypeEnum ownerType) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (OwnerTypeEnum.OFFICIAL.equals(ownerType)
                || OwnerTypeEnum.PERSONAL.equals(ownerType)
                || !PowerTypeEnum.SYSTEM_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            initExit(flexModel, null, tablePermissionService, ownerType);
        }
    }

    /**
     * 初始化查询条件
     *
     * @param flexModel
     * @param and
     * @param tablePermissionService
     */
    private static void initExit(FlexModel flexModel, QueryWrapper and, TablePermissionService tablePermissionService, OwnerTypeEnum ownerType) {
        QueryCondition condition = QueryCondition.createEmpty();
        // 默认查询个人/授权/本租户数据
        if (null == ownerType) {
            ownerType = OwnerTypeEnum.PERSONAL_GRANT_TENANT;
        }
        switch (ownerType) {
            // 官方
            case OFFICIAL:
                // 查询官方数据
                if (null != tablePermissionService && tablePermissionService.getOwnerType() != null) {
                    condition.or(tablePermissionService.getOwnerType().eq("official"));
                } else {
                    condition.or(QueryMethods.column("owner_type").eq("official"));
                }
                break;
            // 个人
            case PERSONAL:
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                break;
            //  授权
            case GRANT:
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                break;
            // 个人/授权
            case PERSONAL_GRANT:
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                break;
            //  个人/授权/本租户
            case PERSONAL_GRANT_TENANT:
                // 查询自己创建的知识库
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                // 如果是管理员，可以查询当前租户下的
                queryTenantData(condition, tablePermissionService);
                break;
            case ALL:
                // 查询官方数据
                if (null != tablePermissionService && tablePermissionService.getOwnerType() != null) {
                    condition.or(tablePermissionService.getOwnerType().eq("official"));
                } else {
                    condition.or(QueryMethods.column("owner_type").eq("official"));
                }
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                // 如果是管理员，可以查询当前租户下的
                queryTenantData(condition, tablePermissionService);
                break;
        }

        if (flexModel != null) {
            QueryWrapper wrapper = flexModel.getWrapperWg();
            if (null != wrapper) {
                wrapper.and(addd -> {
                    addd.or(condition);
                });
            }
        } else {
            and.and(condition);
        }
    }

    /**
     * 初始化查询条件
     *
     * @param flexModel
     * @param and
     * @param tablePermissionService
     * @param superAdmin             超级管理员列表
     */
    private static void initExit2(FlexModel flexModel, QueryWrapper and, TablePermissionService tablePermissionService
            , List<String> superAdmin) {
        QueryCondition condition = QueryCondition.createEmpty();
        // 查询自己创建的知识库
        queryCreateUser(condition, tablePermissionService);
        // 查询超管创建的
        querySuperAdmin(condition, superAdmin);
        // 查询授权的数据，查询授权的租户
        queryGrantData(condition, tablePermissionService);
        // 如果是管理员，可以查询当前租户下的知识库
        queryTenantData(condition, tablePermissionService);
        if (flexModel != null) {
            QueryWrapper wrapper = flexModel.getWrapperWg();
            if (null != wrapper) {
                wrapper.and(addd -> {
                    addd.or(condition);
                });
            }
        } else {
            and.and(condition);
        }
    }

    /**
     * 初始化查询条件-查询当前租户下的知识库
     *
     * @param flexModel
     * @param and
     * @param tablePermissionService
     */
    private static void initExitQueryCondition(FlexModel flexModel, QueryWrapper and, TablePermissionService tablePermissionService, OwnerTypeEnum ownerType) {
        QueryCondition condition = QueryCondition.createEmpty();

        // 默认查询个人/授权/本租户数据
        if (null == ownerType) {
            ownerType = OwnerTypeEnum.PERSONAL_GRANT_TENANT;
        }
        switch (ownerType) {
            // 官方
            case OFFICIAL:
                // 查询官方数据
                if (null != tablePermissionService && tablePermissionService.getOwnerType() != null) {
                    condition.or(tablePermissionService.getOwnerType().eq("official"));
                } else {
                    condition.or(QueryMethods.column("owner_type").eq("official"));
                }
                break;
            // 个人
            case PERSONAL:
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                break;
            //  授权
            case GRANT:
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                break;
            // 个人/授权
            case PERSONAL_GRANT:
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                break;
            //  个人/授权/本租户
            case PERSONAL_GRANT_TENANT:
                // 查询自己创建的知识库
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                // 如果是管理员，可以查询当前租户下的
                queryCurrentTenantData(condition, tablePermissionService);
                break;
            case ALL:
                // 查询官方数据
                if (null != tablePermissionService && tablePermissionService.getOwnerType() != null) {
                    condition.or(tablePermissionService.getOwnerType().eq("official"));
                } else {
                    condition.or(QueryMethods.column("owner_type").eq("official"));
                }
                // 查询自己创建的
                queryCreateUser(condition, tablePermissionService);
                // 查询授权的数据，查询授权的租户
                queryGrantData(condition, tablePermissionService);
                // 如果是管理员，可以查询当前租户下的
                queryCurrentTenantData(condition, tablePermissionService);
                break;
        }
        if (flexModel != null) {
            QueryWrapper wrapper = flexModel.getWrapperWg();
            if (null != wrapper) {
                wrapper.and(addd -> {
                    addd.or(condition);
                });
            }
        } else {
            and.and(condition);
        }
    }

    /**
     * 查询自己创建的
     */
    private static void queryCreateUser(QueryCondition condition, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (null != condition) {
            if (null != tablePermissionService && null != tablePermissionService.getCreateUser()) {
                condition.or(tablePermissionService.getCreateUser().eq(tokenOauthUserInfo.getAccountName()));
            } else {
                condition.or(QueryMethods.column("create_user").eq(tokenOauthUserInfo.getAccountName()));
            }
        }
    }

    /**
     * 查询超管创建的
     */
    private static void querySuperAdmin(QueryCondition condition, List<String> superAdmin) {
        if (null != condition) {
            if (CollectionUtil.isNotEmpty(superAdmin)) {
                condition.or(QueryMethods.column("create_user").in(superAdmin));
            }
        }
    }

    /**
     * 查询授权的数据
     */
    private static void queryGrantData(QueryCondition condition, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        String tenantId = "-1";
        if (StringUtils.isNotBlank(tokenOauthUserInfo.getTenantId())) {
            tenantId = tokenOauthUserInfo.getTenantId();
        }
        if (null != tablePermissionService && StringUtils.isNotBlank(tablePermissionService.getDataType()) && null != tablePermissionService.getDataId()) {
            // 查询授权的数据，查询授权的用户
            QueryCondition existsUser = QueryMethods.exists(QueryMethods.selectOne()
                    .from(GRANT_DATA)
                    .where(GRANT_DATA.DATA_TYPE.eq(tablePermissionService.getDataType()))
                    .and(GRANT_DATA.TARGET_TYPE.eq(MybatisFiledConstant.USER))
                    .and(GRANT_DATA.TARGET_ID.eq(tokenOauthUserInfo.getId()))
                    .and(GRANT_DATA.DATA_ID.eq(tablePermissionService.getDataId()))
            );

            // 查询授权的数据，查询授权的租户
            QueryCondition existsTenant = QueryMethods.exists(QueryMethods.selectOne()
                    .from(GRANT_DATA)
                    .where(GRANT_DATA.DATA_TYPE.eq(tablePermissionService.getDataType()))
                    .and(GRANT_DATA.TARGET_TYPE.eq(MybatisFiledConstant.TENANT))
                    .and(GRANT_DATA.TARGET_ID.eq(tenantId))
                    .and(GRANT_DATA.DATA_ID.eq(tablePermissionService.getDataId()))
            );
            if (null != condition) {
                condition.or(existsUser);
                condition.or(existsTenant);
            }
        }
    }

    /**
     * 管理员查询租户下的并且发布到上商店的数据
     */
    public static void queryTenantData(QueryCondition condition, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.NORMAL_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            return;
        }
        String tenantId = "-1";
        if (StringUtils.isNotBlank(tokenOauthUserInfo.getTenantId())) {
            tenantId = tokenOauthUserInfo.getTenantId();
        }
        // 如果是管理员，可以查询当前租户下的数据(如果有已上架的应用，同一个租户都可以看到)
        if (null != condition) {
            if (PowerTypeEnum.NORMAL_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
                if (null != tablePermissionService) {
                    publishAppStoreCondition(condition, tablePermissionService, tenantId, false);
                } else {
                    condition.or(QueryMethods.column("tenant_id").eq(tenantId));
                }
            } else if (PowerTypeEnum.NORMAL_USER.getCode().equals(tokenOauthUserInfo.getPowerType())) {
                if (null != tablePermissionService) {
                    publishAppStoreCondition(condition, tablePermissionService, tenantId, true);
                }
            }
        }
    }

    /**
     * isNeedPublish: 是否需要商家条件
     * 应用商店上架的数据的展示条件
     */
    public static void publishAppStoreCondition(QueryCondition condition, TablePermissionService tablePermissionService, String tenantId, boolean isNeedPublish) {
        QueryColumn publishAppStore = tablePermissionService.getPublishAppStore();
        QueryCondition tenantCondition = QueryCondition.createEmpty();
        if (null != publishAppStore) {
            // 如果有上架字段，则可以看到当前租户下已上架的数据
            if (isNeedPublish) {
                tenantCondition.and(publishAppStore.eq(YesNoEnum.YES.getCode()));
            }
            condition.or(tenantCondition.and(tablePermissionService.getTenantId().eq(tenantId)));
        }
    }

    /**
     * 查询租户下的数据
     */
    public static void queryCurrentTenantData(QueryCondition condition, TablePermissionService tablePermissionService) {
        TokenUser tokenOauthUserInfo = AppContextHolder.getTokenUserInfo();
        if (!PowerTypeEnum.NORMAL_ADMIN.getCode().equals(tokenOauthUserInfo.getPowerType())) {
            return;
        }
        String tenantId = "-1";
        if (StringUtils.isNotBlank(tokenOauthUserInfo.getTenantId())) {
            tenantId = tokenOauthUserInfo.getTenantId();
        }
        // 非超级管理员均可以查询当前租户下的知识库
        if (null != condition) {
            if (null != tablePermissionService) {
                condition.or(tablePermissionService.getTenantId().eq(tenantId));
            } else {
                condition.or(QueryMethods.column("tenant_id").eq(tenantId));
            }
        }
    }
}
