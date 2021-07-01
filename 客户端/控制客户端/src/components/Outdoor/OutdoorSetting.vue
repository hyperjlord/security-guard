<template>
  <div ref="outdoor-setting-div" class="align-left">
    <van-cell-group title="控制端被监护人设定" class="align-left">
      <van-cell title="安全等级设定" label="安全范围扩散等级：越高扩散范围越小">
        <!-- 使用 right-icon 插槽来自定义右侧图标 -->
     <van-stepper v-model="level" min="1" max="3" />
      </van-cell>
          </van-cell-group>
        <van-cell-group title="被监护端紧急人联系方式" class="align-left">
            <van-field
    v-model="name"
    name="姓名"
    label="姓名"
    placeholder="姓名"
    :rules="[{ required: true, message: '请填写密码' }]"
  />
        <van-field
    v-model="phone"
    name="电话号码"
    label="电话号码"
    placeholder="电话号码"
    :rules="[{ required: true, message: '请填写电话号码' }]"
  />
  <div style="margin: 16px;">
    <van-button round block type="info" native-type="submit" @click="submit">提交</van-button>
  </div>
    </van-cell-group>
  </div>
</template>

<script>
import { Notify } from 'vant';
export default {
  props: {
    parentMethods: Object
  },
  data() {
    return {
      isAIon: null,
      AISensitivity: null,
      showSlider: false,
      level:0,
      phone:"",
      name:"",
    }
  },
  mounted() {
    this.pullSettingsFromCloud();
  },
  methods: {
    pullSettingsFromCloud() {
      this.isAIon = true;
      this.AISensitivity = 50;
    },
    onSettingChange() {

    },
    activeSlider() {
      this.showSlider = true;
    },
    setDangerArea() {

    },
    setScheduledReminders() {

    },
    continueSwipe() {
      window.setTimeout(this.parentMethods.continueSwipe, 30);
    },
    pauseSwipe() {
      this.parentMethods.pauseSwipe();
    },
    submit(){
      var sendObject=new Object();
      sendObject["level"]=this.level;
      sendObject["name"]=this.name;
      sendObject["phone"]=this.phone;
      var setting=JSON.stringify(sendObject);
      var timeStamp=Date.parse(new Date());
      var accountId =  sessionStorage.getItem("accountId");
       this.$axios
        .post("http://stu-project-test.qingxu.website:10008/GUARDIAN-SERVICE/guardian/outdoor/setting", {
          ward: accountId,
          timeStamp: timeStamp,
          setting: setting
        })
        .then((response) => {
            this.level=1;
      this.phone="";
      this.name="";
      this.$notify({ type: "success", message: "提交成功" });
        })
        .catch((error) => {});
    },
    
  }
}
</script>

<style scoped>
.align-left {
  text-align: left;
}

#ai-sens-label {
  font-size: 16px;
}
#ai-sens-slider {
  margin-bottom: 20px;
  margin-top: 0;
}
</style>
