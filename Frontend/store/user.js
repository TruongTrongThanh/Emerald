export const state = () => ({
  authUser: null
})

export const mutations = {
  setAuthUser(state, authUser) {
    state.authUser = authUser
  }
}

export const actions = {
  login({ commit }, { loginUrl, user }) {
    const config = {
      withCredentials: true
    }
    return this.$axios.$post(loginUrl, user, config).then(res => {
      commit('setAuthUser', res.data)
      return res
    })
  },
  fetchCurrentUser({ commit }, currentUserUrl) {
    return this.$axios.$post(currentUserUrl).then(res => {
      commit('setAuthUser', res)
      return res
    })
  },
  logout({ commit }, logoutUrl) {
    return this.$axios.$post(logoutUrl).then(res => {
      commit('setAuthUser', null)
      return res
    })
  }
}
