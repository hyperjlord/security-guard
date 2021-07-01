<template>
  <div id="guest">
    <router-view ref="child" :on-login-success="onLoginSuccess"/>
    <van-sticky :offset-top="dividerOffset">
      <van-divider
        :style="{ color: '#1989fa', borderColor: '#1989fa', padding: '0 16px' }"
        id="divider"
      >
        <h5 @click="onLoginClick">登入</h5>
        <h5 id="vertical-divider">|</h5>
        <h5 @click="onRegisterClick">注册</h5>
      </van-divider>
    </van-sticky>
  </div>
</template>

<script>
export default {
  data() {
    return {
      dividerOffset: 0,
    };
  },
  mounted() {
    this.dividerOffset = document.body.clientHeight - 80;
  },
  methods: {
    getCurrentName(){
      return this.$refs.child.routeName;
    },
    onLoginClick() {
      if (this.getCurrentName() !== "Login") {
        this.$router.push({ name: "Login" }).catch((error) => {});    
      }
    },
    onRegisterClick() {
      if (this.getCurrentName() !== "Register") {
        this.$router.push({ name: "Register" }).catch((error) => {});
      }
    },
    onLoginSuccess(token) {
      localStorage.setItem("token", token);
      this.$router.push({ name: "MainView" }).catch((error) => {});
    }
  },
};
</script>


<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#vertical-divider {
  margin-left: 10px;
  margin-right: 10px;
}

#guest {
  padding: 10px 10px 10px 10px;
}
</style>
