const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  cachedViews: state => state.tagsView.cachedViews,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  name: state => state.user.name,
  permission_routes: state => state.permission.routes
}
export default getters
