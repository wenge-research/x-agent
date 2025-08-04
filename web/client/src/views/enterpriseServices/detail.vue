<template>
	<div>
    <van-nav-bar
      title="项目详情"
      left-text=""
      fixed
      left-arrow
      @click-left="goBack"
    />
		<div class="project-detail" v-if="showDetail">
			<div class="detail-cont" v-html="detail.content"></div>
		</div>
	</div>
</template>

<script>
import { getStoryById } from '/@/api/enterpriseServices';
import { NavBar } from 'vant';

export default {
	meta: {
		title: '',
	},
	props: {},
	components: {
		'van-nav-bar': NavBar,
	},

	data() {
		return {
			detail: {},
			showDetail: false,
		};
	},

	mounted() {
		// }
		this.getListDetail();
	},
	methods: {
    goBack() {
      this.$router.back();
    },
		getListDetail() {
			getStoryById({
        esId: sessionStorage.getItem("esId")
      })
				.then((res) => {
					if (res.code == '000000') {
						this.detail = res.data;
            this.showDetail = true
					}
				})
				.catch((err) => {});
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

</style>
