<template>
  <div class="user-info pointer">
    <span v-if="authUser">{{ authUser.username }}</span>
    <span v-else>Anonymous</span>
    <img
      v-if="authUser"
      :src="authUser.avatarUrl || require('~/assets/no_avatar.jpg')"
      class="avatar"
    >
    <img 
      v-else
      src="~/assets/no_avatar.jpg"
      class="avatar"
    >
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  computed: {
    ...mapState({
      authUser: state => state.user.authUser
    })
  }
}
</script>

<style lang="scss">
.user-info {
  display: flex;
  align-items: center;
  .avatar {
    border-radius: 30px;
    height: 45px;
    margin-left: 7px;
  }
}
</style>
