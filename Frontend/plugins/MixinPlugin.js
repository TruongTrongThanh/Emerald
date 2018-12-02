import Vue from 'vue'

Vue.mixin({
  methods: {
    encodeName(name) {
      return name.replace(/\s/g, "-").toLowerCase()
    },
    resolvePageQuery(query, maxPage = Infinity) {
      if (query !== null || query !== undefined) {
        let queryPage = query.page
        if (!isNaN(queryPage)) 
          if (queryPage > 1 && queryPage < maxPage)
            return queryPage
      }
      return 1
    }
  }
})