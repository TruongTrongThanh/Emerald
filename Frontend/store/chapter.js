export const state = () => ({
  newChapters: [],
  chapterDetails: null,
  paperList: [],
  maxPaperListPage: 0
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
  },
  setMaxPaperListPage(state, value) {
    state.maxPaperListPage = value
  }
}

export const actions = {
  fetchNewChapters({ commit }, { newChaptersUrl, newChaptersOptions = null }) {
    const config = {
      params: newChaptersOptions
    }
    return this.$axios.$get(newChaptersUrl, config).then(res => {
      commit('setNewChapters', res.content)
    })
  },
  fetchPaperList({ commit }, { papersUrl, paperOptions = null }) {
    const config = {
      params: paperOptions
    }
    return this.$axios.$get(papersUrl, config).then(res => {
      commit('setMaxPaperListPage', res.totalPages)
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
