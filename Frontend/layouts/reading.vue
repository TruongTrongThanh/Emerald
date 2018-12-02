<template>
  <div @dblclick="bodyClickEvent($event)">
    <reading-navbar :class="{ 'no-fixed': isOnePageMode }"/>
    <nuxt :class="{ 'no-fixed': isOnePageMode }"/>
  </div>
</template>

<script>
import ReadingNavbar from '@/components/ReadingNavbar'
import { mapState, mapMutations } from 'vuex'

export default {
  components: {
    ReadingNavbar
  },
  data() {
    return {
      toggle: false,
      isOnePageMode: false
    }
  },
  computed: {
    ...mapState({
      readingMode: state => state.readingSetting.readingMode
    })
  },
  created() {
    if (this.readingMode === 1)
      this.isOnePageMode = true
  },
  methods: {
    ...mapMutations({
      toggleNavbarActive: 'readingSetting/toggleNavbarActive'
    }),
    bodyClickEvent(e) {
      let element = e.target
      while (element !== null) {
        if (element.classList.contains('navbar')) {
          return
        }
        element = element.parentElement
      }
      this.toggleNavbarActive()
    }
  }
}
</script>

<style>
</style>
