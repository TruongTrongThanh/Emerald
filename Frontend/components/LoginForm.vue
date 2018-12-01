<template>
  <form class="login-form">
    <label>Username</label>
    <input 
      v-model="form.username"
      name="username"
      type="text"
    >
    <label>Password</label>
    <input
      v-model="form.password"
      name="password" 
      type="password"
    >
    <div class="remember-checkbox">
      <input
        v-model="form.rememberMe"
        name="remember-me"
        type="checkbox"
      ><label>Remember me</label>
    </div>
    <button
      @click.prevent="loginHandler"
      v-text="'login'"
    />
  </form>
</template>

<script>
export default {
  name: 'LoginForm',
  data() {
    return {
      form: {
        username: '',
        password: '',
        rememberMe: false
      }
    }
  },
  methods: {
    loginHandler() {
      const loginUrl = process.env.userApi + '/login'
      const user = this.form
      this.$store.dispatch('user/login', { loginUrl, user })
        .catch(e => {
          console.log(e)
        })
    }
  }
}
</script>

<style lang="scss" scoped>
.login-form {
  display: grid;
  grid-template-columns: 1fr 40%;
  grid-row-gap: 5px;
  width: 80%;
  margin: 0 auto;
  font-size: 16px;

  label {
    text-align: left;
    grid-column: span 2;
  }
  input {
    height: 20px;
    grid-column: span 2;
  }
  .remember-checkbox {
    margin-top: 5px;
    display: flex;
    align-items: center;
    position: relative;
    top: 5px;
  }
  button {
    margin-top: 5px;
    height: 30px;
  }
}
</style>
