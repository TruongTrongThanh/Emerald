<template>
  <div class="panel">
    <div class="header-box multiple">
      <div 
        v-for="(header, index) in headers" 
        :key="header"
        :class="{'pointer': true, 'header': true, 'selected': selectedIndex === index}"
        @click="selectedIndex = index"
      >
        <span class="header-icon">Icon</span> <span class="header-name">{{ header }}</span>
      </div>
    </div>
    <div 
      v-for="(header, index) in headers"
      :key="header"
      :class="{'content-box multiple': true, 'selected': selectedIndex === index}"
    >
      <slot :name="header"/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MultipleHeaderPanel',
  props: {
    headers: {
      type: Array,
      required: true
    },
    content: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      selectedIndex: 0
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/module/panel.scss';
</style>
