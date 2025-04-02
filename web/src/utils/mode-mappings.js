const languageToMime = {
  // 常见语言映射
  javascript: 'text/javascript',
  python: 'text/x-python',
  html: 'text/html',
  css: 'text/css',
  java: 'text/x-java',
  cpp: 'text/x-c++src',
  php: 'text/x-php',
  json: 'application/json',
  xml: 'application/xml',
  sql: 'text/x-sql',
  markdown: 'text/x-markdown',
  ruby: 'text/x-ruby',
  go: 'text/x-go',
  yaml: 'text/x-yaml',
  shell: 'text/x-sh',
  lua: 'text/x-lua',
  // 特殊或复合模式
  vue: 'text/html',          // Vue 文件使用 HTML 混合模式
  typescript: 'text/typescript',
  sass: 'text/x-sass',
  less: 'text/x-less',
  // 更多映射可自行扩展...
};

// 处理特殊语言名称 (highlight.js 返回的可能别名)
const languageAliases = {
  js: 'javascript',
  ts: 'typescript',
  py: 'python',
  rb: 'ruby',
  sh: 'shell',
  yml: 'yaml',
  md: 'markdown',
  // 其他别名...
};

import hljs from 'highlight.js';

function getMimeType(language) {
  // 处理别名
  const normalizedLang = languageAliases[language] || language;
  // 返回对应的 MIME 类型，找不到则返回纯文本
  return languageToMime[normalizedLang] || 'text/plain';
}

export function autoGetMode(content) {
  const result = hljs.highlightAuto(content);
  console.log('language', result.language)
  return getMimeType(result.language);
}
