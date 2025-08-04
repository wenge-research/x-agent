<template>
	<div>
		<van-nav-bar
      title="E刻钟生活圈"
      left-text=""
      fixed
      left-arrow
      @click-left="goBack"
    />
		<div class="enterprise-services">
			<div class="enterprise-services-hd">
        <img
					src="/src/assets/enterpriseServices/building.png"
					alt=""
				/>
        <div class="enterprise-services-hd-title">功能暂未开放，敬请期待</div>
      </div>
		</div>
	</div>
</template>
<script>
import { NavBar } from 'vant';
export default {
	meta: {
		title: "",
	},
	props: {},
  components: {
    'van-nav-bar': NavBar,
  },

	data() {
		return {};
	},

	mounted() {
		// }
	},
	methods: {
    goBack() {
      this.$router.back();
    },
		comeBack() {
			window.location.href =
				"https://dibrain.localhost/wg-agent-client/#/homePage/zgc";
		},
	},
};
</script>
<style scoped lang="scss">
::v-deep .van-nav-bar{
  background: #1683a2;
  .van-nav-bar__title, .van-icon{
    color: #fff;
  }
}
.enterprise-services {
	position: relative;
	text-align: center;
	height: 100vh;
	background: #fff;
	background-size: cover;
  padding: 46px 0 0 0;
  overflow: hidden;
  text-align: center;
	.enterprise-services-hd {
		font-family: MiSans, MiSans;
    font-weight: 400;
    font-size: 16px;
    color: #B4BCCC;
    line-height: 20px;
    font-style: normal;
    img{
      margin: 80px auto 40px;
      width: 160px;
    }
	}

}
</style>
