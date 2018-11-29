<template>
  <div class="comic-page container">
    <div class="panel info">
      <div class="header-box">
        <h3 class="header">Comic Info</h3>
      </div>
      <div class="content-box">
        <div class="comic-details">
          <img 
            :src="comicDetails.coverUrl"
            class="cover"
          >
          <h2 
            class="comic-name" 
            v-text="comicDetails.name"
          />
          <div class="details">
            <div>Author</div>
            <div v-text="comicDetails.author"/>
            <div>Demographic</div>
            <div v-text="comicDetails.demographic"/>
            <div class="rich-text-title">Genres</div>
            <ul class="rich-text">
              <li 
                v-for="genre in comicDetails.genres" 
                :key="genre"
                class="genre-tag" 
                v-text="genre"
              />
            </ul>
            <div class="rich-text-title">Description</div>
            <div 
              class="rich-text" 
              v-text="comicDetails.description"
            />
          </div>
          
        </div>
      </div>
    </div>
    <div class="panel chapters">
      <div class="header-box">
        <h3 class="header">Chapter List</h3>
      </div>
      <div class="content">
        <div 
          v-if="comicChapters.length > 0"
          class="chapter-list"
        >
          <nuxt-link
            v-for="chapter in comicChapters"
            :key="chapter.id"
            :to="`/chapters/${chapter.id}/${encodeName(chapter.name)}`"
            class="chapter-item no-link-style"
            v-text="chapter.name"
          />
        </div>
        <div 
          v-else
          class="no-content-box"
          v-text="'No chapters'"
        />
      </div>
    </div>
    <div class="panel related">
      <div class="header-box">
        <h3 class="header">Related Comics</h3>
      </div>
      <div class="content-box">
        Content
      </div>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  head() {
    return {
      title: this.comicDetails.name
    }
  },
  computed: {
    ...mapState({
      comicDetails: state => state.comic.comicDetails,
      comicChapters: state => state.comic.comicChapters
    })
  },
  validate({ params }) {
    return /^[0-9a-f]{24}$/g.test(params.id)
  },
  async fetch({ params, store, env, error }) {
    let comicUrl = env.comicApi + '/' + params.id
    let comicChaptersUrl = env.comicApi + '/' + params.id + '/chapters'
    try {
      await store.dispatch('comic/fetchComicDetails', comicUrl)
      const options = {
        sortOrder: 'name',
        direction: 'desc'
      }
      await store.dispatch('comic/fetchComicChapters', { comicChaptersUrl, options })
    }
    catch (e) {
      let statusCode = e.response ? e.response.data.status : 500
      let message = e.response ? e.response.data.message : 'Server Error'
      error({ statusCode: statusCode, message: message })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/module/panel.scss';
@import '@/assets/scss/module/comicDetails.scss';

.comic-page {
  display: grid;
  grid-gap: 15px;
  grid-template-areas:
    "info"
    "chapters"
    "related";

  @include for-tablet-landscape-up {
    grid-gap: 20px;
    align-items: start;
    grid-template-columns: 1fr 25%;
    grid-template-areas:
      "info related"
      "info related"
      "chapters related";
  }
  .info {
    grid-area: info;

    .comic-name {
      padding: 10px 0;
    }
  }
  .chapters {
    grid-area: chapters;

    .chapter-list {
      > .chapter-item {
        padding: 10px 0 10px 10px;
        display: block;
        
        &:nth-child(even) {
          background-color: #dfdfdf;
        }
        &:nth-child(odd) {
          background-color: #eaeaea;
        }

        &:hover {
          background-color: #55c88e;
          color: white;
        }
      }
    }
    .no-content-box {
      padding: 10px;
      color: gray;
      font-style: italic;
    }
  }
  .related {
    grid-area: related;
  }
}
</style>
