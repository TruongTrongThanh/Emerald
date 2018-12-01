export const actions = {
  async nuxtServerInit ({ dispatch }, { req, env }) {
    if (req.headers.cookie) {
      const currentUserUrl = env.userApi + "/current-user"
      await dispatch('user/fetchCurrentUser', currentUserUrl)
    }
  }
}