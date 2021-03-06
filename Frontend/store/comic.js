export const state = () => ({
  comicList: [],
  comicDetails: null,
  comicChapters: []
})

export const mutations = {
  setComicList(state, comicList) {
    state.comicList = comicList
  },
  setComicDetails(state, comicDetails) {
    state.comicDetails = comicDetails
  },
  setComicChapters(state, comicChapters) {
    state.comicChapters = comicChapters
  }
}

export const actions = {
  fetchComicList({ commit }, { comicListUrl, comicOptions = null }) {
    const config = {
      params: comicOptions
    }
    return this.$axios.$get(comicListUrl, config).then(res => {
      commit('setComicList', res.content)
    })
  },
  fetchComicDetails({ commit }, url) {
    return this.$axios.$get(url).then(res => {
      commit('setComicDetails', res)
    })
  },
  fetchComicChapters({ commit }, { comicChaptersUrl, comicChaptersOptions = null }) {
    const config = {
      params: comicChaptersOptions
    }
    return this.$axios.$get(comicChaptersUrl, config).then(res => {
      commit('setComicChapters', res.content)
    })
  }
}
