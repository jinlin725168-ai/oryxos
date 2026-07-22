import { defineConfig } from 'vitepress'

// ⚠️ 占位项（部署前替换）：GitHub 仓库地址、站点域名。
const GITHUB = 'https://github.com/oryxos-jin/oryxos'
const HOSTNAME = 'https://oryxos.dev'

export default defineConfig({
  title: 'OryxOS',
  titleTemplate: ':title — OryxOS',
  description: '企业级 Java 原生 Agent OS —— 私有、可控、可审计的智能体统一底座。',
  base: '/',
  cleanUrls: true,
  appearance: 'force-light',

  head: [
    ['link', { rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.googleapis.com' }],
    ['link', { rel: 'preconnect', href: 'https://fonts.gstatic.com', crossorigin: '' }],
    ['link', { rel: 'stylesheet', href: 'https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@500;700&display=swap' }],
    ['meta', { name: 'author', content: 'OryxOS' }],
    ['meta', { name: 'keywords', content: 'OryxOS, Agent OS, 智能体操作系统, Java, Spring Boot, Spring AI Alibaba, ReAct, MCP, LLM, 企业 AI Agent, 私有部署' }],
    ['meta', { name: 'robots', content: 'index, follow' }],
    ['meta', { property: 'og:type', content: 'website' }],
    ['meta', { property: 'og:site_name', content: 'OryxOS' }],
    ['meta', { property: 'og:title', content: 'OryxOS — 企业级 Java 原生 Agent OS' }],
    ['meta', { property: 'og:description', content: '私有、可控、可审计的智能体统一底座，装在你自己的基础设施上。' }],
    ['meta', { property: 'og:url', content: HOSTNAME }],
    ['meta', { name: 'twitter:card', content: 'summary_large_image' }],
    ['link', { rel: 'canonical', href: HOSTNAME }],
  ],

  locales: {
    root: {
      label: 'English',
      lang: 'en-US',
      themeConfig: {
        nav: [
          { text: 'Home', link: '/' },
          { text: 'Docs', link: '/docs/what' },
          { text: 'GitHub', link: GITHUB },
        ],
        sidebar: { '/docs/': enSidebar() },
        outline: { label: 'On this page', level: [2, 3] },
        docFooter: { prev: 'Previous', next: 'Next' },
        returnToTopLabel: 'Back to top',
      },
    },
    zh: {
      label: '中文',
      lang: 'zh-CN',
      link: '/zh/',
      themeConfig: {
        nav: [
          { text: '首页', link: '/zh/' },
          { text: '文档', link: '/zh/docs/what' },
          { text: 'GitHub', link: GITHUB },
        ],
        sidebar: { '/zh/docs/': zhSidebar() },
        outline: { label: '本页目录', level: [2, 3] },
        docFooter: { prev: '上一篇', next: '下一篇' },
        returnToTopLabel: '返回顶部',
      },
    },
  },

  themeConfig: {
    siteTitle: false,
    logo: '/logo.svg',
    socialLinks: [{ icon: 'github', link: GITHUB }],
  },

  sitemap: { hostname: HOSTNAME },
})

function enSidebar() {
  return [
    {
      text: 'Getting Started',
      items: [
        { text: 'What is OryxOS', link: '/docs/what' },
        { text: 'Quick Start', link: '/docs/quick-start' },
      ],
    },
    {
      text: 'Concepts',
      items: [
        { text: 'Five Core Capabilities', link: '/docs/capabilities' },
        { text: 'Defining an Agent', link: '/docs/agent' },
      ],
    },
    {
      text: 'Deep Dives',
      items: [
        { text: 'Architecture', link: '/docs/architecture' },
        { text: 'REST API', link: '/docs/rest-api' },
        { text: 'CLI Reference', link: '/docs/cli' },
      ],
    },
    {
      text: 'Reference',
      items: [
        { text: 'Roadmap', link: '/docs/roadmap' },
        { text: 'FAQ', link: '/docs/faq' },
      ],
    },
  ]
}

function zhSidebar() {
  return [
    {
      text: '快速入门',
      items: [
        { text: 'OryxOS 是什么', link: '/zh/docs/what' },
        { text: '快速开始', link: '/zh/docs/quick-start' },
      ],
    },
    {
      text: '核心概念',
      items: [
        { text: '五大核心能力', link: '/zh/docs/capabilities' },
        { text: '定义一个 Agent', link: '/zh/docs/agent' },
      ],
    },
    {
      text: '深入了解',
      items: [
        { text: '整体架构', link: '/zh/docs/architecture' },
        { text: 'REST API', link: '/zh/docs/rest-api' },
        { text: '命令行参考', link: '/zh/docs/cli' },
      ],
    },
    {
      text: '参考',
      items: [
        { text: '路线图', link: '/zh/docs/roadmap' },
        { text: '常见问题', link: '/zh/docs/faq' },
      ],
    },
  ]
}
