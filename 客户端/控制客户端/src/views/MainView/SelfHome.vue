<template>
  <div ref="message-div">
    <van-sticky container="message-div">
      <van-tabs ref="tabs" v-model="tabIndex" @change="onTabChange" sticky>
        <van-tab title="消息"></van-tab>
        <van-tab title="录音"></van-tab>
        <van-tab title="设置"></van-tab>
      </van-tabs>
    </van-sticky>
    <router-view/>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tabIndex: 0,
      cameraId: null,
      interfaceGroup: {
        onSelected: null,
      }
    };
  },
  mounted() {
    this.interfaceGroup.onSelected = this.onSelected;
    this.$router.push({ name: "MessageList" }).catch((error) => {});
  },
  methods: {
    onSwipe(dir) {
      switch (dir) {
        case 0:
        case 1:
        case 2:
          return;
        case 3:
          if (this.tabIndex < 2) {
            this.tabIndex++;
          }
          break;
        case 4:
          if (this.tabIndex > 0) {
            this.tabIndex--;
          }
          break;
        default:
          return;
      }
      this.$refs.tabs.scrollTo(this.tabIndex);
      this.onTabChange(this.tabIndex);
    },
    onTabChange(index, title) {
        this.$router.push({ name: this.indexToName(index) }).catch((error) => {});
    },
    indexToName(index) {
      switch (index) {
        case 0:
          return "MessageList";
        case 1:
          return "RecordList";
        case 2:
          return "SelfSetting";
      }
    },
  },
};
</script>

<style scoped>
</style>