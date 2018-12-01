<template>
  <div class="home container">
    <multiple-header-panel
      :headers="['New Comics', 'New Chapters', 'Random Comics']"
      content="some awesome content...."
    >
      <div 
        slot="New Comics"
        class="new-comics-section"
      >
        <div
          v-for="comic in comicList"
          :key="comic.id" 
          class="comic-details"
        >
          <nuxt-link 
            :to="`/comics/${comic.id}/${encodeName(comic.name)}`"
            class="cover"
          >
            <fallback-image
              :img-src="comic.thumbUrl"
              :loading-height="70"
            />
          </nuxt-link>
          <h2
            class="comic-name"
            v-text="comic.name"
          />
          <div class="details">
            <div>Author</div>
            <div v-text="comic.author"/>
            <div>Demographic</div>
            <div v-text="comic.demographic"/>
            <div class="rich-text-title">Genres</div>
            <ul class="rich-text">
              <li 
                v-for="genre in comic.genres" 
                :key="genre"
                class="genre-tag" 
                v-text="genre"
              />
            </ul>
          </div>
        </div>
      </div>

      <template slot="New Chapters">
        New chapters...
      </template>

      <template slot="Random Comics">
        Random comics...
      </template>
    </multiple-header-panel>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import lodash from 'lodash'
import multipleHeaderPanel from '@/components/MultipleHeaderPanel'

export default {
  name: 'Home',
  head() {
    return {
      title: 'Home'
    }
  },
  components: {
    multipleHeaderPanel
  },
  async fetch({ store, env, error }) {
    let comicListUrl = env.comicApi
    let newChaptersUrl = env.chapterApi
    try {
      await store.dispatch('chapter/fetchNewChapters', { newChaptersUrl })
      await store.dispatch('comic/fetchComicList', { comicListUrl })
    }
    catch (e) {
      if (e.response === undefined || e.response === null) 
        error({ statusCode: 500, message: "Server is down" })
      else
        error({ statusCode: e.response.data.status, message: e.response.data.message })
    }
  },
  filters: {
    calculateTime(val) {
      if (!val) return ''
    
      const ONE_SECOND = 1000
      const ONE_MINUTE = ONE_SECOND * 60
      const ONE_HOUR = ONE_MINUTE * 60
      const ONE_DAY = ONE_HOUR * 24
    
      let chapterDate = new Date(val)
      let currentDate = new Date()
   
      let yearDiff = currentDate.getUTCFullYear() - chapterDate.getUTCFullYear()
      let monthDiff =
      yearDiff * 12 + (currentDate.getUTCMonth() - chapterDate.getUTCMonth()) 
      let timeDiff = Math.abs(chapterDate.getTime() - currentDate.getTime())
      let dayDiff = Math.round(timeDiff / ONE_DAY)
      let hourDiff = Math.round(timeDiff / ONE_HOUR)
      let minuteDiff = Math.round(timeDiff / ONE_MINUTE)
      let secondDiff = Math.round(timeDiff / ONE_SECOND)

      if (monthDiff < 12) {
        if (dayDiff < 30) {
          if (hourDiff < 24) {
            if (minuteDiff < 60) {
              if (secondDiff < 60) {
                return secondDiff + ' seconds ago'
              }
              return minuteDiff + ' minutes ago'
            }
            return hourDiff + ' hours ago'
          }
          return dayDiff + ' days ago'
        }
        return monthDiff + ' months ago'
      }
      return yearDiff + ' years ago'
    }
  },
  computed: {
    ...mapState({
      comicList: state => state.comic.comicList,
      newChapters: state => state.chapter.newChapters
    }),
    groupNewChapters() {
      let chapterGroupList = []
      for (let i = 0; i < this.newChapters.length; i++) {
        let chapter = this.newChapters[i]

        let index = chapterGroupList.findIndex(chapterGroup => {
          return chapterGroup.comicName === chapter.comic.name
        })

        if (index === -1) {
          let chapterGroup = {
            comicName: chapter.comic.name,
            comicThumbUrl: chapter.comic.thumbUrl,
            timeStamp: new Date(chapter.createdAt),
            chapters: []
          }
          chapterGroup.chapters.push(chapter)
          chapterGroupList.push(chapterGroup)
        }
        else {
          let chapterGroup = chapterGroupList[index]
          let chapterDateTime = new Date(chapter.createdAt)
          if (chapterDateTime > chapterGroup.timeStamp) {
            chapterGroup.timeStamp = chapterDateTime
          }
          chapterGroup.chapters.push(chapter)
        }
      }
      return chapterGroupList
    }
  },
  methods: {
    truncateGenresList(genreList) {
      if (genreList.length > 3) 
        return genreList.slice(0, 3)
    
      return genreList
    },
    orderedChapter(chapters) {
      return lodash.orderBy(chapters, ['createdAt'], ['desc'])
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/module/panel.scss';
@import '@/assets/scss/module/comicDetails.scss';

.home {
 .panel {
   width: 90%;
   margin: 0 auto;
  @include for-tablet-landscape-up {
    width: 95%;
  }

   .new-comics-section {
     display: grid;
     grid-template-columns: 1fr;

    @include for-tablet-landscape-up {
      grid-template-columns: 1fr 1fr;
      grid-column-gap: 15px;
     }
   }
 }
}
</style>
