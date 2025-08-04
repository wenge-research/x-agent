<template>
  <div class="kbm-view">
    <transition name="el-fade-in-linear">
      <KbmList ref="KbmList" v-if="showView === 'kbmList'" @change-view="changeKbmView" />
    </transition>
    <transition name="el-fade-in-linear">
      <KbmCreate ref="KbmCreate" v-if="showView === 'kbmCreate'" @change-view="changeKbmView" :rowData.sync="rowData" :isAppManage="false" :type="type" />
    </transition>
  </div>
</template>

<script>
import KbmList from '@/views/keyManage/component/KbmList.vue'
import KbmCreate from '@/views/keyManage/component/KbmCreate.vue'
export default {
  components: {
    KbmList,
    KbmCreate
  },
  data() {
    return {
      showView: 'kbmList', // kbmList kbmCreate // SliceInfo
      rowData: {}
    }
  },
  mounted() {},
  methods: {
    changeKbmView(name, params) {
      console.log(name, params)
      this.showView = name
      if (params) {
        if (params.type === 'addKnowledge') {
          this.type = 'add'
          this.rowData = {}
        }
        if (params.type === 'setKnowledge') {
          this.type = 'set'
          this.rowData = params.data
        }
        if (params.type === 'viewKnowledge') {
          this.type = 'view'
          this.rowData = params.data
        }
      }
    }
  },
}

</script>

<style lang="scss" scoped>
.kbm-view {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  position: relative;
}
</style>
<style scoped>
/deep/ .el-drawer__header {
  margin-bottom: 10px;
  font-weight: bold;
}

/deep/ .el-dialog__body {
  padding: 10px 20px;
}

/deep/ .el-drawer__body {
  flex: 1;
  overflow: auto;
}

/deep/ .el-dialog__body {
  display: flex;
  justify-content: center;
  flex-direction: column;
}

/deep/ .el-upload {
  width: 100%;
}

/deep/ .el-date-editor {
  width: 100%;
}

/deep/ .el-upload-dragger {
  width: 100%;
}

/deep/ .el-cascader {
  width: 100%;
}

/deep/ .el-tree {
  width: 100%;
  height: 100%;
}
</style>
