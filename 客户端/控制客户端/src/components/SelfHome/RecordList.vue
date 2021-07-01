<template>
  <div ref="record-list-div" style="text-align:left">
    <van-pull-refresh style="min-height: calc(100vh - 160px);max-height: calc(100vh - 160px);overflow: scroll" v-model="isLoading"
                      @refresh="onRefresh" success-text="刷新成功">
      <van-cell-group title="被监护人历史录音文件">
        <van-cell v-for="(item) in audioFileInfos"
                  :title="item.fileName"
                  :value="item.lastModifiedTime"
                  size="large"
                  @click="playMusic(item.fileName,item.srcUrl)"/>
      </van-cell-group>
    </van-pull-refresh>
    <aplayer
        :key="songInfo.src"
        :conrols="true"
        :music="songInfo"
        :float="true"
        :theme="'#000000'"
        style="background-color: steelblue;color: gold;"
    ></aplayer>
  </div>
</template>

<script>
import Aplayer from 'vue-aplayer'
import {Toast} from "vant";

export default {
  components: {
    Aplayer
  },
  mounted: function () {
    this.onRefresh();
  },
  data() {
    return {
      audioFileInfos: [],
      isLoading: false,
      targetFileName: '',
      songInfo: {
        title: '东西（Cover：林俊呈）',
        artist: '纳豆',
        src: 'https://stu-project.qingxu.website:20511/audio/2/20210531-085555.wav?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=HCI_admin%2F20210608%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20210608T074747Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=381a2ee13f6cbb1c1895bca23cbcd5e6d4257a92009419db9dcc4059c439b2fd',
        pic: 'https://p1.music.126.net/5zs7IvmLv7KahY3BFzUmrg==/109951163635241613.jpg?param=300y300', // prettier-ignore
        lrc: 'https://cdn.moefe.org/music/lrc/thing.lrc',
      },
    }
  },
  methods: {
    onRefresh() {
      let that = this;
      this.$axios
          .get(this.$api.getAudioFileInfos)
          .then((response) => {
            that.isLoading = false;
            that.audioFileInfos = response.data;
            Toast("刷新成功");
          })
          .catch((error) => {
          });
    },
    playMusic(title, srcUrl) {
      this.songInfo.src = srcUrl;
      this.songInfo.title = title;
      this.songInfo.artist = '456'
      console.log(this.songInfo.src);
    }
  }
}
</script>

<style scoped>
.aplayer {
  position: fixed;
  width: 100%;
  bottom: 50px;
}
</style>
