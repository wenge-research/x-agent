<template>
	<div class="box-big" />
</template>

<script>
import { useRoute } from 'vue-router';
const route = useRoute();
export default {
	data() {
		return {
			policyUrl: localStorage.getItem(`${route.params.appId}appId`),
			judicialUrl: import.meta.env.VITE_JUDICIAL_QA,
		};
	},
	watch: {},
	mounted() {
		sessionStorage.setItem('accessToken', 'fd595fcd-5a0a-488d-8ba2-10dcfaa09629');
		sessionStorage.setItem('loginPlat', 'longyan-gpt-login');
		let curHerf = window.location.href;
		if (curHerf.indexOf(this.policyUrl) > -1) {
			sessionStorage.setItem('curRouteId', this.policyUrl);
			this.$router.push({
				path: `/knowledgeDetails/${this.policyUrl}`,
			});
		}
		if (curHerf.indexOf(this.judicialUrl) > -1) {
			sessionStorage.setItem('curRouteId', this.judicialUrl);
			this.$router.push({
				path: `/knowledgeDetails/${this.judicialUrl}`,
			});
		}
	},
	methods: {},
};
</script>
