<template>
  <header>
    <div class="l-content">
      <el-breadcrumb separator="/">

        <el-breadcrumb-item :to="current.path" v-if="current">
          {{ current.label }}
        </el-breadcrumb-item>
        <el-breadcrumb-item v-else>
          {{$t('homePage')}}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- <div class="r-content">
      <el-dropdown trigger="click" size="mini">
        <span class="el-dropdown-link"><img :src="userImg" class="user"/></span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item>个人中心</el-dropdown-item>
          <el-dropdown-item @click.native="logOut">退出</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div> -->
  </header>
</template>

<script>
import { mapState } from 'vuex'
export default {
  computed: {
    ...mapState({
      current: state => state.tab.currentMenu
    })
  },
  data() {
    return {
      userImg: require('../assets/images/user.png')
    }
  },
  methods: {
    collapseMenu() {
      this.$store.commit('collapseMenu')
    },
    logOut() {
      this.$store.commit('clearToken')
      this.$store.commit('clearMenu')
      location.reload()
    }
  }
}
</script>

<style lang="scss" scoped>
header {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: space-between;
}
.l-content {
  display: flex;
  align-items: center;
  .el-button {
    margin-right: 20px;
  }
}
.r-content {
  .user {
    width: 40px;
    height: 40px;
    border-radius: 50%;
  }
}
</style>

<style lang="scss">
.el-breadcrumb__item {
  .el-breadcrumb__inner {
    font-family: MiSans-Medium;
    font-size: 18px;
    color: #383D47;
    line-height: 28px;
  }
}
</style>
