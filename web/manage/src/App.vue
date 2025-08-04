<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style lang="scss">
  #app {
    height: 100vh;
  }
// 全局滚动条隐藏开始
.no-scrollbar {
    overflow: auto; /* 确保内容仍然可以滚动 */
    -ms-overflow-style: none;  /* Internet Explorer 10+ */
    scrollbar-width: none;     /* Firefox */
}
 
/* 适用于 Chrome, Safari, Edge */
.no-scrollbar::-webkit-scrollbar {
    display: none;
}
.el-table--scrollable-y .el-table__body-wrapper {
    overflow: auto; /* 确保内容仍然可以滚动 */
    -ms-overflow-style: none;  /* Internet Explorer 10+ */
    scrollbar-width: none;     /* Firefox */
}
/* 适用于 Chrome, Safari, Edge */
.el-table--scrollable-y .el-table__body-wrapper::-webkit-scrollbar {
    display: none;
}
// 全局滚动条隐藏结束
</style>
