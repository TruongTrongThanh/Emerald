<template>
  <div class="side-user-panel">
    <div 
      class="username-section"
      @click="$emit('sideuserpanel-click')"
    >
      <user-info/>
    </div>
    <div
      v-if="!authUser"
      class="no-auth"
    >
      <div class="button-section">
        <button
          @click="selectedSection = 1"
          v-text="'Register'"
        />
        <button
          @click="selectedSection = 2"
          v-text="'Login'"
        />
      </div>
      <div class="line"/>
      <div 
        v-if="selectedSection === 1"
        class="register-section"
      >
        <register-form/>
      </div>
      <div
        v-if="selectedSection === 2"
        class="login-section"
      >
        <login-form/>
      </div>
      <div class="line"/>
    </div>

    <div 
      v-else
      class="is-auth"
    >
      Hello {{ authUser.username }}!
      <button @click="logoutHandler">Logout</button>
    </div>
  </div>
</template>

<script>
import LoginForm from '@/components/LoginForm'
import RegisterForm from '@/components/RegisterForm'
import UserInfo from '@/components/UserInfo'
import { mapState } from 'vuex'

export default {
  name: 'SideUserPanel',
  components: {
    LoginForm,
    RegisterForm,
    UserInfo
  },
  data() {
    return {
      selectedSection: 1
    }
  },
  computed: {
    ...mapState({
      authUser: state => state.user.authUser
    })
  },
  methods: {
    async logoutHandler() {
      const logoutUrl = process.env.userApi + "/logout"
      await this.$store.dispatch('user/logout', logoutUrl);
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/config/mixin.scss';

.side-user-panel {
  position: fixed;
  background-color: white;
  text-align: center;

  height: 200px;
  margin: 0 auto;
  @include for-tablet-landscape-up {
    height: 100%;
    top: 0;
    width: 270px;
    right: 0;
  }

  .username-section {
    display: block;
    margin-top: 6px;
    margin-bottom: 18px;

    .user-info {
      justify-content: right;
      margin-right: 10px;
      margin-top: 7.5px;
    }
  }

  .register-section {
    margin-bottom: 18px;
  }
  .login-section {
    margin-bottom: 18px;
  }

  .line {
    width: 90%;
    height: 0.5px;
    margin: 0 auto;
    background-color: black;
    margin-top: 19px;
    margin-bottom: 19px;
  }
}
</style>
