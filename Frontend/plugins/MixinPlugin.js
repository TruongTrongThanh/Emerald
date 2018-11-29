import Vue from 'vue'

Vue.mixin({
  methods: {
    encodeName(name) {
      return name.replace(/\s/g, "-").toLowerCase()
    }
  }
})