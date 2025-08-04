import { onMounted, onUpdated } from 'vue'
import { copyText } from '/@/utils/format'
import { Message } from 'winbox-ui-next';
import { useChatStore } from '/@/stores/chat';

window.themeBtn = () => {
  const chatStore = useChatStore()
  chatStore.isLightTheme = !chatStore.isLightTheme
}
window.copyBtn = () => {
  Message.success("复制成功")
}

export function useCopyCode() {
  function copyCodeBlock() {
    const codeBlockWrapper = document.querySelectorAll('.code-block-wrapper')
    codeBlockWrapper.forEach((wrapper) => {
      const copyBtn = wrapper.querySelector('.code-block-header__copy')
      const themeBtn = wrapper.querySelector('.code-block-header__theme')
      const codeBlock = wrapper.querySelector('.code-block-body')
      if (copyBtn && codeBlock) {
        copyBtn.addEventListener('click', () => {
          if (navigator.clipboard?.writeText)
            navigator.clipboard.writeText(codeBlock.textContent ?? '')
          else
            copyText({ text: codeBlock.textContent ?? '', origin: true })
        })
      }
    })
  }

  onMounted(() => copyCodeBlock())

  onUpdated(() => copyCodeBlock())
}
