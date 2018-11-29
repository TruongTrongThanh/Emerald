<template>
  <div @dblclick="bodyClickEvent($event)">
    <reading-navbar/>
    <nuxt/>
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
      toggle: false
    }
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
