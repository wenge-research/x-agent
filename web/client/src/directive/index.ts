import type { App } from 'vue';
import { authDirective } from '/@/directive/authDirective';

/**
 * 导出指令方法：v-xxx
 * @methods authDirective 用户权限指令，用法：v-auth
 */
export function directive(app: App) {
	// 用户权限指令
	authDirective(app);
}
