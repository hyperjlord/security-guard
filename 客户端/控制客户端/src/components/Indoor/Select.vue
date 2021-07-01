<template>
  <div ref="indoor-select-div">
    <van-collapse v-model="accountIndex" accordion>
      <van-collapse-item
        v-for="account in accountCameraList"
        :key="account.accountId"
        :title="account.name"
      >
        <van-button
          v-for="camera in account.cameraList"
          :key="camera.cameraId"
          type="default"
          @click="onCameraSelected(account.accountId, camera.cameraId)"
          block
        >
          {{ camera.cameraName }}
        </van-button>
      </van-collapse-item>
    </van-collapse>
  </div>
</template>

<script>
import defVal from "../../assets/config/default-value.js";
export default {
  props: {
    parentMethods: Object,
  },
  mounted() {
    this.loadCameraList();
  },
  data() {
    return {
      accountIndex: 0,
      accountCameraList: defVal.accountCameraList,
    };
  },
  methods: {
    loadCameraList() {
      this.$axios
        .get(this.$api.getCameraList)
        .then((response) => {
          this.accountCameraList = response.data.accountCameraList;
        })
        .catch((error) => {});
    },
    onCameraSelected(accountId, cameraId){
      this.parentMethods.onSelected(accountId, cameraId);
    }
  },
};
</script>

<style scoped>
</style>
