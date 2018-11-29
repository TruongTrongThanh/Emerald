<template>
  <nav 
    :class="navbarClass"
    class="navbar"
  >
    <nuxt-link
      :to="`/comics/${comic.id}/${encodeName(comic.name)}`"
      class="no-link-style"
    >
      <div class="navbar-brand with-logo">
        <img src="@/assets/Emerald_Logo.svg">
        <div 
          class="text"
          v-text="'Back To Comic'"
        />
      </div>
    </nuxt-link>
    <div class="navbar-mobile-list">
      something inside
    </div>
    <div 
      class="navbar-toggle" 
      @click="isToggleActive = !isToggleActive"
    >
      |||
    </div>
    <div 
      :class="{ 'is-active': isToggleActive }"
      class="navbar-desktop-list" 
    >
      <div class="navbar-start">
        <div 
          :class="{ 'is-active': containerFit }"
          class="navbar-item toggle-button pointer no-select"
          @click="toggleContainerFit()"
        >
          Container Fit
        </div>
        <div
          v-if="containerFit"
          :class="{ 'is-active': forceZoom }"
          class="navbar-item toggle-button pointer no-select"
          @click="toggleForceZoom()"
        >
          Force Zoom
        </div>
      </div>
      <div class="navbar-end">
        <div class="navbar-item">
          <select class="chapter-selection">
            <option
              v-for="chapter in chapterList"
              :key="chapter.id"
              v-text="chapter.name"
            />
          </select>
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
import { mapState, mapMutations } from 'vuex'
import lodash from 'lodash'

export default {
  name: 'ReadingNavbar',
  data() {
    return {
      isToggleActive: false,
      isLock: false,
      navbarClass: {
        'is-active': true,
        'fade-enter-active': false,
        'fade-leave-active': false
      }
    }
  },
  computed: {
    ...mapState({
      paperList: state => state.chapter.paperList,
      comic: state => state.comic.comicDetails,
      chapter: state => state.chapter.chapterDetails,
      chapterList: state => state.comic.comicChapters,
      forceZoom: state => state.readingSetting.forceZoom,
      containerFit: state => state.readingSetting.containerFit,
      navbarActive: state => state.readingSetting.navbarActive
    })
  },
  watch: {
    navbarActive: function(newVal, oldVal) {
      if (newVal === true) {
        this.fadeEnterAnimation()
      }
      else {
        this.fadeLeaveAnimation()
      }
    }
  },
  mounted() {
    window.addEventListener('scroll', lodash.throttle(this.scrollDown, 100))
  },
  methods: {
    ...mapMutations({
      toggleForceZoom: 'readingSetting/toggleForceZoom',
      toggleContainerFit: 'readingSetting/toggleContainerFit',
      setNavbarActive: 'readingSetting/setNavbarActive',
      setClickNavbarActiveLock: 'readingSetting/setClickNavbarActiveLock'
    }),
    fadeEnterAnimation() {
      this.navbarClass['fade-enter-active'] = true
      setTimeout(() => {
        this.navbarClass['is-active'] = true
        this.navbarClass['fade-enter-active'] = false
      }, 300)
    },
    fadeLeaveAnimation() {
      this.navbarClass['fade-leave-active'] = true
      setTimeout(() => {
        this.navbarClass['is-active'] = false
        this.navbarClass['fade-leave-active'] = false
      }, 300)
    },
    scrollDown() {
      let current = document.documentElement.scrollTop
      let height = document.documentElement.scrollHeight - document.documentElement.clientHeight
      if (current < 1 || height - current < 1) {
        this.isLock = false
        this.setNavbarActive(true)
        this.setClickNavbarActiveLock(true)
      } 
      else {
        if (!this.isLock) {
          this.setNavbarActive(false)
          this.setClickNavbarActiveLock(false)
          this.isLock = true
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/module/navbar.scss';
.navbar {
  opacity: 0;
  &.is-active {
    opacity: 1;
  }
  &.fade-enter-active {
    animation: fade .3s;
  }
  &.fade-leave-active {
     animation: fade .3s reverse;
  }

  .text {
    text-align: left;
  }
  .chapter-selection {
    width: 200px;
    height: 27px;
  }
  .toggle-button {
    color: gray;
    &.is-active {
      color: red;
    }
  }
}

@keyframes fade {
  0% {
    opacity: 0;
  }
  100% {
    opacity: 1;
  }
}
</style>
