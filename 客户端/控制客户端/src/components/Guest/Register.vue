<template>
  <div ref="register-div" id="register-div">
    <van-form @submit="onRegister">
      <van-field
        v-model="phone"
        type="tel"
        name="手机号"
        label="手机号"
        placeholder="请填写手机号"
        :rules="[{ required: true, message: '手机号长度应为11位', validator: phoneValidator }]"
      />
      <van-field
        v-model="nickname"
        name="昵称"
        label="昵称"
        placeholder="请填写昵称"
        :rules="[{ required: true, message: '请填写昵称' }]"
      />
      <van-field
        v-model="password"
        type="password"
        name="密码"
        label="密码"
        placeholder="密码"
        :rules="[{ required: true, message: '密码长度需大于6位', validator: passValidator }]"
      />
      <van-field
        v-model="password1"
        type="password"
        name="密码确认"
        label="密码确认"
        placeholder="密码"
        :rules="[{ required: true, message: '两次密码不匹配，请重新输入', validator: pass1Validator }]"
      />
      <div style="margin: 16px">
        <van-button square block type="info" native-type="submit"
          >注册</van-button
        >
      </div>
    </van-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      phone: null,
      password: null,
      password1: null,
      nickname: null,
    };
  },
  methods: {
    phoneValidator(value, rule){
      return (value.length == 11);
    },
    passValidator(value, rule){
      return (value.length >= 6);
    },
    pass1Validator(value, rule){
      return (value == this.password);
    },
    onRegister() {
      if(!this.phoneValidator(this.phone) || 
      !this.passValidator(this.password) || 
      !this.pass1Validator(this.password1)){
        return;
      }
      this.$axios
        .post(this.$api.register,{
          phone: this.phone,
          nickname: this.nickname,
          password: this.password
        })
        .then((response) => {
          this.$notify({ type: "success", message: response.data.msg });
          this.$emit("login-success", response.data.token);
        })
        .catch((error) => {})
    },
  },
};
</script>

<style scoped>
#register-div {
  position: sticky;
  top: 50%;
}
</style>
