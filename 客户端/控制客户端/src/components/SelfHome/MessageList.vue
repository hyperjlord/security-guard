<template>
  <div ref="message-list-div" style="text-align:left">
    <van-pull-refresh style="min-height: calc(100vh - 94px);max-height: calc(100vh - 94px);" v-model="isLoading"
                      @refresh="onRefresh" success-text="刷新成功">
      <van-cell-group title="被监护人历史预警记录">
        <van-cell v-for="message in messages" :title="message.content" :value="message.ward" size="large"
                  :label="message.time"/>
      </van-cell-group>
    </van-pull-refresh>
  </div>
</template>

<script>
import {Toast} from "vant";

export default {
  mounted: function () {
    this.onRefresh();
  },
  data() {
    return {
      messages: [],
      isLoading: false
    }
  },
  methods: {
    onRefresh() {
      this.$axios
          .get(this.$api.getWarningRecords)
          .then((response) => {
            this.isLoading = false;
            this.messages = response.data;
            Toast("刷新成功");
            console.log("预警信息：");
            console.log(this.messages);
          })
          .catch((error) => {
          });
    }
  }
}
</script>

<style scoped>
</style>