import defaultSettings from '@/settings'

const title = defaultSettings.title || '教学共享平台管理端'

export default function getPageTitle(pageTitle) {
  if (pageTitle) {
    return `${pageTitle} - ${title}`
  }
  return `${title}`
}
