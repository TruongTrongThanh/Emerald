<template>
  <div class="side-user-panel">
    <div class="username-section">
      <span class="username">Anonymous</span>
      <img 
        src="~/assets/no_avatar.jpg"
        class="avatar pointer"
        @click="$emit('sideuserpanel-click')"
      >
    </div>
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
</template>

<script>
import LoginForm from '@/components/LoginForm'
import RegisterForm from '@/components/RegisterForm'

export default {
  name: 'SideUserPanel',
  components: {
    LoginForm,
    RegisterForm
  },
  data() {
    return {
      selectedSection: 1
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
    display: flex;
    justify-content: right;
    align-items: center;
    margin-top: 6px;
    margin-right: 10px;
    margin-bottom: 18px;
    .username {
      margin-right: 10px;
    }
    .avatar {
      border-radius: 30px;
      height: 45px;
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
