<template>
  <nav class="navbar">
    <nuxt-link 
      to="/"
      class="no-link-style"
    >
      <div class="navbar-brand with-logo">
        <img src="~/assets/Emerald_Logo.svg">
        <div class="text">Emerald Forest</div>
      </div>
    </nuxt-link>
    <div class="navbar-mobile-list">
      <div class="search-bar">
        <input 
          type="text" 
          placeholder="Search..."
        >
        <button class="search-icon">S</button>
      </div>
    </div>
    <div 
      class="navbar-toggle" 
      @click="isNavBarActive = !isNavBarActive"
    >
      |||
    </div>

    <div 
      :class="{ 'is-active': isNavBarActive }"
      class="navbar-desktop-list" 
    >
      <div class="navbar-start">
        <div class="navbar-item">Item 1</div>
        <div class="navbar-item">Item 2</div>
        <div class="navbar-item desktop-only">
          <div class="search-bar">
            <input 
              type="text" 
              placeholder="Search..."
            >
            <button class="search-icon">Search</button>
          </div>
        </div>
      </div>
      <div class="navbar-end">
        <div class="navbar-item username">Anonymous</div>
        <div class="navbar-item no-padding">
          <img 
            src="~/assets/no_avatar.jpg"
            class="avatar pointer"
            @click="$emit('sideuserpanel-click')"
          >
        </div>
      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'DefaultNavbar',
  data() {
    return {
      isNavBarActive: false
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/module/navbar.scss';

.search-bar {
  $borderStyle: 1px solid #a8a8a8;
  display: grid;
  grid-template-columns: 80% 20%;
  width: 95%;
  margin: 0 auto;
  @include for-tablet-landscape-up {
    width: 400px;
  }
  input[type='text'] {
    height: 35px;
    box-sizing: border-box;
    padding-left: 5px;

    border: $borderStyle;
  }
  .search-icon {
    padding: 3px 20px;
    color: #858585;
    border: $borderStyle;
    border-left-width: 0;
    box-sizing: border-box;
    height: 35px;
  }
}

.username {
  position: relative;
  top: 3.2px;
}
.avatar {
  border-radius: 30px;
  height: 45px;
}
</style>
