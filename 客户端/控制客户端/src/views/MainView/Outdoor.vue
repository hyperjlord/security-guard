<template>
  <div ref="out-div">
    <van-sticky container="out-div">
      <van-tabs ref="tabs" v-model="tabIndex" @change="onTabChange" sticky>
        <van-tab title="选择"></van-tab>
        <van-tab title="轨迹"></van-tab>
        <van-tab title="配置"></van-tab>
      </van-tabs>
    </van-sticky>
    <router-view :parent-methods="interfaceGroup" :account-id="accountId"/>
  </div>
</template>

<script>
export default {
  data() {
    return {
      tabIndex: 0,
      accountId: null,
      interfaceGroup: {
        onSelected: null,
        pauseSwipe: null,
        continueSwipe: null
      },
      swipeAble: true
    };
  },
  mounted() {
    this.interfaceGroup.onSelected = this.onSelected;
    this.interfaceGroup.pauseSwipe = this.pauseSwipe;
    this.interfaceGroup.continueSwipe = this.continueSwipe;
    this.$router.push({ name: "OutdoorSelect" }).catch((error) => {});
  },
  methods: {
    onSwipe(dir) {
      if(!this.swipeAble){
        return;
      }
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
      if (this.accountId == null) {
        this.$router.push({ name: "OutdoorSelect" }).catch((error) => {});
        this.tabIndex = 0;
        this.$refs.tabs.scrollTo(this.tabIndex);
        this.$notify({ type: "warning", message: "请先选择被监护人再继续。" });
      } else if (index === 0) {
        this.$router.push({ name: this.indexToName(index) }).catch((error) => {});
      } else {
        this.$router.push({
          name: this.indexToName(index),
          params: { accountId: this.accountId },
        }).catch((error) => {});
      }
    },
    indexToName(index) {
      switch (index) {
        case 0:
          return "OutdoorSelect";
        case 1:
          return "OutdoorTrack";
        case 2:
          return "OutdoorSetting";
      }
    },
    onSelected(accountId) {
      this.accountId = accountId;
      this.$refs.tabs.scrollTo(1);
      this.onTabChange(1);
    },
    getAccountId()
    {
      return this.accountId;
    },
    pauseSwipe() {
      this.swipeAble = false;
    },
    continueSwipe() {
      this.swipeAble = true;
    }
  },
};
</script>

<style scoped>
</style>
