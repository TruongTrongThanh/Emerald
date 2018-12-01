export const state = () => ({
  newChapters: [],
  chapterDetails: null,
  paperList: []
})

export const mutations = {
  setNewChapters(state, newChapters) {
    state.newChapters = newChapters
  },
  setChapterDetails(state, chapterDetails) {
    state.chapterDetails = chapterDetails
  },
  setPaperList(state, paperList) {
    state.paperList = paperList
  }
}

export const actions = {
  fetchNewChapters({ commit }, { newChaptersUrl, options = null }) {
    const config = {
      params: options
    }
    return this.$axios.$get(newChaptersUrl, config).then(res => {
      commit('setNewChapters', res.content)
    })
  },
  fetchPaperList({ commit }, { papersUrl, options = null }) {
    const config = {
      params: options
    }
    return this.$axios.$get(papersUrl, config).then(res => {
      commit('setPaperList', res.content)
    })
  },
  fetchChapterDetails({ commit }, chapterUrl) {
    return this.$axios.$get(chapterUrl).then(res => {
      commit('setChapterDetails', res)
      return res
    })
  }
}
