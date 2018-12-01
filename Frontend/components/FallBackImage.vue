<template>
  <spinner
    v-if="state === 'loading'"
    :margin-height="loadingHeight"
  />
  <img 
    v-else-if="state === 'completed'"
    ref="image"
    :style="imageStyle"
    :src="imgSrc"
  >
  <img 
    v-else
    class="error-image"
    src="~/assets/error_img.png"
  >
</template>

<script>
import Spinner from '@/components/Spinner'
export default {
  components: {
    Spinner
  },
  props: {
    imgSrc: {
      type: String,
      default: '~/assets/error_img.png'
    },
    containerFit: {
      type: Boolean,
      default: true
    },
    forceZoom: {
      type: Boolean,
      default: true
    },
    disableOndragstart: {
      type: Boolean,
      default: false
    },
    loadingHeight: {
      type: Number,
      default: 50
    }
  },
  data() {
    return {
      state: 'loading',
      imageStyle: {
        maxWidth: '100%',
        width: 'auto'
      }
    }
  },
  watch: {
    forceZoom: function(newVal, oldVal) {
      if (newVal === true) {
        this.imageStyle.width = '100%'
      }
      else {
        this.imageStyle.width = 'auto'
      }
    },
    containerFit: function(newVal, oldVal) {
      if (newVal === true) {
        this.imageStyle.maxWidth = '100%'
      }
      else {
        this.imageStyle.width = 'auto'
        this.imageStyle.maxWidth = 'none'
      }
    }
  },
  mounted() {
    let img = new Image()
    img.onload = () => {
      this.state = 'completed'
      this.$nextTick(() => {
        let image = this.$refs.image
        if (image != null && this.disableOndragstart)
          image.ondragstart = () => false
      })
    }
    img.onerror = () => {
      this.state = 'failed'
    }
    img.src = this.imgSrc
  }
}
</script>

<style>
.error-image {
  padding: 15px;
  width: 100%;
}
</style>
