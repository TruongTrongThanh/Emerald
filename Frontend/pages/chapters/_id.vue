<template>
  <div class="chapter-view container">
    <div 
      v-if="readingMode === 1"
      class="navigate-page-buttons"
    >
      <button @click="previousHandler">Previous</button>
      <button @click="nextHandler">Next</button>
    </div>
    <ul>
      <li 
        v-for="paper in paperList"
        :key="paper.id"
        @click="nextHandler"
      >
        <fallback-image 
          :img-src="paper.imageUrl"
          :loading-height="500"
          :width-size="'100%'"
          :force-zoom="forceZoom"
          :container-fit="containerFit"
          :disable-ondragstart="true"
        />
        <!-- <div class="previous-zone"/> -->
        <!-- <div class="next-zone"/> -->
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

  async fetch({ store, query, params, env, error }) {
    const chapterUrl = `${env.chapterApi}/${params.id}`
    const papersUrl = `${env.chapterApi}/${params.id}/papers`
  
    try {
      const chapter = await store.dispatch('chapter/fetchChapterDetails', chapterUrl)
      const comicUrl = `${env.comicApi}/${chapter.comicId}`
      const comicChaptersUrl = `${comicUrl}/chapters`
      
      let paperOptions
      if (store.state.readingSetting.readingMode === 1) {
        let page = store._vm.resolvePageQuery(query)
        paperOptions = {
          sortOrder: 'name',
          page: page,
          size: 1
        }
      }
      let paperListPromise = store.dispatch('chapter/fetchPaperList', { papersUrl, paperOptions })

      const comicChaptersOptions = { sortOrder: 'name', direction: 'desc' }
      let comicChaptersPromise = store.dispatch('comic/fetchComicChapters', { comicChaptersUrl, comicChaptersOptions })

      let comicPromise = null
      if (store.state.comic.comicDetails === null) {
        comicPromise = store.dispatch('comic/fetchComicDetails', comicUrl)
      }
      await Promise.all([paperListPromise, comicChaptersPromise, comicPromise])
    }
    catch (e) {
      if (e.response === undefined || e.response === null) 
        throw e
      else
        error({ statusCode: e.response.data.status, message: e.response.data.message })
    }
  },
  computed: {
    ...mapState({
      paperList: state => state.chapter.paperList,
      maxPaperListPage: state => state.chapter.maxPaperListPage,
      comicDetails: state => state.comic.comicDetails,
      chapterDetails: state => state.chapter.chapterDetails,
      chapterList: state => state.comic.comicChapters,
      forceZoom: state => state.readingSetting.forceZoom,
      containerFit: state => state.readingSetting.containerFit,
      readingMode: state => state.readingSetting.readingMode
    })
  },
  methods: {
    previousHandler() {
      let page = this.resolvePageQuery(this.$route.query)
      if (page - 1 > 0) 
        this.$router.push({ query: { page: page - 1  } })
      else {
        let index = this.chapterList.findIndex(chapter => chapter.id === this.chapterDetails.id)
        let preChapter = this._.get(this.chapterList, `[${index - 1}]`, null)
        if (preChapter !== null) {
          this.$router.push({ path: `/chapters/${preChapter.id}/${this.encodeName(preChapter.name)}?page=${preChapter.totalPages}` })
        }
        else
          this.$router.push({ path: `/comics/${this.comicDetails.id}/${this.encodeName(this.comicDetails.name)}` })
      }
    },
    nextHandler() {
      let page = this.resolvePageQuery(this.$route.query)
      if (page*1 + 1 <= this.maxPaperListPage)
        this.$router.push({ query: { page: page*1 + 1 } })
      else {
        let index = this.chapterList.findIndex(chapter => chapter.id === this.chapterDetails.id)
        let nextChapter = this._.get(this.chapterList, `[${index*1 + 1}]`, null)
        if (nextChapter !== null) 
          this.$router.push({ path: `/chapters/${nextChapter.id}/${this.encodeName(nextChapter.name)}?page=1` })
        else
          this.$router.push({ path: `/comics/${this.comicDetails.id}/${this.encodeName(this.comicDetails.name)}` })
      }
    }
  },
  validate({ params }) {
    return /^[0-9a-f]{24}$/g.test(params.id)
  },

  beforeRouteUpdate(to, from, next) {
    if (to.params.id === from.params.id && this.readingMode === 1) {
      const chapterApi = process.env.chapterApi
      const papersUrl = chapterApi + '/' + to.params.id + '/papers'
      let page = this.resolvePageQuery(to.query)
      const paperOptions = {
        sortOrder: 'name',
        page: page,
        size: 1
      }
      this.$store.dispatch('chapter/fetchPaperList', { papersUrl, paperOptions })
      .then(res => next())
    }
    else
      next()
  }
}
</script>

<style lang="scss" scoped>
.chapter-view {
  text-align: center;

  .navigate-page-buttons {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-column-gap: 10px;
    width: 80%;
    margin: 0 auto;
    margin-bottom: 10px;
    button {
      width: 100%;
      height: 50px;
    }
  }

  ul {
    li {
      text-align: center;
      margin-bottom: 10px;

      .previous-zone {
        background-color: red;
        width: 100%;
        height: 100%;
      }
      .next-zone {
        background-color: blue;
      }
    }
  }
}
</style>
