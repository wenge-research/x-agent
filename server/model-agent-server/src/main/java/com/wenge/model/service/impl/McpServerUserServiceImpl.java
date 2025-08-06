package com.wenge.model.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.wenge.model.entity.McpServer;
import com.wenge.model.entity.McpServerUser;
import com.wenge.model.mapper.McpServerMapper;
import com.wenge.model.mapper.McpServerUserMapper;
import com.wenge.model.service.McpServerUserService;
import com.wenge.oauth.entity.TokenUser;
import com.wenge.oauth.holder.AppContextHolder;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.mybatisflex.core.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.wenge.model.entity.table.McpServerUserTableDef.MCP_SERVER_USER;


@Service
@Slf4j
public class McpServerUserServiceImpl extends ServiceImpl<McpServerUserMapper, McpServerUser> implements McpServerUserService {

    @Override
    public Result updateStatus(McpServerUser mcpServerUser) {
        if (StringUtils.isBlank(mcpServerUser.getMcpId())) {
            return Result.fail("服务id不能为空");
        }
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        McpServerUser mcpServerUserOld = Wrappers.of(mapper)
                .where(MCP_SERVER_USER.MCP_ID.eq(mcpServerUser.getMcpId())
                .and(MCP_SERVER_USER.USER_ID.eq(tokenUserInfo.getId())))
                .one();
        if (ObjectUtils.isNotEmpty(mcpServerUserOld)) {
            mcpServerUser.setStatus(mcpServerUser.getStatus());
            mcpServerUser.setId(mcpServerUserOld.getId());
            this.updateById(mcpServerUser);
        } else {
            mcpServerUser.setUserId(String.valueOf(tokenUserInfo.getId()));
            this.save(mcpServerUser);
        }
        return Result.success(mcpServerUser);
    }

    @Override
    public McpServerUser queryMcpServerUser(String mcpId) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        McpServerUser mcpServerUserOld = Wrappers.of(mapper)
                .where(MCP_SERVER_USER.MCP_ID.eq(mcpId)
                        .and(MCP_SERVER_USER.USER_ID.eq(tokenUserInfo.getId())))
                .one();
        return mcpServerUserOld;
    }

    @Override
    public List<McpServerUser> myMcpServerUserList() {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        return Wrappers.of(mapper)
                .where(MCP_SERVER_USER.USER_ID.eq(tokenUserInfo.getId()))
                .and(MCP_SERVER_USER.STATUS.eq(1))
                .list();
    }

    /**
     *
     * @param permisson 是否根据当前用户查询mcpServerUser表
     * @param status 查询的启用禁止条件，1启用，0禁止
     * @return
     */
    @Override
    public List<McpServerUser> myCheckMcpServerUserList(Boolean permisson, Integer status) {
        TokenUser tokenUserInfo = AppContextHolder.getTokenUserInfo();
        return Wrappers.of(mapper)
                .where(permisson,MCP_SERVER_USER.USER_ID.eq(tokenUserInfo.getId()))
                .and(MCP_SERVER_USER.STATUS.eq(status))
                .list();
    }

}
