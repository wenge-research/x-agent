package com.wenge.oauth.service;

import com.mybatisflex.core.service.IService;
import com.wenge.oauth.dto.param.MenuHiddenParam;
import com.wenge.oauth.dto.param.MenuPageParam;
import com.wenge.oauth.dto.param.MenuUpdateStatusParam;
import com.wenge.oauth.entity.Menu;
import com.wg.appframe.core.bean.Result;
import com.wg.appframe.core.dto.params.EmptyParam;
import com.wg.appframe.core.dto.params.StringParam;

import java.util.List;

/**
 * Description: 菜单表服务类
 * @Author: CHENZHIWEI
 * Version: 1.0
 * Create Date Time: 2024-07-02 11:03:36
 *
 */
public interface MenuService extends IService<Menu> {

    Result addMenuList(List<Menu> menus);

    Result updateMenuList(List<Menu> menus);

    Result delMenuByMenuId(String menuId);

    Result addMenu(Menu menu);

    Result getMenuList(MenuPageParam param);

    Result updateMenu(Menu menu);

    Result deleteMenu(List<String> idList);

    Result<List<Menu>> getPermission(StringParam roleId);

    Result<List<Menu>> getUserPermission(StringParam userId);

    Result<List<Menu>> getAllMenuTree(EmptyParam param);

    Result<List<String>> getMenuIdListByRole(StringParam roleId);

    Result updateStatus(MenuUpdateStatusParam param);

    Result updateHidden(MenuHiddenParam param);

    List<Menu> getAdminPermission();
}