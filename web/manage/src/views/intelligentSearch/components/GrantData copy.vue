<script>
import {addGrantData, getGrantDataList, tenantList, userList} from "@/api/app";

export default {
  name: "GrantData",
  data() {
    return {
      grantType: 'user',
      userCondition:'',
      grantTypeOption: [
        {
          label: '授权给用户',
          value: 'user',
          id: '1'
        }, {
          label: '授权给租户',
          value: 'tenant',
          id: '2'
        }
      ],
      userCheckData:[],
      userTableDatas:[],
      grantData:{},
    }
  },
  props: {
    dataId: {
      type: String,
      request: true,
    },
    dataType: {
      type: String,
      request: true,
    }
  },
  methods:{
    confirmGrant() {
      let _this = this;
      if (!this.userCheckData || !this.userCheckData.length) {
        this.$confirm('没有选择数据，将会清空该数据的授权，是否继续?', this.$t('tips'), {
          confirmButtonText: this.$t('confirm'),
          cancelButtonText: this.$t('cancel'),
          type: 'warning'
        }).then(function () {
          _this.saveGrant()
          _this.cancelGrant()
        }).catch(function () {
          _this.$message({
            message: this.$t('cancelledOperation'),
            type: 'success'
          });
        });
      } else {
        this.saveGrant()
        this.cancelGrant()
      }
    },
    onchange(value){
      this.grantType = value;
      this.getData();
    },
    getData(condition) {
      switch (this.grantType) {
        case 'user':
          this.getUserLists(condition);
          break;
        case 'tenant':
          this.getTenantLists(condition);
          break;
      }
      this.getGrantDataList(this.dataId, this.dataType, this.grantType);
    },
    userInput(value) {
      this.getData(value)
    },
    userSelectionChange(val) {
      this.userCheckData = val;
    },
    saveGrant() {
      let targetIdList = []
      switch (this.grantType) {
        case 'user':
          targetIdList = this.userCheckData.map(item => item.id);
          break;
        case 'tenant':
          targetIdList = this.userCheckData.map(item => item.tenantId);
          break;
      }
      let param = {
        // dataId: this.grantData.applicationId,
        // dataType: 'app',
        dataId: this.dataId,
        dataType: this.dataType,
        targetType: this.grantType,
        targetIdList: targetIdList,
      };
      addGrantData(param)
          .then(res => {
            if ('000000' === res.code) {
              this.$message({
                message: this.$t('successed'),
                type: 'success'
              });
              this.grantVisible = false;
            } else {
              this.$message({
                message: res.msg,
                type: 'error'
              });
            }

          });
    },
    cancelGrant() {
      this.$emit('cancelGrant');
    },
    getUserLists(condition) {
      userList({
        pageNo:1,
        pageSize:99999,
        condition: condition,
      }).then(res=>{
        if(res.code=='000000'){
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records||[];
        }else{
          // this.totals = 0;
          this.userTableDatas = [];
        }
      })
    },
    getTenantLists(condition) {
      tenantList({
        pageNo:1,
        pageSize:99999,
        tenantName: condition,
      }).then(res=>{
        if(res.code=='000000'){
          // this.totals = res.data?.totalRow||0;
          this.userTableDatas = res.data.records||[];
        }else{
          // this.totals = 0;
          this.userTableDatas = [];
        }
      })
    },
    getGrantDataList(dataId, dataType, targetType) {
      getGrantDataList({dataId, dataType, targetType})
          .then(res => {
            setTimeout(()=>{
              this.initSelection(res.data);
            },300)
          });
    },
    initSelection(selectList) {
      // 根据 this.tableData 中的 selected 属性初始化选中状态
      if (selectList && selectList.length) {
        let targetIdList = selectList.map(item=>item.targetId);
        targetIdList.forEach(item => {
          this.userTableDatas.forEach(row => {
            switch (this.grantType) {
              case 'user':
                if (row.id == item) {
                  this.$refs.multipleTable.toggleRowSelection(row, true);
                }
                break;
              case 'tenant':
                if (row.tenantId === item) {
                  this.$refs.multipleTable.toggleRowSelection(row, true);
                }
                break;
            }
          });
        });
      }

    }
  },
  mounted() {
    this.getData();
  }
}
</script>

<template>
<div>
  <div style="display: flex;align-items: center;margin: 10px 0;justify-content: space-between">
    <el-select v-model="grantType" @change="onchange">
      <el-option v-for="item in grantTypeOption" :label="item.label" :value="item.value" :key="item.id"></el-option>
    </el-select>
    <div style="display: flex;align-items: center;margin: 10px 0;" v-if="grantType==='user'">
      <span style="width: 120px">{{$t('userName')}}：</span><el-input v-model="userCondition" @input="userInput"></el-input>
    </div>
    <div style="display: flex;align-items: center;margin: 10px 0;" v-else-if="grantType==='tenant'">
      <span style="width: 120px">租户名：</span><el-input v-model="userCondition" @input="userInput"></el-input>
    </div>
  </div>
  <div v-if="grantType==='user'">
    <el-table ref="multipleTable" @selection-change="userSelectionChange" :data="userTableDatas" tooltip-effect="dark" style="width: 100%" class="table">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
      <el-table-column prop="username" :label="$t('userName')"></el-table-column>
      <el-table-column prop="deptName" :label="$t('department')"></el-table-column>
    </el-table>
  </div>
  <div v-else-if="grantType==='tenant'">
    <el-table ref="multipleTable" @selection-change="userSelectionChange" :data="userTableDatas" tooltip-effect="dark" style="width: 100%" class="table">
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column type="index" width="60" :label="$t('sequenceNumber')"></el-table-column>
      <el-table-column prop="tenantName" label="租户名称"></el-table-column>
      <el-table-column prop="contacts" label="联系人"></el-table-column>
    </el-table>
  </div>
  <span slot="footer" class="dialog-footer">
    <div style="margin-top: 10px">
      <el-button type="success" @click="confirmGrant">{{$t('confirm')}}</el-button>
      <el-button @click="cancelGrant">{{ $t('cancel') }}</el-button>
    </div>
        </span>
</div>
</template>

<style scoped lang="scss">

</style>
