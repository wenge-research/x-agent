<template>

  <div class="common-aside" >

    <div class="common-aside-open" v-if="false" >

      <div class="common-aside-open-item" 
        v-for="menu in parsedPermission" 
        :key="menu.menuCode"
        @mouseover="showSubMenu(menu.children)"
        @mouseleave="hideSubMenu"
        >
        <div>
          <iconpark-icon :name="menu.icon" :title="menu.menuCode"></iconpark-icon>
          <span>{{ $t(menu.menuCode) }}</span>
        </div>
        <div v-show="menu?.children?.length" >
          <iconpark-icon name="arrow-right-s-line"></iconpark-icon>
        </div>
      </div>

      <div class="common-aside-open-submenu" v-if="hoveredChildren.length">
        <div
          class="common-aside-open-submenu-item"
          v-for="child in hoveredChildren"
          :key="child.menuCode"
        >
          <span>·</span>
          <span>{{ $t(child.menuCode) }}</span>
        </div>
      </div>
    </div>



    <!-- 没完成之前先展示老内容 -->
    <el-menu
      :default-active="$route.path"
      class="el-menu-vertical-demo"
      text-color="#383D47"
      active-text-color="#1C50FD"
      @select="handleSelect"
      :collapse="isFold"
      v-if="true"
    >
      <template
        v-for="menu in parsedPermission"
        v-if="[0].includes(menu.isHidden) && menu.menuType === 1"
      >
        <el-submenu
          :index="menu.menuCode"
          :key="menu.menuCode"
          v-if="InterceptMenu(menu.children)"
          :class="[isFold ? 'biaoji' : '']"
        >
          <template slot="title">
            <div
              class="fold-line"
              :class="[$route.path.includes(menu.menuUrl) && isFold ? 'bg' : '']"
            >
              <!-- 应用管理 -->
              <iconpark-icon
                v-if="menu.menuCode == 'appmanage'"
                name="apps-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('appmanage')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'kbm'"
                name="book-marked-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('kbm')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'modelmanagement'"
                name="box-3-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('modelmanagement')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'sceneManage'"
                name="color-filter-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('sceneManage')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'operationsManagement'"
                name="slideshow-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('operationsManagement')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'toolManager'"
                name="briefcase-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('toolManager')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'systemManage'"
                name="settings-fill"
                :class="[isFold ? 'mr0' : '']"
                title="$t('systemManage')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'chatTemplate'"
                name="device-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('chatTemplate')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'workflow'"
                name="git-merge-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('workflowManagement')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <iconpark-icon
                v-else-if="menu.menuCode == 'matterManage'"
                name="archive-fill"
                :class="[isFold ? 'mr0' : '']"
                :title="$t('matterManage')"
                :color="
                  $route.path.includes(menu.menuUrl) && isFold
                    ? 'rgba(28, 80, 253, 1)'
                    : 'rgb(132,133,135)'
                "
              ></iconpark-icon>
              <span class="txt">{{ $t(menu.menuCode)}}</span>
            </div>
          </template>
          <el-menu-item
            v-for="child in menu.children"
            :index="child.menuCode"
            :key="child.menuCode"
            v-if="child.menuType === 1"
          >
            {{$t(child.menuCode)}}
          </el-menu-item>
        </el-submenu>
        <el-menu-item
          v-else
          :class="[isFold ? 'biaoji' : '']"
          :index="menu.menuCode"
        >
          <!-- <i :class="'el-icon-' + menu.icon"></i> -->
          <div
            class="fold-line"
            :class="[$route.path == menu.menuUrl && isFold ? 'bg' : '']"
          >
            <iconpark-icon
              v-if="menu.menuCode == 'appmanage'"
              name="apps-fill"
              :title="$t('appmanage')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'kbm'"
              name="book-marked-fill"
              :title="$t('kbm')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'operationsManagement'"
              name="slideshow-fill"
              :title="$t('operationsManagement')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'largemodelmanagement'"
              name="box-3-fill"
              :title="$t('largemodelmanagement')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'toolManager'"
              name="briefcase-fill"
              :title="$t('toolManager')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'systemManage'"
              name="settings-fill"
              :title="$t('systemManage')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'chatTemplate'"
              name="device-fill"
              :title="$t('chatTemplate')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'workflow'"
              name="git-merge-fill"
              :title="$t('workflowManagement')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <iconpark-icon
              v-else-if="menu.menuCode == 'matterManage'"
              name="archive-fill"
              :title="$t('matterManage')"
              :color="
                $route.path == menu.menuUrl && isFold
                  ? 'rgba(28, 80, 253, 1)'
                  : 'rgb(132,133,135)'
              "
              :class="[isFold ? 'mr0' : '']"
            ></iconpark-icon>
            <span class="txt">{{ $t(menu.menuCode)}}</span>
          </div>
        </el-menu-item>
      </template>
    </el-menu>
  </div>
</template>

<script>
export default {
  computed: {
    parsedPermission() {
      const permission = sessionStorage.getItem("permission") ;
      if (permission) {
        try {
           let permissions = JSON.parse(permission) || [];
           // 获取属性clientType为空或者为PC的菜单
            permissions = this.filterPermissions(permissions);
            return permissions;
        } catch (error) {
          return [];
        }
      }
      return [];
    },
  },
  data() {
    return {
      isFold: false, // 是否折叠
      hoveredChildren: [], // 悬浮的子菜单
    };
  },
  methods: {
    showSubMenu(children) {
      this.hoveredChildren = children || [];
      console.log(this.parsedPermission,'菜单的数据');
    },
    hideSubMenu() {
      this.hoveredChildren = [];
    },
    filterPermissions(permissions) {
      return permissions
          .filter((item) => !item.clientType || item.clientType === "PC")
          .filter((item) => item.isHidden !== 1)
          .map((item) => {
            if (item.children) {
              item.children = this.filterPermissions(item.children);
            }
            return item;
          });
    },
    InterceptMenu(children) {
      if (children) {
        const hasMenuType = children.some((item) => item.menuType === 1);
        return hasMenuType;
      } else {
        return false;
      }
    },
    handleSelect(menuName) {
      this.$router.push({ name: menuName });
    },
  },
};
</script>

<style lang="scss" scoped>
.common-aside {
  height: calc(100% - 64px - 96px);
}
.common-aside-open {
  height: 100%;
  width: 100%;
  background: #f0f5fc;
  border: none;
  background: transparent;
  overflow-x: hidden;
  .common-aside-open-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 48px;
    line-height: 48px;
    padding: 0 20px;
    cursor: pointer;
    user-select: none;
    &:hover {
      background: #e6ebf3;
    }
  }
  .common-aside-open-submenu {
    box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.1);
    background: #fff;
    position: fixed;
    left: 250px;
    top: 100px;
    z-index: 99;
    width: 192px;
    .common-aside-open-submenu-item{
      height: 48px;
    }
  }


}
.el-menu {
  height: 100%;
  width: 100%;
  border: none;
  background: transparent;
  overflow-x: hidden;
  ::v-deep .el-menu--inline {
    padding-left: 20px !important;
  }
  .el-submenu .el-menu-item {
    position: relative;
    &::before {
      content: "";
      position: absolute;
      width: 4px;
      height: 4px;
      background: #bdbdbd;
      border-radius: 100%;
      left: 28px;
      top: 24px;
    }
    &.is-active {
      &::before {
        background: #409EFF;
      }
    }
  }
  .fold-line {
    width: 48px;
    height: 48px;
    line-height: 48px;
    text-align: center;
    border-radius: 4px;
  }
  .bg {
    background: linear-gradient(
      270deg,
      rgba(142, 101, 255, 0.1) 0%,
      rgba(28, 80, 253, 0.1) 100%
    );
  }
}

iconpark-icon {
  margin-right: 14px;
  font-size: 16px;
}
.mr0 {
  margin-right: 0;
}

.txt {
  height: 20px;
  font-family: MiSans, MiSans;
  font-weight: 400;
  font-size: 16px;
  color: #383d47;
}

.biaoji {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0!important;
  ::v-deep .el-submenu__icon-arrow {
    display: none;
  }
}
::v-deep .el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 240px;
  min-height: 400px;
}

</style>