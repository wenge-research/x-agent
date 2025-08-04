/**
 * 转义 HTML 字符
 * @param source
 */
export function encodeHTML(source: string) {
  return source
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

/**
 * 判断是否为代码块
 * @param text
 */
export function includeCode(text: string | null | undefined) {
  const regexp = /^(?:\s{4}|\t).+/gm
  return !!(text?.includes(' = ') || text?.match(regexp))
}

/**
 * 复制文本
 * @param options
 */
export function copyText(options: { text: string; origin?: boolean }) {
  const props = { origin: true, ...options }

  let input: HTMLInputElement | HTMLTextAreaElement

  if (props.origin)
    input = document.createElement('textarea')
  else
    input = document.createElement('input')

  input.setAttribute('readonly', 'readonly')
  input.value = props.text
  document.body.appendChild(input)
  input.select()
  if (document.execCommand('copy'))
    document.execCommand('copy')
  document.body.removeChild(input)
}

 // 设置千分位
export function ThousandWithNumber(value:number) {
  if (!value) return 0
  // 获取整数部分
  const wholePart = Math.trunc(value)
  // 梳理数据 -> 千分位
  const wholePartFormat = wholePart.toString().replace(/(\d)(?=(?:\d{3})+$)/g, '$1,')
  // 处理小数部分
  let decimalPart = ''
  // 将数值截取为小数部分和整数部分
  const valueArray = value.toString().split('.')
  if (valueArray.length === 2) { // 有小数部分
    decimalPart = valueArray[1].toString() // 取得小数部分
    return wholePartFormat + '.' + decimalPart
  }
  return wholePartFormat + decimalPart
}
