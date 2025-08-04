<template>
  <div class="departmentTotalCount">
    <div class="department-overview-one">
      <div style="padding-top: 24px; padding-left: 24px">
        <span class="department-overview-one-title">部门/科室数</span>
        <iconpark-icon name="question-line"></iconpark-icon>
      </div>
      <div
        style="padding-top: 20px; padding-left: 24px"
      >
        <span class="department-overview-one-number" style="font-size: 40px;margin-right: 8px;">{{ getDeptKnowledgeDataObj.deptCount }}</span
        ><span style="font-size: 16px;margin-right: 16px;">部门</span>
        <span class="department-overview-one-number" style="font-size: 40px;margin-right: 8px;">{{ getDeptKnowledgeDataObj.childrenCount }}</span
        ><span style="font-size: 16px">科室</span>
      </div>
    </div>
    <div class="department-icon">
    </div>
  </div>
</template>
<script>
export default {
  name: "DepartmentTotalCount",
  data() {
    return {
      qaCount: 0,
    };
  },
  props: {
  	getDeptKnowledgeDataObj: {
  		type: Object,
  		default: () => {
  		  return {}
  		}
  	}
  },
  methods: {
    setQaCount(data) {
      this.qaCount = data;
    },
  },
};
</script>
<style lang="scss" scoped>
.departmentTotalCount {
    position: relative;
  .department-overview-one {
    width: 100%;
    height: 120px;
    background-size: 100% 100%;
    span {
      display: inline-block;
    }
    .department-overview-one-title {
      font-size: 18px;
      color: #1D2129;
    }
    .department-overview-one-number {
      color: #5BB9BC;
      font-weight: 600;
    }
  }
  .department-icon{
    width: 172px;
    height: 120px;
    background: url('./../../../assets/images/pic.png') no-repeat bottom;
    position: absolute;
    right: 0;
    top: 0;
  }
}
</style>
