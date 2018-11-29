<template>
  <div class="chapter-view">
    <ul>
      <li 
        v-for="paper in paperList"
        ref="imageHolders" 
        :key="paper.id"
        class="drag-scroll"
      >
        <fallback-image 
          :img-src="paper.imageUrl"
          :loading-height="500"
          :width-size="'100%'"
          :force-zoom="forceZoom"
          :container-fit="containerFit"
          :disable-ondragstart="true"
        />
      </li>
    </ul>
  </div>
</template>

<script>
import { mapState, mapMutations } from 'vuex'

export default {
  head() {
    return {
      title: this.chapterDetails.name + ' / ' + this.comicDetails.name
    }
  },
  layout: 'reading',

  async fetch({ store, params, env, error }) {
    const chapterUrl = env.chapterApi + '/' + params.id
    const papersUrl = env.chapterApi + '/' + params.id + '/papers'
  
    try {
      const chapter = await store.dispatch('chapter/fetchChapterDetails', chapterUrl)
      const comicUrl = env.comicApi + '/' + chapter.comicId
      const comicChaptersUrl = comicUrl + '/chapters'
      const options = {
        sortOrder: 'name'
      }

      let paperListPromise = store.dispatch('chapter/fetchPaperList', { papersUrl, options })
      let comicChaptersPromise = store.dispatch('comic/fetchComicChapters', { comicChaptersUrl, options })

      let comicPromise = null
      if (store.state.comic.comicDetails === null) {
        comicPromise = store.dispatch('comic/fetchComicDetails', comicUrl)
      }
      await Promise.all([paperListPromise, comicChaptersPromise, comicPromise])
    }
    catch (e) {
      if (e.response === undefined || e.response === null) 
        error({ statusCode: 500, message: "Server is down" })
      else
        error({ statusCode: e.response.data.status, message: e.response.data.message })
    }
  },
  computed: {
    ...mapState({
      paperList: state => state.chapter.paperList,
      comicDetails: state => state.comic.comicDetails,
      chapterDetails: state => state.chapter.chapterDetails,
      forceZoom: state => state.readingSetting.forceZoom,
      containerFit: state => state.readingSetting.containerFit
    })
  },
  watch: {
    containerFit: function(newVal) {
      this.$nextTick(() => {
        let imageHolders = this.$refs.imageHolders
        for (let i = 0; i < imageHolders.length; i++) {
          if (newVal === false) {
            let hasHorizontalScrollbar = imageHolders[i].scrollWidth > imageHolders[i].clientWidth
            if (hasHorizontalScrollbar)
              imageHolders[i].style.cursor = 'move'
          }
          else
            imageHolders[i].style.cursor = 'auto'
        }
      })
    }
  },
  mounted() {
    let dragHandler = (e) => {
      let element = e.target.parentElement
      if (e.buttons === 1 && element !== null && element.classList.contains('drag-scroll')) {
        element.scrollLeft -= e.movementX
      }
    }
    window.addEventListener('mousemove', dragHandler)
  },
  methods: {
    changeCursor() {
      console.log('test')
    }
  },
  validate({ params }) {
    return /^[0-9a-f]{24}$/g.test(params.id)
  }
}
</script>

<style lang="scss" scoped>
.chapter-view {
  ul {
    li {
      text-align: center;
      margin-bottom: 10px;
      overflow-x: auto;
      overflow-y: hidden;
    }
  }
  .scroll-area {
  position: relative;
  margin: auto;
  width: 400px;
  height: 300px;
}
}
</style>
