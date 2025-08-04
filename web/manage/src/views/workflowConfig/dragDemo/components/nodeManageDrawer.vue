<template>
  <div class="Ec-x6-icon" v-if="visibleNode">
    <!--节点管理 -->
    <el-drawer title="" :visible.sync="visibleNode" :direction="directionRtl" append-to-body size="560px" @close="closeDrawerNode"
      :show-close="false">
      <div class="drawer-conter">
        <div class="drawer-header">
          <h3>{{ $t("customLabelNode") }}</h3>
          <!-- <h3>{{ $t("node") }}{{ $t('manage') }}</h3> -->
          <iconpark-icon name="close-large-fill" color="#1D2129" size="20" style="cursor: pointer; padding: 8px;"
            @click="closeDrawerNode"></iconpark-icon>
        </div>
        <div class="title">
          <h1 class="title-left">{{ $t("currentlyOnlySupportsManagingCustomNodes") }}</h1>
          <div class="title-right" @click="handleAddNode('create')">
            <iconpark-icon name="add-line" color="#1D2129" size="14"
              style="margin-left:13px; margin-right:8px;"></iconpark-icon>
            <span>{{ $t("newSlice") }}{{ $t("node") }}</span>
          </div>
        </div>
        <ul class="node-list">
          <li v-for="(item, index) in NodeList" :key="index">
            <h1 class="node-left">
              <iconpark-icon name="apps-fill" color="#ffffff" size="18"
                style="cursor: pointer; padding: 7px;"></iconpark-icon>
            </h1>
            <div class="introduce">
              <h3>{{ item.nodeName}}</h3>
              <h4>{{ item.nodeDesc }}</h4>
            </div>
            <div class="node-icon">
              <el-switch style="margin-right:12px;vertical-align:baseline;" 
                v-model="item.startStatus" 
                :active-value = '0' 
                :inactive-value = '1'
                active-color="#1747E5" 
                inactive-color="#CED4E0" 
                @change="handleStatusChange(item)">
              </el-switch>
              <iconpark-icon name="draft-line" color="#1D2129" size="18" style="cursor: pointer; padding: 7px;"
                @click="handleEditNode(item,'edit')"></iconpark-icon>
              <iconpark-icon name="delete-bin-4-line" color="#1D2129" size="18" style="cursor: pointer; padding: 7px;"
                @click="handleDeleteNode(item)"></iconpark-icon>
            </div>
          </li>
        </ul>
      </div>
      <!-- <div class="node-but">
        <el-button @click="closeDrawerNode">{{ $t("cancel") }}</el-button>
        <el-button type="primary" @click="sureDrawerNode">{{ $t("confirm") }}</el-button>
      </div> -->
    </el-drawer>


    <!-- 添加$编辑自定义节点 -->
    <el-dialog :visible.sync="dialogVisible" width="100%" fullscreen :show-close="false" class="flexDialog"
      destroy-on-close :close-on-click-modal="false" :close-on-press-escape="false" append-to-body>
      <div class="headBar">
        <div class="leftSlide">
          <img src="@/assets/images/arrow-go-back-fill.svg" @click="closeDialog" />
          <div class="titleIcon">
            <p>{{ typeNode === 1 ? '添加' : '编辑' }}{{ $t('customLabelNode') }}</p>
          </div>
        </div>
        <div class="rightSlide">
          <el-button class="btns" v-permission="'issue'" @click="fabuDialog">
            <img src="@/assets/images/send-plane-fill.svg" />
            <span>{{ $t('save') }}</span>
          </el-button>
        </div>
      </div>
      <CodeSetting v-if="visibleCodeSetting" ref="child" :typeNode="typeNode" :appNodeForm="appNodeForm"></CodeSetting>
    </el-dialog>
  </div>
</template>

<script>
import { nodeList, deleteNode, addNode, queryNode, updateNode } from "@/api/workflow";
import CodeSetting from "./codeSetting.vue"
export default {
  name: "nodeManageDrawer",
  components: {
    CodeSetting
  },
  data() {
    return {
      dialogVisible: false,
      direction: "ltr",
      visibleCodeSetting: false,
      visibleNode: false,
      directionRtl: "rtl",
      NodeList: [],
      typeNode: 1,
      appNodeForm:{}
    };
  },
  computed:{},
  mounted() {
    this.$EventBus.$on("showVisibleNode", () => {
      this.openNodeManage()
    })
    // this.getNodeList()
  },
  methods: {
    getNodeList() {
      nodeList('NODE_MANAGE').then((res) => {
        if (res.code == "000000") {
          this.NodeList = res.data || [];
        } else {
          this.NodeList = [];
        }
      });
    },
    handleStatusChange(item) {
      updateNode({
          startStatus: item.startStatus,
          id:item.id,
          nodeName:item.nodeName
        });
          setTimeout(() => {
          this.getNodeList()
        }, 1000) 
      },
    sureDrawerNode() {
      this.visibleNode = false;
      this.$EventBus.$emit("showNodeList");
    },
    //保存
    fabuDialog() {
      const params = this.$refs.child.appForm;
      const apiCall = this.typeNode === 1 ? addNode(params) : updateNode(params);
        apiCall.then((res) => {
          if (res.code == "000000") {
            console.log("操作成功", res);
            this.dialogVisible = false;
            // 延迟1秒刷新列表，确保数据更新
            setTimeout(() => {
              this.getNodeList();
            }, 1000);
          }
        }).catch(error => {
          console.error("操作失败", error);
          this.$message.error('操作失败，请重试');
        });
    },
    //节点管理
    openNodeManage() {
      this.getNodeList()
      this.visibleNode = true;
    },
    closeDrawerNode() {
      this.visibleNode = false;
      this.$EventBus.$emit("showNodeList");
      nodeList('CUSTOM').then((res) => {
        if (res.code == "000000") {
          this.NodeList = res.data || [];
        } else {
          this.NodeList = [];
        }
        });
    },
    //添加
    handleAddNode(mode) {
      if (mode === 'create') {
        this.typeNode = 1;
        this.dialogVisible = true;
        this.visibleCodeSetting = true
      }
    },
    //编辑
    handleEditNode(item,mode) {
      if (mode === 'edit') {
        this.typeNode = 2
        this.dialogVisible = true;
        this.visibleCodeSetting = true
        queryNode(item.id).then((res) => {
          if (res.code == "000000") {
            this.appNodeForm = Object.assign({}, res.data);
          }
        })
      }
    },
    handleDeleteNode(item) {
      this.$confirm("请确认是否删除", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        confirmButtonClass: "confirm-ok",
        cancelButtonClass: "confirm-cancel",
      }).then(async () => {
          deleteNode(item.id).then((res) => {
          if (res.code === "000000") {
            this.$message.success('删除成功')
            this.getNodeList();
          } 
        });
      })
    },
    closeDialog() {
      this.dialogVisible = false
    },
  },
};
</script>
<style lang="scss" scoped>
// .Ec-x6-icon ::v-deep .el-drawer__wrapper {
//   border-radius: 0;
//   left: 66px;
// }

// .Ec-x6-icon ::v-deep .el-drawer__body {
//   overflow-y: auto;
//   overflow-x: hidden;
//   position: relative;
// }
.Ec-x6-icon {
  position: relative;
}

.Ec-x6-icon ::v-deep .el-drawer {
  padding: 24px;
}

::v-deep .el-drawer__header {
  padding: 0;
  margin-bottom: 0;
}

.drawer-header {
  position: relative;
  display: flex;

  h3 {
    font-size: 18px;
    color: #383D47;
  }

  .el-icon-d-arrow-left {
    position: absolute;
    right: 0;
    top: 2px;
    cursor: pointer;
  }
}

::v-deep .el-drawer__header>:first-child {
  font-family: MiSans, MiSans;
  font-weight: 500;
  font-size: 20px;
  color: #1D2129;
  line-height: 32px;
  text-align: left;
  font-style: normal;
  text-transform: none;
}

::v-deep .el-drawer__close-btn {
  font-size: 30px;
}

::v-deep .el-drawer {
  padding: 32px;
}

.drawer-conter {
  .drawer-header {
    display: flex;
    justify-content: space-between;

    h3 {
      height: 32px;
      font-family: MiSans, MiSans;
      font-weight: 500;
      font-size: 20px;
      color: #1D2129;
      line-height: 32px;
      text-align: left;
      font-style: normal;
      text-transform: none;
    }
  }

  .title {
    margin-top: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;

    .title-left {
      font-family: MiSans, MiSans;
      font-weight: 400;
      font-size: 14px;
      color: #252E40;
      line-height: 20px;
      text-align: justify;
      font-style: normal;
    }

    .title-right {
      width: 104px;
      height: 32px;
      border-radius: 4px;
      border: 1px solid #E7E7E7;
      line-height: 32px;

      span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 14px;
        color: #1D2129;
        line-height: 20px;
        text-align: left;
        font-style: normal;
      }
    }
  }

  .node-list {
    margin-top: 16px;

    li {
      height: 56px;
      border-radius: 2px;
      border: 1px solid #E7E7E7;
      display: flex;
      padding: 10px;
      position: relative;
      margin-bottom: 12px;

      .node-left {
        width: 32px;
        height: 32px;
        background: #6970E2;
        border-radius: 2px;
        margin: 2px;
      }

      .introduce {
        margin-left: 10px;
        h3 {
          height: 20px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 14px;
          color: #1D2129;
          line-height: 20px;
          text-align: left;
          font-style: normal;
        }

        h4 {
          height: 16px;
          font-family: MiSans, MiSans;
          font-weight: 400;
          font-size: 12px;
          color: #86909C;
          line-height: 16px;
          text-align: left;
          font-style: normal;
          width: 300px;
          white-space: nowrap;    /* 禁止文本换行 */
          overflow: hidden;       /* 隐藏超出容器的部分 */
          text-overflow: ellipsis;
        }
      }

      .node-icon {
        position: absolute;
        top: 12px;
        right: 12px;
      }
    }
  }
}

.node-but {
  position: absolute;
  right: 32px;
  bottom: 32px;

  .el-icon-edit-outline {
    font-size: 14px;
  }
}

::v-deep .flexDialog {
  background-color: #f2f5fa;
  padding: 0 33px 33px;
}

.headBar {
  border-bottom: 1px solid #E5E6EA;
  background: #ffffff;
  padding: 16px 32px 16px 32px;
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  width: 100%;
  position: absolute;
  top: 0;
  right: 0;

  img {
    width: 15px;
    height: 15px;
  }

  .leftSlide {
    display: flex;
    justify-content: space-between;
    align-items: center;

    >img {
      margin-right: 16px;
      cursor: pointer;
    }

    .titleIcon {
      p:first-child {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 20px;
        color: #1D2129;
        ;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        margin-bottom: 6px;
      }

      p:last-child {
        font-family: MiSans, MiSans;
        font-weight: 500;
        font-size: 18px;
        color: #1D2129;
        line-height: 18px;
        text-align: left;
        font-style: normal;
      }
    }
  }

  .rightSlide {
    display: flex;
    justify-content: space-between;

    .preview {
      line-height: 36px;
      margin-right: 28px;
      cursor: pointer;

      >span {
        font-family: MiSans, MiSans;
        font-weight: 400;
        font-size: 16px;
        color: #3666ea;
        line-height: 24px;
        text-align: left;
        font-style: normal;
        text-transform: none;
      }

      >img {
        margin-right: 5px;
      }

      >span,
      >img {
        vertical-align: middle;
      }
    }

    .btn {
      height: 40px;
      color: #3666ea;
      border: 1px solid #3666ea;
      border-radius: 4px;

      img {
        margin-right: 5px;
      }

      img,
      span {
        vertical-align: middle;
      }
    }

    .btns {
      color: #fff;
      height: 40px;
      background: linear-gradient(270deg, #8e65ff 0%, #1c50fd 100%);
      border-radius: 4px;

      img {
        margin-right: 5px;
      }

      img,
      span {
        vertical-align: middle;
      }
    }
  }
}
</style>
