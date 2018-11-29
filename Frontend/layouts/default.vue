<template>
  <div>
    <default-navbar
      @sideuserpanel-click="toggleSideUserPanel"
    />
    <div 
      v-show="sideUserPanelActive"
      class="overlay"
    />
    <side-user-panel
      v-show="sideUserPanelActive"
      @sideuserpanel-click="toggleSideUserPanel"
    />
    <nuxt/>
  </div>
</template>

<script>
import DefaultNavbar from '@/components/DefaultNavbar'
import SideUserPanel from '@/components/SideUserPanel'

export default {
  components: {
    DefaultNavbar, 
    SideUserPanel
  },
  data() {
    return {
      sideUserPanelActive: false
    }
  },
  methods: {
    toggleSideUserPanel() {
      this.sideUserPanelActive = !this.sideUserPanelActive
    }
  }
}
</script>

<style lang="scss" scoped>
.overlay {
  background-color: rgba($color: #000000, $alpha: 0.5);
  position: fixed;
  top: 0;
  height: 100%;
  width: 100%;
}
</style>
