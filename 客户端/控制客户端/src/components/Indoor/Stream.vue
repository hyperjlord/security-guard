<template xmlns="http://www.w3.org/1999/html">
  <div ref="indoor-stream-div">
    <van-loading v-if="showLoading" type="circular" color="#1989fa">正在等待被控制端响应，请稍等</van-loading>
    <video id="stream-player"></video>
    <van-sticky container="indoor-stream-div" :offset-top="buttonGroupPos">
      <div id="button-group">
        <button class="button-style" @click="onPlayButtonClick">
          <img v-if="isPlaying" class="play-button-icon" src="../../assets/img/pause-button.svg" alt="暂停"/>
          <img v-else class="play-button-icon" src="../../assets/img/play-button-blue.svg" alt="播放"/>
        </button>
        <br/>
        <button class="button-style" @click="onPhoneButtonClick">
          <img class="button-icon" src="../../assets/img/phone-button.svg" alt="通话"/>
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
    parentMethods: Object,
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
      messageListenerOn: true,
    }
  },
  mounted() {
    if (typeof this.cameraId == "number") {
      this.initStreamPlayer();
      this.initButtonGroup();
    } else {
      this.$router.push({name: "Indoor"});
    }
    window.setTimeout(this.registerMessageListener, 1000);
    this.messageListenerOn = true;

  },
  beforeDestroy() {
    this.stopStream();
    this.messageListenerOn = false;
  },
  methods: {
    initButtonGroup() {
      this.buttonGroupPos = document.body.clientHeight - 160;
    },
    initStreamPlayer() {
      this.streamPlayer = document.getElementById('stream-player');
    },
    onPlayButtonClick() {
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
    registerMessageListener(){
      if(!this.messageListenerOn){
        return;
      }
      console.log("getMessage");
      this.$axios.request({
        url: "http://light.qingxu.website:10282/message/getMessage",
        methods: 'get',
      }).then((response) => {
        if (response.data.stateCode === this.$api.SUCCESS){
          this.$notify({ type: "danger", message: response.data.msg });
        }
        window.setTimeout(this.registerMessageListener, 1000);
      }).catch((error) => {
        window.setTimeout(this.registerMessageListener, 2000);
      })
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
              window.setTimeout(this.startStreamHeartBeat, 3000);
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
    onPhoneButtonClick() {
      let aLink = document.createElement("a");
      aLink.href = "tel:13131976736";
      let event;
      if (window.MouseEvent) event = new MouseEvent("click");
      else {
        event = document.createEvent("MouseEvents");
        event.initMouseEvent(
            "click",
            true,
            false,
            window,
            0,
            0,
            0,
            0,
            0,
            false,
            false,
            false,
            false,
            0,
            null
        );
      }
      aLink.dispatchEvent(event);
    },
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

.play-button-icon {
  margin: 0 0;
  display: block;
  width: 40px;
  height: 40px;
}
</style>
