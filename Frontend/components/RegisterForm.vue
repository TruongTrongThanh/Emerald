<template>
  <form class="register-form">
    <label>Username</label>
    <input 
      name="username" 
      type="text"
    >
    <label>Password</label>
    <input 
      name="password" 
      type="password"
    >
    <label>Confirm Password</label>
    <input 
      name="confirmPassword" 
      type="password"
    >
    <label>Email</label>
    <input 
      name="email" 
      type="email"
    >
    <button>Clear</button>
    <button
      @click.prevent="submitHandler"
      v-text="'Submit'"
    />
  </form>
</template>

<script>
export default {
  name: 'RegisterForm'
}
</script>

<style lang="scss" scoped>
.register-form {
  display: grid;
  grid-template-columns: 1fr;
  grid-row-gap: 5px;
  width: 80%;
  margin: 0 auto;
  font-size: 16px;

  label {
    text-align: left;
  }
  input {
    height: 20px;
  }
  button {
    margin-top: 5px;
    height: 30px;
  }
}
</style>
