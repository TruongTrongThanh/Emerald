export const state = () => ({
  forceZoom: false,
  containerFit: true,
  navbarActive: true,
  clickNavbarActiveLock: true,
  readingMode: 1
})

export const mutations = {
  toggleForceZoom(state) {
    state.forceZoom = !state.forceZoom
  },
  toggleContainerFit(state) {
    state.containerFit = !state.containerFit
    if (state.containerFit === false)
      state.forceZoom = false
  },
  toggleNavbarActive(state) {
    if (!state.clickNavbarActiveLock)
      state.navbarActive = !state.navbarActive
  },
  setNavbarActive(state, value) {
    if (!state.clickNavbarActiveLock)
      state.navbarActive = value
  },
  setClickNavbarActiveLock(state, value) {
    state.clickNavbarActiveLock = value
  },
  setReadingMode(state, value) {
    state.readingMode = value
  }
}

export const actions = {
}
