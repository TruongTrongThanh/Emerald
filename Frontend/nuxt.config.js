const pkg = require('./package')
const hostname = '192.168.1.13'
const serverUrl = `http://${hostname}:3000/api`

module.exports = {
  mode: 'universal',

  /*
  ** Headers of the page
  */
  head: {
    titleTemplate: '%s - Emerald',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: pkg.description }
    ],
    link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }]
  },
  env: {
    comicApi: process.env.COMIC_URL || serverUrl + '/comics',
    chapterApi: process.env.CHAPTER_URL || serverUrl + '/chapters',
    userApi: process.env.USER_URL || serverUrl + '/users'
  },
  server: {
    port: 8080,
    host: hostname
  },
  plugins: [
    '~/plugins/FallBackImagePlugin',
    '~/plugins/MixinPlugin',
    '~/plugins/LodashPlugin'
  ],
  /*
  ** Customize the progress-bar color
  */
  loading: { color: 'lawngreen' },
  /*
  ** Global CSS
  */
  css: ['@/assets/scss/config/reset.scss', '@/assets/scss/module/global.scss'],
  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://github.com/nuxt-community/axios-module#usage
    '@nuxtjs/axios'
  ],
  router: {
    extendRoutes(routes, resolve) {
      routes.push(
        {
          name: 'comicIdWilcard',
          path: '/comics/:id/*',
          component: resolve(__dirname, 'pages/comics/_id.vue')
        },
        {
          name: 'chapterIdWildcard',
          path: '/chapters/:id/*',
          component: resolve(__dirname, 'pages/chapters/_id.vue')
        }
      )
    },
    scrollBehavior: function(to, from, savedPosition) {
      return { x: 0, y: 0 }
    }
  },
  /*
  ** Build configuration
  */
  build: {
    /*
    ** You can extend webpack config here
    */
    extend(config, ctx) {
      // Run ESLint on save
      if (ctx.isDev && ctx.isClient) {
        config.module.rules.push({
          enforce: 'pre',
          test: /\.(js|vue)$/,
          loader: 'eslint-loader',
          exclude: /(node_modules)/
        })
      }
    }
  }
}
