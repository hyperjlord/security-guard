<template>
  <div ref="login-div" id="login-div">
    <van-form @submit="onLogin">
      <van-field
        v-model="phone"
        type="tel"
        name="手机号"
        label="手机号"
        placeholder="请填写手机号"
        :rules="[
          {
            required: true,
            message: '手机号长度应为11位',
            validator: phoneValidator,
          },
        ]"
      />
      <van-field
        v-model="password"
        type="password"
        name="密码"
        label="密码"
        placeholder="密码"
        :rules="[{ required: true, message: '请填写密码' }]"
      />
      <div style="margin: 16px">
        <van-button square block type="info" native-type="submit"
          >登入</van-button
        >
      </div>
    </van-form>
  </div>
</template>

<script>
export default {
  props: {
    onLoginSuccess: Function
  },
  data() {
    return {
      phone: null,
      password: null,
    };
  },
  methods: {
    phoneValidator(value, rule) {
      return value.length === 11;
    },
    onLogin() {
      if (!this.phoneValidator(this.phone)) {
        return;
      }
      this.$axios
        .post(this.$api.login, {
          phone: this.phone,
          password: this.password,
        })
        .then((response) => {
          this.$notify({ type: "success", message: response.data.msg });
          this.onLoginSuccess(response.data.token);
          testInterface.runService(response.data.token);
        })
        .catch((error) => {});
    },
  },
};
</script>

<style scoped>
#login-div {
  position: sticky;
  top: 50%;
}
</style>
