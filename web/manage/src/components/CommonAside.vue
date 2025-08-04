<template>
  <el-menu
    :default-active="$route.path"
    class="el-menu-vertical-demo no-scrollbar"
    text-color="#383D47"
    active-text-color="#1C50FD"
    @select="handleSelect"
    :collapse="isFold"
  >
    <template
      v-for="(menu, index) in parsedPermission"
      v-if="[0].includes(menu.isHidden) && menu.menuType === 1"
    >
      <el-submenu
        :index="menu.menuCode"
        :key="menu.menuCode"
        v-if="InterceptMenu(menu.children)"
        :class="[isFold ? 'biaoji' : '', index == 1? 'first' : '', menu.menuCode]"
      >
        <template slot="title">
          <div
            class="fold-line"
            :class="[$route.path.includes(menu.menuUrl) && isFold ? 'bg' : '']"
          >
            <svg class="icon" aria-hidden="true">
                <use :xlink:href="$route.path == menu.menuUrl ? iconActiveObj[menu.menuCode] : iconObj[menu.menuCode]"></use>
            </svg>
            <span class="txt" :style="{'font-weight': $route.path == menu.menuUrl ? '600':'400'}">{{ $t(menu.menuCode)}}</span>
          </div>
        </template>
        <el-menu-item
          v-for="child in menu.children"
          :index="child.menuCode"
          :key="child.menuCode"
          v-if="child.menuType === 1"
          :class="child.menuCode"
        >
          <span :style="{'font-weight': $route.path == child.menuUrl ? '600':'400'}">{{ $t(child.menuCode)}}</span>
        </el-menu-item>
      </el-submenu>
      <el-menu-item
        v-else
        :class="[isFold ? 'biaoji' : '', index == 1 ? 'first' : '', menu.menuCode]"
        :index="menu.menuCode"
      >
        <div
          class="fold-line"
          :class="[$route.path == menu.menuUrl && isFold ? 'bg' : '']"
        >
        <el-tooltip v-if="isFold" class="item" effect="dark" :content="$t(menu.menuCode)" placement="right">
            <svg class="icon" aria-hidden="true">
                <use :xlink:href="$route.path == menu.menuUrl ? iconActiveObj[menu.menuCode] : iconObj[menu.menuCode]"></use>
            </svg>
        </el-tooltip>
        <svg v-else class="icon" aria-hidden="true">
            <use :xlink:href="$route.path == menu.menuUrl ? iconActiveObj[menu.menuCode] : iconObj[menu.menuCode]"></use>
        </svg>
          <span class="txt" :style="{'font-weight': $route.path == menu.menuUrl ? '600':'400'}">{{ $t(menu.menuCode)}}</span>
        </div>
      </el-menu-item>
    </template>
  </el-menu>
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
      iconActiveObj: {
        'manual': '#icon-shiyongshouce1xuanzhong',
        'storemanage': '#icon-shangdian1xuanzhong',
        'appmanage': '#icon-xiangmu1xuanzhong',
        'kbm': '#icon-zhishi1xuanzhong',
        'modelmanagement': '#icon-moxing1xuanzhong',
        'sceneManage': '#icon-changjing1xuanzhong',
        'operationsManagement': '#icon-yunying1xuanzhong',
        'toolManager': '#icon-gongju1xuanzhong',
        'systemManage': '#icon-xitong1xuanzhong',
        'workflow': '#icon-gongzuoliu1xuanzhong',
        'service': '#icon-gongzuoliu1xuanzhong',
        'keyManage': '#icon-miyueguanli',
		    'mcp': '#icon-MCPfuwuxuanzhong',
			'dataSj': '#icon-MCPfuwuxuanzhong',
        'effectEvaluation': '#icon-zhinengtipingcexuanzhong'
      },
      iconObj: {
        'manual': '#icon-shiyongshouce',
        'storemanage': '#icon-shangdian',
        'appmanage': '#icon-xiangmu1',
        'kbm': '#icon-zhishi1',
        'modelmanagement': '#icon-moxing1',
        'sceneManage': '#icon-changjing1',
        'operationsManagement': '#icon-yunying1',
        'toolManager': '#icon-gongju1',
        'systemManage': '#icon-xitong1',
        'workflow': '#icon-gongzuoliu1',
        'service': '#icon-gongzuoliu1',
        'keyManage': '#icon-a-miyueguanli2',
		    'mcp': '#icon-MCPfuwu',
			'dataSj': '#icon-MCPfuwu',
        'effectEvaluation': '#icon-zhinengtipingce'
      },
    };
  },
  methods: {
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
      this.$router.push({ name: menuName }).catch(()=>{});
    },
  },
};
</script>

<style lang="scss" scoped>
.el-menu {
  height: calc(100% - 64px - 136px);
  width: 100%;
  border: none;
  background: transparent;
  overflow-x: hidden;
  padding: 0 12px;
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
    
  }
  .el-menu-item{
    position:relative;
    height: 52px;
    padding-top:4px;
  }
    ::v-deep .el-menu-item:nth-child(2){
      margin-top:16px;
    }
  ::v-deep .el-submenu .el-submenu__title{
    height: 52px;
    padding-top:4px;
  }
  ::v-deep .el-submenu:nth-child(9){
    margin-top:16px;
  }
  ::v-deep .el-submenu .el-menu-item{
    margin-top:0;
  }
  ::v-deep .el-menu-item:nth-child(8){
    position: relative;
      padding-bottom: 4px;
      margin-bottom: 8px;
      border:0;
      padding-top:0;
      &::after{
        content:'';
        position: absolute;
        bottom: -8px; /* 下边框距离元素的距离 */
        left: 0;
        right: 0;
        height: 1px; /* 下边框高度 */
        background-color: rgba(0,0,0,0.12); /* 下边框颜色 */
      }
  }
  .fold-line {
    position: absolute;
    left:0;
    top:0;
    width: 48px;
    height: 48px;
    line-height: 48px;
    text-align: center;
    border-radius: 4px;
    .icon{
      margin-left:16px;
      margin-right:12px;
      width: 20px;
      height: 20px;
    }
  }
  .bg {
    background: linear-gradient(
      270deg,
      rgba(142, 101, 255, 0.1) 0%,
      rgba(28, 80, 253, 0.1) 100%
    );
  }
}
.icon {
    margin-right: 14px;
    font-size: 16px;
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
.el-menu-item {
     &:hover {
        .txt{
            font-weight: 500;
        }
     }
     &.is-active {
      color: #409EFF;
      background-color: #ecf5ff;
    }
    &.manual{
      position: fixed;
      bottom: 52px;
      width: 216px;
    }

}
.first {
  // border-bottom: 1px solid rgba(0,0,0,0.12);
  position: relative;
      padding-bottom: 4px;
      margin-bottom: 8px;
      border:0;
      padding-top:0;
      &::after{
        content:'';
        position: absolute;
        bottom: -8px; /* 下边框距离元素的距离 */
        left: 0;
        right: 0;
        height: 1px; /* 下边框高度 */
        background-color: rgba(0,0,0,0.12); /* 下边框颜色 */
      }
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
.no-scrollbar {
    overflow: auto; /* 确保内容仍然可以滚动 */
    -ms-overflow-style: none;  /* Internet Explorer 10+ */
    scrollbar-width: none;     /* Firefox */
}

/* 适用于 Chrome, Safari, Edge */
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
</style>