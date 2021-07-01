<template>
  <div ref="danger-area-div">
    <van-loading v-if="showLoading" type="circular" color="#1989fa">正在等待被控制端响应，请稍等</van-loading>
    <video id="stream-player"></video>
    <van-sticky :offset-top="buttonGroupPos">
      <div id="button-group">
        <button class="button-style" @click="onSaveButtonClick">
          <img class="button-icon" src="../../assets/img/save-button.svg" alt="保存"/>
        </button>
      </div>
    </van-sticky>
  </div>
</template>

<script>
import flv from 'flv.js';

export default {
  props: {
    accountId: Number,
    cameraId: Number,
    save: Function,
  },
  data() {
    return {
      showLoading: false,
      streamPlayer: null,
      waitCount: 0,
      continueHeartBeat: false,
      flvPlayer: null,
      isPlaying: false,
      buttonGroupPos: 0,
    }
  },
  mounted() {
    if (typeof this.cameraId == "number") {
      this.initStreamPlayer();
      this.initButtonGroup();
      this.registerListeners();
      this.$toast("请点击摄像头画面来绘制危险区域");
      this.switchStream();
    } else {
      this.$notify({ type: "warning", message: "请选择摄像头" });
      this.$router.push({name: "Indoor"});
    }
  },
  beforeDestroy() {
    this.stopStream();
    this.removeListeners();
  },
  methods: {
    initButtonGroup() {
      this.buttonGroupPos = document.body.clientHeight - 100;
    },
    initStreamPlayer() {
      this.streamPlayer = document.getElementById('stream-player');
    },
    switchStream() {
      if (this.isPlaying) {
        this.isPlaying = false;
        this.stopStream();
      } else {
        this.isPlaying = true;
        this.startStream();
      }
    },
    startStream() {
      this.showLoading = true;
      this.$axios.request({
        url: this.$api.launchStream,
        method: 'get',
        params: {
          wardId: this.accountId,
          cameraId: this.cameraId
        }
      }).then((response) => {
        if (this.flvPlayer == null) {
          this.flvPlayer = flv.createPlayer({
            type: 'flv',
            isLive: true,
            cors: true,
            url: response.data.httpurl
          });
          this.flvPlayer.attachMediaElement(this.streamPlayer);
        }
        if (response.data.stateCode === this.$api.SUCCESS) {
          this.playStreamPlayer();
        } else if (response.data.stateCode === this.$api.WAITING) {
          this.waitCount = 0;
          window.setTimeout(this.waitIndoorClient, 500);
        }
      }).catch((error) => {
        this.showLoading = false;
        this.$notify({type: "warning", message: "网络异常请检查后重试"});
      });
    },
    waitIndoorClient() {
      this.waitCount++;
      if (this.waitCount < 60) {
        this.$axios.request({
          url: this.$api.streamHeartBeat,
          method: 'get',
          params: {
            cameraId: this.cameraId
          },
        }).then((response) => {
          switch (response.data.stateCode) {
            case this.$api.SUCCESS:
              this.playStreamPlayer();
              break;
            case this.$api.WAITING:
              window.setTimeout(this.waitIndoorClient, 500);
              break;
            default:
              this.console.log(response);
          }
        }).catch((error) => {
          this.showLoading = false;
        });
      } else {
        this.showLoading = false;
      }

    },
    playStreamPlayer() {
      this.flvPlayer.load();
      this.flvPlayer.play();
      this.showLoading = false;
      this.continueHeartBeat = true;
      this.startStreamHeartBeat();
    },
    startStreamHeartBeat() {
      if (this.continueHeartBeat) {
        this.$axios.request({
          url: this.$api.streamHeartBeat,
          method: 'get',
          params: {
            cameraId: this.cameraId
          },
        }).then((response) => {
          switch (response.data.stateCode) {
            case this.$api.SUCCESS:
              window.setTimeout(this.startStreamHeartBeat, 500);
              break;
            default:
              this.console.log(response);
          }
        }).catch((error) => {
        });
      }
    },
    stopStream() {
      this.continueHeartBeat = false;
      if (this.flvPlayer != null) {
        this.flvPlayer.pause();
        this.flvPlayer.unload();
      }
    },
    registerListeners(){

    },
    removeListeners(){

    }
  }
}
</script>

<style scoped>
#button-group {
  float: right;
}

.button-icon {
  display: block;
  margin: -1px -2px;
  width: 45px;
  height: 45px;
}

.button-style {
  width: 52px;
  height: 52px;
  background-color: rgba(239, 239, 239, 0.71);
  border-radius: 50%;
  border: transparent 0;
}
</style>