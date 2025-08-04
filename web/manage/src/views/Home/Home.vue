<template>
    <div style="display: flex;justify-content: space-between;padding: 20px">
      <div>{{ $t('homePage') }}</div>
      <div style="display: flex;align-items: center">
        <div style="margin-right: 20px">{{ $t('welcome') }} {{username}}</div>
        <div>
          <el-button type="danger" @click="quite">{{ $t('logout') }}</el-button>
        </div>
      </div>
    </div>
</template>

<script>
import {Login, logout} from "@/api";

    export default {
        components: {
        },
        data() {
            return {
              username: "",
            }
        },
        methods: {
          initUser() {
            const userStr = sessionStorage.getItem('user');
            const user = JSON.parse(userStr);
            this.username = user.username;
          },
          async quite() {
            const res = await logout()
            console.log(res)
            sessionStorage.clear()
            this.$router.replace({
              name: 'login',
            })
          }
        },
        created() {
          this.initUser();
        }

    }


</script>

<style lang="scss" scoped>
</style>
