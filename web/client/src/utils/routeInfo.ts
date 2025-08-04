import { useBasicLayout } from '/@/hooks/useBasicLayout';
import { getUrlParamValue } from '/@/utils/tool';

const { isMobile } = useBasicLayout();
/**
 * 获取code
 */
export function getUrlParamsLegacy() {
	const search = window.location.search.substring(1);
	const params = search.split('&');
	const paramObject = {};

	for (let i = 0; i < params.length; i++) {
		const keyValue = params[i].split('=');
		if (keyValue.length === 2) {
			paramObject[decodeURIComponent(keyValue[0])] = decodeURIComponent(keyValue[1]);
		}
	}

	return paramObject;
}

export function toPageDetail(to, from, next, data, accountName) {
	let key = getUrlParamValue('key');
	let { mobileTemplateRoute, templateRoute, applicationCode } = data;
	// console.log("to.path", to.path)
	// 新版PC路由        /knowledgeDetails-new
	// 新版H5路由        /chat
	// 助手PC路由        /assistantPc
	// 助手H5路由        /assistantMobile
	// 老版PC、H5路由    /knowledgeDetails
	// 合肥模板          /knowledgeDetails-hf
	// 重庆模版 掌上两城 /twoCitiesPlamCq
	// 重庆模版 政策帮   /policyHelpCq
	// H5通用模板   /mobileUniversalTemplate
	// H5xxxxxxx模板   /mobileDazhouTemplate
	// 郑小东专用模板   /zhengxiaodong(法阁君)
	const routeMap = {
		'/knowledgeDetails-new/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				assistantPc: `/assistantPc/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
		},
		'/knowledgeDetails/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: accountName ? `/appTemplate/${applicationCode}?accountName=${accountName}` : `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				'knowledgeDetails-searchNew-hk': `/knowledgeDetails-searchNew-hk/${applicationCode}`,
				'knowledgeDetails-searchNew-inudtry': `/knowledgeDetails-searchNew-inudtry/${applicationCode}`,
				'knowledgeDetails-searchNew-zh': `/knowledgeDetails-searchNew-zh/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// PC通用模板
		'/appTemplate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: key ? `/mobileUniversalTemplate/${applicationCode}?key=${key}` : `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		'/chat/': {
			mobile: {
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': `/knowledgeDetails-new/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
		},
		'/assistantPc/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				'knowledgeDetails-new': `/knowledgeDetails-new/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
		},
		'/assistantMobile/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				'knowledgeDetails-new': `/knowledgeDetails-new/${applicationCode}`,
				assistantPc: `/assistantPc/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
		},
		'/twoCitiesPlam-cq/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				'knowledgeDetails-new': `/knowledgeDetails-new/${applicationCode}`,
				assistantPc: `/assistantPc/${applicationCode}`,
			},
		},
		'/policyHelp-cq/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				'knowledgeDetails-new': `/knowledgeDetails-new/${applicationCode}`,
				assistantPc: `/assistantPc/${applicationCode}`,
			},
		},
		// 网信办模板
		'/sz-cac/': {
			mobile: {
				chat: `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetails/${applicationCode}`,
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 手机通用模板
		'/mobileUniversalTemplate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 手机通用模板
		'/mobileDazhouTemplate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 手机通用模板
		'/knowledgeDetailSjj/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`, // 市督
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 粤语PC模板
		'/cantoneseTemplate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 新版市监局模板PC
		'/smqa/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 应用基础模板PC
		'/basicTemplate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 郑小东H5模板
		'/zhengxiaodong/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// AI视频拆分
		'/aiVideoSplitting/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				reportGenerate: `/reportGenerate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
			},
		},
		// AI报告生成
		'/reportGenerate/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 视频背景大屏
		'/videoLargeScreen/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				TTAnalysis: `/TTAnalysis/${applicationCode}`,
			},
		},
		// 智库数据分析模板
		'/TTAnalysis/': {
			mobile: {
				chat: accountName ? `/chat/${applicationCode}?accountName=${accountName}` : `/chat/${applicationCode}`,
				knowledgeDetails: `/knowledgeDetailSjj/${applicationCode}`, // 市督
				assistantMobile: `/assistantMobile/${applicationCode}`,
				'twoCitiesPlam-cq': `/twoCitiesPlam-cq/${applicationCode}`,
				'policyHelp-cq': `/policyHelp-cq/${applicationCode}`,
				'sz-cac': `/sz-cac/${applicationCode}`,
				mobileUniversalTemplate: `/mobileUniversalTemplate/${applicationCode}`,
				mobileDazhouTemplate: `/mobileDazhouTemplate/${applicationCode}`,
				zhengxiaodong: `/zhengxiaodong/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
			},
			pc: {
				knowledgeDetailLh: `/knowledgeDetailLh/${applicationCode}`,
				virtualTemplate: `/virtualTemplate/${applicationCode}`, // 虚拟园区
				assistantPc: `/assistantPc/${applicationCode}`,
				'knowledgeDetails-new': accountName
					? `/knowledgeDetails-new/${applicationCode}?accountName=${accountName}`
					: `/knowledgeDetails-new/${applicationCode}`,
				'knowledgeDetails-hf': `/knowledgeDetails-hf/${applicationCode}`,
				'knowledgeDetails-tz': `/knowledgeDetails-tz/${applicationCode}`,
				'knowledgeDetails-ai': `/knowledgeDetails-ai/${applicationCode}`,
				stockExchangeSZ: `/stockExchangeSZ/${applicationCode}`,
				fileParsing: `/fileParsing/${applicationCode}`,
				aiVideoSplitting: `/aiVideoSplitting/${applicationCode}`,
				digitalPeople: `/digitalPeople/${applicationCode}`,
				intelligentTranslation: `/intelligentTranslation/${applicationCode}`,
				createTemplate: `/createTemplate/${applicationCode}`,
				intelligentReport: `/intelligentReport/${applicationCode}`,
				aiImageTemplate: `/aiImageTemplate/${applicationCode}`,
				aiVideoTemplate: `/aiVideoTemplate/${applicationCode}`,
				municipalSupervisionBureau: `/municipalSupervisionBureau/${applicationCode}`,
				smqaNewUI: `/smqa/${applicationCode}`,
				cantoneseTemplate: `/cantoneseTemplate/${applicationCode}`,
				DHTemplate: `/DHTemplate/${applicationCode}`,
				'knowledgeDetails-search': `/knowledgeDetails-search/${applicationCode}`,
				'knowledgeDetails-searchNew': `/knowledgeDetails-searchNew/${applicationCode}`,
				basicTemplate: `/basicTemplate/${applicationCode}`,
				dazhouTemplate: `/dazhouTemplate/${applicationCode}`,
				videoLargeScreen: `/videoLargeScreen/${applicationCode}`,
				appTemplate: `/appTemplate/${applicationCode}`,
			},
		},

		// '/smqa/': {
		//     mobile: `/knowledgeDetailSjj/${applicationCode}`,
		// },
		// // 市督
		// '/knowledgeDetailSjj/': {
		//     pc: `/smqa/${applicationCode}`,
		// },
	};

	const path = Object.keys(routeMap).find((key) => to.path.includes(key));
	if (path) {
		console.log('path=============', path);
		console.log('routeMap=============', routeMap);
		const routeConfig = routeMap[path][isMobile.value ? 'mobile' : 'pc'];
		console.log('routeConfig=============', routeConfig);
		let type = isMobile.value ? mobileTemplateRoute : templateRoute;
		// 基础模板
		if (path.includes('basicTemplate')) {
			type = 'basicTemplate';
		} else if (mobileTemplateRoute == 'videoLargeScreen') {
			type = 'videoLargeScreen';
		} else {
			type = isMobile.value ? mobileTemplateRoute : templateRoute;
		}
		let route = routeConfig ? routeConfig[type] : null;
		console.log('route=============', route);
		if (route && route !== to.path) {
			next(route);
		} else {
			next();
		}
	} else {
		next();
	}
}

// 过滤权限
export function filterPermissions(permissions) {
	return permissions
		.filter((item) => !item.clientType || item.clientType === 'H5')
		.filter((item) => item.isHidden !== 1)
		.map((item) => {
			if (item.children) {
				item.children = filterPermissions(item.children);
			}
			return item;
		});
}
