<template>
	<div class="applicationPolicy" @touchstart="handleTouchStart" @touchend="handleTouchEnd">
		<van-nav-bar title="申请政策" left-text="" fixed left-arrow @click-left="goBack" />
		<div class="applicationPolicy-content">
			<h1 style="font-weight: 600">2025年海淀区留学回国人才引进支持申请相关要求</h1>

			<h2>一、申报范围</h2>

			<h3>（一）单位条件</h3>
			<p>
				申报单位应符合海淀区功能定位及北京国际科技创新中心核心区建设主导产业方向，在海淀区注册、纳税、合法经营、发展状况良好、有长期发展意愿，且在区级服务权限范畴。经其他渠道办理的（如北京海外学人中心等），请勿从海淀区重复申报。
			</p>

			<h3>（二）人员条件</h3>
			<p>
				申报人员应符合《北京市促进留学人员来京创业和工作暂行办法》（京政发〔2009〕14号）有关规定，我国公派或自费出国（境）在国（境）外留学一年以上并已于近期回国或入境（一般应在回国或入境两年内，个人条件优秀的可适当放宽至两年半），一般在申报单位工作满3个月（以在申请单位签订合同、缴纳社保和个税时间为准），无刑事犯罪记录，引进时年龄原则上不超过45周岁（个人能力、业绩和贡献突出的可放宽至50周岁）。且符合以下条件之一：
			</p>
			<p>1.在国（境）外取得硕士及以上学位；</p>
			<p>2.出国（境）前已具有高级职称，出国（境）进行博士后研究或进修。</p>

			<h2>二、申报材料</h2>

			<h3>（一）单位材料</h3>
			<p>1.《2025年留学回国人才引进申报表》excel版及申请单位法人签字并加盖单位公章的扫描件（申报表模板及填报说明分别见附件1、附件2；必须提供）；</p>
			<p>2.申报单位2024年度《税收完税证明》（必须提供）。</p>

			<h3>（二）人员材料</h3>
			<p>1.学习证明材料</p>
			<p>（1）国（境）外学习阶段学历学位证书及教育部认证报告（本科及以上阶段均须提供，证书和认证报告缺一不可）；</p>
			<p>（2）国内学习阶段学历、学位证书及教育部认证报告（仅提供国内全日制最高学历，结合实际提供）；</p>
			<p>
				（3）出国（境）前具有高级职称，出国（境）后进行访学、进修、博士后研究等非学历学位教育的，须提供职称证明和国（境）外学习相关证明（结合实际提供）。
			</p>
			<p>2.国（境）外学习时长及首次回国（入境）证明材料</p>
			<p>（1）公安部门出入境记录查询结果（必须提供）；</p>
			<p>（2）国（境）外高校录取通知书（通知书上应注有明确具体的开学日期，否则需补充提供可证明具体开学日期的相关证明材料，必须提供）；</p>
			<p>（3）国（境）外语言班学习证明材料（结合实际提供）；</p>
			<p>（4）网上授课相关证明材料（结合实际提供，须与新冠肺炎疫情不可抗力相关）；</p>
			<p>（5）毕业后在国（境）外实习或工作等相关证明材料（结合实际提供，包括国（境）外单位出具的工作起止时间证明材料、工资单、税单等）。</p>
			<p>3.工作证明材料</p>
			<p>
				申报人员在本单位3个月以上社保连续缴纳证明，即《北京市社会保险个人权益记录（参保人员缴费信息）》（北京市人力资源和社会保障局官网查询下载个人版权益记录，非单位版权益记录，必须提供）。
			</p>
		</div>
	</div>
</template>

<script>
import detailsMixins from './detailsMixins'
export default {
	mixins: [ detailsMixins ]
};
</script>

<style lang="scss" scoped>
::v-deep .van-nav-bar {
	background: #fff;
	z-index: 99;
	.van-nav-bar__title,
	.van-icon {
		color: #797f8a;
	}
}
#nav_bar {
	background: #fff;
}
.applicationPolicy {
	&-content {
		font-family: 'SimSun', serif;
		line-height: 1.6;
		padding: 12px 24px;
		background: #fff;
		color: #323232;
		margin-top: 46px;
		h1,
		h2,
		h3 {
			text-align: center;
		}
		h1 {
			font-size: 24px;
			margin-bottom: 10px;
		}
		h2 {
			font-size: 20px;
			margin-top: 20px;
			margin-bottom: 10px;
		}
		h3 {
			font-size: 18px;
			margin-top: 15px;
			margin-bottom: 10px;
		}
		p {
			text-indent: 2em;
			margin: 5px 0;
		}
		.notice {
			font-weight: bold;
			text-align: center;
			margin-top: 20px;
		}
		.date {
			text-align: right;
			margin-top: 20px;
		}
	}
}
</style>