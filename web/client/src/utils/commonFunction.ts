// 通用函数
import useClipboard from 'vue-clipboard3';
import { Message } from 'winbox-ui-next'


import { useI18n } from 'vue-i18n';

export default function () {
	const { t } = useI18n();
	const { toClipboard } = useClipboard();
	// 点击复制文本
	const copyText = (text: string) => {
		return new Promise((resolve, reject) => {
			try {
				//复制
				toClipboard(text);
				//下面可以设置复制成功的提示框等操作
				Message.success(t('message.layout.copyTextSuccess'));
				resolve(text);
			} catch (e) {
				//复制失败
				Message.error(t('message.layout.copyTextError'));
				reject(e);
			}
		});
	};

	return {
		copyText
	};
}

