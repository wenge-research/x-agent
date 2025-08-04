import { useBreakpoints } from '@vueuse/core';
import { ref } from 'vue';
export function useBasicLayout() {
	//
	const breakpoints = useBreakpoints({
		xs: 0,
		sm: 576,
		md: 830,
		lg: 992,
		xl: 1200,
		xxl: 1600,
	});
	const isxl = breakpoints.smaller('xl');
	const ismd = breakpoints.smaller('md');
	let ua = navigator.userAgent;
	let isMobile;
	if (process.env.NODE_ENV == 'development') {
		isMobile = breakpoints.smaller('md');
	} else {
		isMobile = ref(false);
		if (ua.indexOf('iPhone') > -1) {
			isMobile.value = true;
		} else if (ua.indexOf('Android') > -1) {
			isMobile.value = true;
		} else if (ua.indexOf('HarmonyOS') > -1) {
			isMobile.value = true;
		} else if (ua.indexOf('OpenHarmony') > -1) {
			isMobile.value = true;
		} else {
			// isMobile.value = sessionStorage.getItem('isIframe')?true:false;
		}
	}
	const hasH5 = sessionStorage.getItem('isH5') ? JSON.parse(sessionStorage.getItem('isH5')) : false
	if(hasH5 && !window.location.href.includes('/basicTemplate')) {
		isMobile.value = hasH5
	} else {
		const isH5 = window.location.href.includes('isH5=') ? true : false
		if(isH5) {
			sessionStorage.setItem('isH5', isH5);
			isMobile.value = isH5
		}
	}
	return {
		isMobile,
		isxl,
		ismd,
	};
}
