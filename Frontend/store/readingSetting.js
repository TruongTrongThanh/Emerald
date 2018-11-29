export const state = () => ({
  forceZoom: false,
  containerFit: true,
  navbarActive: true,
  clickNavbarActiveLock: true
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
    state.navbarActive = value
  },
  setClickNavbarActiveLock(state, value) {
    state.clickNavbarActiveLock = value
  }
}

export const actions = {
}
