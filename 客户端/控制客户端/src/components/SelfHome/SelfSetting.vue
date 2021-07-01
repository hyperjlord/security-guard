<template>
  <div ref="self-setting-div"
       style="text-align:left;min-height: calc(100vh - 94px);max-height: calc(100vh - 94px);overflow: scroll">
    <van-cell-group title="基本信息">
      <div style="margin:4px;padding:4px;border-radius: 10px;border:2px solid gray;">
        <h5><van-icon name="info" color="#ee0a24" /> ID：<span>{{userInfo.id}}</span></h5>
        <h5><van-icon name="manager" color="#ee0a24" /> 昵称：<span>{{userInfo.nickname}}</span></h5>
        <h5><van-icon name="phone" color="#ee0a24" /> 手机号：<span>{{userInfo.phone}}</span></h5>
        <div style="position: relative">
          <h5 style="display: inline"><van-icon name="warning" color="#ee0a24" /> 我的关联码：</h5>
          <van-button type="primary" style="position: absolute;right:4px " @click="showShareCode">查看关联码</van-button>
        </div>
      </div>
    </van-cell-group>
    <van-cell-group title="被监护人管理">
      <van-collapse v-model="activeNames2">
        <van-collapse-item title="我的被监护人" name="2">
        <van-swipe-cell v-for="(item) in userInfo.wards" style="border-bottom: 1px solid gray ">
          <van-cell :title="'ID: '+item.accountId" :value="'昵称: '+item.name">
            <template #right>
              <van-button square text="删除" type="danger" class="delete-button" />
            </template>
          </van-cell>
        </van-swipe-cell>
        </van-collapse-item>
        <van-collapse-item title="关联被监护人" name="1">
          <van-form @submit="bindWard">
            <van-field
                v-model="shareCode"
                name="shareCode"
                label="关联码"
                placeholder="请输入要绑定的被监护人的关联码"
                :rules="[{ required: true, message: '关联码不应为空' }]"
            />
            <van-field
                v-model="wardPhone"
                name="wardPhone"
                label="被监护人号码"
                placeholder="请输入被监护人的电话号码"
                :rules="[{ required: true, message: '请填写被监护人手机号' }]"
            />
            <van-field
                v-model="guardianName"
                name="guardianName"
                label="监护人身份"
                placeholder="请输入监护人的身份，例：父亲"
                :rules="[{ required: true, message: '请填写监护人身份' }]"
            />
            <van-field
                v-model="wardName"
                name="wardName"
                label="被监护人身份"
                placeholder="请输入被监护人的身份，例：儿子"
                :rules="[{ required: true, message: '请填写被监护人身份' }]"
            />
            <div style="margin: 16px;">
              <van-button
                  round block type="info"
                  native-type="submit"
                  color="linear-gradient(to right, #d46a6a, #801515)">
                关联被监护人
              </van-button>
            </div>
          </van-form>
        </van-collapse-item>
      </van-collapse>
    </van-cell-group>
    <van-cell-group title="用户信息设置">
      <van-collapse v-model="activeNames">
        <van-collapse-item name="2">
          <template #title>
            <div>修改昵称
              <van-icon name="question-o"/>
            </div>
          </template>
          <van-form @submit="submitNickName">
            <van-field
                v-model="nickname"
                name="nickName"
                label="昵称"
                placeholder="请输入新昵称"
                :rules="[{ required: true, message: '请填写用户名' }]"
            />
            <div style="margin: 16px;">
              <van-button
                  round block type="info"
                  color="linear-gradient(to right, #6B949E, #0F434F)"
                  native-type="submit">提交
              </van-button>
            </div>
          </van-form>
        </van-collapse-item>
        <van-collapse-item name="3">
          <template #title>
            <div>
              修改登陆信息
              <van-icon name="contact"/>
            </div>
          </template>
          <van-form @submit="submitLoginInfo">
            <van-field
                v-model="phone"
                name="phone"
                label="手机号"
                placeholder="请输入新的手机号"
                :rules="[{ required: false, message: '请填写用户名' }]"
            />
            <van-field
                v-model="password"
                type="password"
                name="password"
                label="密码"
                placeholder="请输入新的密码"
                :rules="[{ required: false, message: '请填写密码' }]"
            />
            <div style="margin: 16px;">
              <van-button
                  round block type="info"
                  color="linear-gradient(to right, #90bf60, #437313)"
                  native-type="submit">提交
              </van-button>
            </div>
          </van-form>
        </van-collapse-item>
      </van-collapse>
    </van-cell-group>

  </div>
</template>

<script>
import {Dialog, Toast} from "vant";

export default {
  mounted() {
    this.$axios.get(this.$api.getUserInfo)
    .then((response)=>{
      console.log(response.data);
      this.userInfo=response.data;
    })
  },
  data() {
    return {
      activeNames2: ['1'],
      activeNames: ['1'],
      userInfo:{},
      guardianName: '',
      wardName: '',
      wardPhone: '',
      shareCode: '',
      nickname: '',
      phone: '',
      password: '',
    }
  },
  methods: {
    showShareCode() {
      let that=this;
      this.$axios.get(this.$api.refreshShareCode)
      .then((response)=>{
        console.log(response.data.msg);
        let shareCode=response.data.shareCode;
        Dialog.alert({
          title: '您的关联码为',
          message: shareCode+'\n\n'+'（关联码用来关联被监护人，请勿泄露给他人）',
          theme: 'round-button',
        }).then(() => {
          // on close
        });
      }).catch((err)=>{})
    },
    logOff() {
      localStorage.removeItem("token");
      this.$router.push({name: "Login"}).catch((error) => {
      });
    },
    bindWard() {
      console.log(this.shareCode);
      console.log(this.wardPhone);
      this.$axios.post(this.$api.applyShareCode, {
        shareCode: this.shareCode,
        phone: this.wardPhone,
        guardianName: this.guardianName,
        wardName: this.wardName,
      })
          .then((response) => {
            Toast(response.data.msg);
          })
          .catch((err) => {
            console.log(err);
          })
    },
    submitNickName() {
      this.$axios.put(this.$api.changeNickname, {
        nickname: this.nickname,
      })
      .then((response) => {
        Toast(response.data.msg)
      })
      .catch((err) => {});
    },
    submitLoginInfo() {
      this.$axios.put(this.$api.changeLoginInfo,{
        phone:this.phone,
        password:this.password,
      })
      .then((response)=>{
        Toast(response.data.msg);
        setTimeout(this.logOff, 2000);
      })
      .catch((err)=>{});
    }
  }
}
</script>

<style scoped>
</style>