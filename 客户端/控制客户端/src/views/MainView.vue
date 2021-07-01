<template>
  <div id="main-view">
    <router-view ref="nextView"/>
    <div id="main-tab">
      <van-tabbar v-model="pageIndex" @change="mainTabChange">
        <van-tabbar-item icon="home-o">室内</van-tabbar-item>
        <van-tabbar-item icon="location-o">室外</van-tabbar-item>
        <van-tabbar-item icon="user-circle-o">我的</van-tabbar-item>
      </van-tabbar>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      pageIndex: 0,
      startx: 0,
      starty: 0,
      swipelistenerList: [],
    };
  },
  mounted() {
    if (!this.loginCheck()) {
      this.$router.push({name: "Login"}).catch((error) => {
      });
      return;
    }
    this.addListeners();
    console.log(document.getElementsByClassName('van-tabbar')[0].offsetHeight);
  },
  beforeDestroy() {
    document.removeEventListener("touchstart", this.touchStartListener);
    document.removeEventListener("touchend", this.touchEndListener);
  },
  methods: {
    mainTabChange() {
      this.$router.push({name: this.pageIndexToName(this.pageIndex)}).catch((error) => {
      });
    },
    setMainTab(str) {
      this.pageName = str;
    },
    getAngle(angx, angy) {
      return (Math.atan2(angy, angx) * 180) / Math.PI;
    },
    getDirection(startx, starty, endx, endy) {
      let divx = endx - startx;
      let divy = endy - starty;
      let dir = 0;

      //滑动距离太短
      if (Math.abs(divx) < 70 && Math.abs(divy) < 70) {
        return dir;
      }

      var angle = this.getAngle(divx, divy);
      if (angle >= -135 && angle <= -45) {
        //上
        dir = 1;
      } else if (angle > 45 && angle < 135) {
        //下
        dir = 2;
      } else if (
          (angle >= 135 && angle <= 180) ||
          (angle >= -180 && angle < -135)
      ) {
        //左
        dir = 3;
      } else if (angle >= -45 && angle <= 45) {
        //右
        dir = 4;
      }

      return dir;
    },
    onSwipe(dir) {
      if (this.$refs.nextView.onSwipe != null) {
        this.$refs.nextView.onSwipe(dir);
      }
    },
    addListeners() {
      var that = this;
      //手指接触屏幕
      document.addEventListener(
          "touchstart",
          this.touchStartListener,
          false
      );
      //手指离开屏幕
      document.addEventListener(
          "touchend",
          this.touchEndListener,
          false
      );
    },
    touchStartListener(e) {
      this.startx = e.touches[0].pageX;
      this.starty = e.touches[0].pageY;
    },
    touchEndListener(e) {
      let endx = e.changedTouches[0].pageX;
      let endy = e.changedTouches[0].pageY;
      let dir = this.getDirection(this.startx, this.starty, endx, endy);
      this.onSwipe(dir);
    },
    loginCheck() {
      return (localStorage.getItem("token") != null);
    },
    pageIndexToName(index) {
      switch (index) {
        case 0:
          return "Indoor";
        case 1:
          return "Outdoor";
        case 2:
          return "SelfHome";
        default:
          return "SelfHome";
      }
    }
  },
};
</script>


<style>
#main-view {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

#main-tab {
  margin-bottom: 0;
}
</style>
