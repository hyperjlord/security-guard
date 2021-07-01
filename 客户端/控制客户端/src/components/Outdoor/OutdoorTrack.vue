<template>
  <div>
    <van-row v-if="locationOr">
      <van-col span="8"
        ><van-button round type="info" @click="showPopup"
          >历史记录</van-button
        ></van-col
      >
      <!--  -->
      <van-popup
        v-model="historyShow"
        position="bottom"
        :style="{ height: '30%' }"
        ><van-picker
          title="时间选择"
          show-toolbar
          :columns="times"
          @confirm="onConfirm"
          @cancel="onCancel"
          @change="onChange"
        />
      </van-popup>
      <van-popup
        v-model="dateChoose"
        position="bottom"
        :style="{ height: '30%' }"
      >
        <van-datetime-picker
          v-model="currentDate"
          type="date"
          title="选择年月日"
          @confirm="timePick"
          :min-date="minDate"
          :max-date="maxDate"
          :formatter="formatter"
        />
      </van-popup>
      <van-col span="8" v-if="boundaryButtonOr"
        ><van-button round type="info" @click="showboundary"
          >查看安全范围</van-button
        ></van-col
      >
      <van-col span="8" v-if="boundaryOr"
        ><van-button round type="info" @click="closeBoundary"
          >关闭范围显示</van-button
        ></van-col
      >
      <!-- <van-col span="8"
        ><van-button round type="info">划定范围</van-button></van-col
      > -->
    </van-row>
    <van-row v-if="historyOr">
      <van-col span="8"
        ><van-button round type="info" @click="back">返回</van-button></van-col
      >
    </van-row>

    <div id="map-div" ref="map-div">
      <amap ref="amap-div" :zoom.sync="amap.zoom" :center.sync="amap.center">
        <amap-tool-bar />
        <amap-control-bar />
        <amap-scale />

        <amap-marker
          :position.sync="amap.wardPosition"
          :label="amap.wardLabel"
        />
        <amap-marker
          :position.sync="amap.startPosition"
          :label="amap.startLabel"
        />
        <amap-polygon v-if="boundaryOr" :path="amap.boundary" />
        <amap-polyline id="polyline" v-if="updateOr" :path="amap.wardpath" />
      </amap>
    </div>
  </div>
</template>

<script>
export default {
  name: "OutdoorTrack",
  props: {
    parentMethods: Object
  },
  data() {
    return {
      mapDiv: null,
      amap: {
        ref: null,
        zoom: 18,
        startPosition: [122.211896, 31.2865],
        wardPosition: [121.211896, 31.2865],
        center: [121.211896, 31.2865],
        bounds: [],
        tracks: [],
        startLabel: {
          content: "起始位置",
          direction: "bottom"
        },
        wardLabel: {
          content: "被监护人的位置",
          direction: "bottom"
        },

        wardpath: [],
        boundary: [],

        historyStartPos: [],
        historyEndPos: [],
        historyStartLabel: {
          content: "起始位置",
          direction: "bottom"
        },
        historyEndLabel: {
          content: "终点位置",
          direction: "bottom"
        },
        historyPath: [],
        historyZoom: 15,
        historyTracks: []
      },
      watchID: null,
      historyShow: false,
      times: ["最近两小时", "最近一天", "选择指定日期"],
      dateChoose: false,
      minDate: new Date(2020, 0, 1),
      maxDate: new Date(2025, 10, 1),
      currentDate: new Date(2021, 0, 17),
      chooseDateValue: {
        year: "",
        month: "",
        day: ""
      },
      boundaryOr: false,
      boundaryButtonOr: true,
      updateOr: true,
      clearWhenClose: true,
      locationOr: true,
      historyOr: false,
      timer:"",
      accountId:0,
    };
  },
  mounted() {
    this.initMapDiv();
    this.parentMethods.pauseSwipe();
    var timestamp = Date.parse(new Date()) / 1000;
    this.amap.center = this.amap.wardPosition;
    this.accountId =  sessionStorage.getItem("accountId");
    //获取account_id
    //定时器的变化
    this.$axios
      .get(this.$api.getTrackRecently + "?account_id="+this.accountId + "&time=" + timestamp)
      .then(res => {
        this.amap.tracks = res.data;
        this.amap.wardpath = new Array();
        for (var i = 0; i < res.data.length; i++) {
          var point = res.data[i];
          var jsonList = eval("(" + point + ")");
          var timeStamp = jsonList[0]["time"];
          var longitude = jsonList[0]["longitude"];
          var latitude = jsonList[0]["latitude"];
          if (i == 0) {
            var timeStr = new Date(parseInt(timeStamp) * 1000)
              .toLocaleString()
              .replace(/:\d{1,2}$/, " ");
            this.amap.startPosition = [longitude, latitude];
            var str = "起始位置" + timeStr;
            this.amap.startLabel.content = str;
          }
          if (i == res.data.length - 1) {
            var timeStr = new Date(parseInt(timeStamp) * 1000)
              .toLocaleString()
              .replace(/:\d{1,2}$/, " ");
            this.amap.wardPosition = [longitude, latitude];
            this.amap.wardLabel.content = "被监护人位置" + timeStr;
          }
          var pointPosition = [longitude, latitude];
          this.amap.wardpath.push(pointPosition);
        }
        this.amap.center = this.amap.wardPosition;
      })
      .catch(err => {
        console.log(err);
      });
    //以下是一个vue的坑
    var _this = this; //注意，要有，因为有箭头函数所以this作用域不在是vue，而是定时器。我们要调用的是vue中的函数
    this.timer = setInterval(() => {
      _this.getPositionBySeconds();
    }, 2000);
  },
  destroyed(){
   clearTimeout(this.timer);
  this.timer = null;
  },
  methods: {
    initMapDiv() {
      let that = this;
      this.amap.center = this.amap.myPosition;
      navigator.geolocation.getCurrentPosition(function(position) {
        that.amap.myPosition = [
          position.coords.longitude,
          position.coords.latitude
        ];
      });

      that.watchID = navigator.geolocation.watchPosition(function(position) {
        that.amap.myPosition = [
          position.coords.longitude,
          position.coords.latitude
        ];
        console.log(that.amap.myPosition);
      });
      this.$nextTick(() => {
        this.amap.ref = this.$refs["amap-div"];
      });
    },
    showPopup() {
      this.historyShow = true;
    },
    onConfirm(value, index) {
      var timestamp = Date.parse(new Date()) / 1000;
      var url = "";
      if (index == 0) {
        // 做2h的处理
        url = "getTrackAround2h";
      } else if (index == 1) {
        // 做一天的处理
        url = "getTrackAroundDay";
      } else if (index == 2) {
        //弹出选择器
        this.historyShow = false;
        this.dateChoose = true;
        return;
        // url="getTrackAroundDay";
        // var timeNow=this.chooseDateValue.year+"-"+this.chooseDateValue.month+"-"+this.chooseDateValue.day;
        // console.log(timeNow);
        // var date=new Date(timeNow);
        // timestamp=date.getTime()/1000;
        // console.log(timestamp);
        // alae();
      } else return;

      this.locationOr = false;
      this.historyOr = true;
      this.historyShow = false;
      this.$axios
        .get(
          "http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/" +
            url +
            "?account_id=" +this.accountId+
            "&time=" +
            timestamp
        )
        .then(res => {
          console.log("转换开始");
          this.amap.historyTracks = this.amap.tracks;
          this.amap.tracks = res.data;
          this.amap.historyPath = this.amap.wardpath;
          this.amap.wardpath = new Array();

          for (var i = 0; i < res.data.length; i++) {
            var point = res.data[i];
            var jsonList = eval("(" + point + ")");
            var timeStamp = jsonList[0]["time"];
            var longitude = jsonList[0]["longitude"];
            var latitude = jsonList[0]["latitude"];
            if (i == 0) {
              var timeStr = new Date(parseInt(timeStamp) * 1000)
                .toLocaleString()
                .replace(/:\d{1,2}$/, " ");
              this.amap.historyStartPos = this.amap.startPosition;
              this.amap.startPosition = [longitude, latitude];
              var str = "起始位置" + timeStr;
              this.amap.historyStartLabel.content = this.amap.startLabel.content;
              this.amap.startLabel.content = str;
            }
            if (i == res.data.length - 1) {
              var timeStr = new Date(parseInt(timeStamp) * 1000)
                .toLocaleString()
                .replace(/:\d{1,2}$/, " ");
              this.amap.historyEndPos = this.amap.wardPosition;
              this.amap.wardPosition = [longitude, latitude];
              this.amap.historyEndLabel.content = this.amap.wardLabel.content;
              this.amap.wardLabel.content = "";
              "被监护人位置" + timeStr;
            }
            var pointPosition = [longitude, latitude];
            this.amap.wardpath.push(pointPosition);
          }
          this.amap.center = this.amap.wardPosition;
          this.amap.zoom = this.amap.historyZoom;
          this.updateOr = false;
          this.$nextTick(() => {
            this.updateOr = true;
          });
        })
        .catch(err => {
          console.log(err);
        });
    },
    onChange(picker, value, index) {},
    onCancel() {
      this.historyShow = false;
    },
    timePick(value) {
      this.dateChoose = false;
      this.historyShow = false;
      var url = "getTrackAroundDay";
      var timeNow =
        this.chooseDateValue.year +
        "-" +
        this.chooseDateValue.month +
        "-" +
        this.chooseDateValue.day;
      var date = new Date(timeNow);
      var timestamp = date.getTime() / 1000 + 57600;
      this.locationOr = false;
      this.historyOr = true;
      this.$axios
        .get(
          "http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/" +
            url +
            "?account_id=" +this.accountId+
            "&time=" +
            timestamp
        )
        .then(res => {
          console.log("转换开始");
          this.amap.historyTracks = this.amap.tracks;
          this.amap.tracks = res.data;
          this.amap.historyPath = this.amap.wardpath;
          this.amap.wardpath = new Array();

          for (var i = 0; i < res.data.length; i++) {
            var point = res.data[i];
            var jsonList = eval("(" + point + ")");
            var timeStamp = jsonList[0]["time"];
            var longitude = jsonList[0]["longitude"];
            var latitude = jsonList[0]["latitude"];
            if (i == 0) {
              var timeStr = new Date(parseInt(timeStamp) * 1000)
                .toLocaleString()
                .replace(/:\d{1,2}$/, " ");
              this.amap.historyStartPos = this.amap.startPosition;
              this.amap.startPosition = [longitude, latitude];
              var str = "起始位置" + timeStr;
              this.amap.historyStartLabel.content = this.amap.startLabel.content;
              this.amap.startLabel.content = str;
            }
            if (i == res.data.length - 1) {
              var timeStr = new Date(parseInt(timeStamp) * 1000)
                .toLocaleString()
                .replace(/:\d{1,2}$/, " ");
              this.amap.historyEndPos = this.amap.wardPosition;
              this.amap.wardPosition = [longitude, latitude];
              this.amap.historyEndLabel.content = this.amap.wardLabel.content;
              this.amap.wardLabel.content = "";
              "被监护人位置" + timeStr;
            }
            var pointPosition = [longitude, latitude];
            this.amap.wardpath.push(pointPosition);
          }
          this.amap.center = this.amap.wardPosition;
          this.amap.zoom = this.amap.historyZoom;
          this.updateOr = false;
          this.$nextTick(() => {
            this.updateOr = true;
          });
        })
        .catch(err => {
          console.log(err);
        });
    },
    formatter(type, value) {
      if (type === "year") {
        this.chooseDateValue.year = value; // 可以拿到当前点击的数值
        return `${value}年`;
      } else if (type === "month") {
        this.chooseDateValue.month = value;
        return `${value}月`;
      }
      this.chooseDateValue.day = value;
      return `${value}日`;
    },

    getLocalTime(nS) {
      return new Date(parseInt(nS) * 1000)
        .toLocaleString()
        .replace(/:\d{1,2}$/, " ");
    },
    //获取追踪位置处理数据
    getPositionBySeconds() {
      var timestamp = Date.parse(new Date()) / 1000;
      this.$axios
        .get(
          //"http://light.qingxu.website:25080/protect/getTrackBySeconds" +
            "http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getTrackBySeconds"+
            "?account_id=" +this.accountId+
            "&time=" +
            timestamp
        )
        .then(res => {
          //直接取用
          if (
            this.amap.wardPosition[0] == res.data[0]["longitude"] &&
            this.amap.wardPosition[1] == res.data[0]["latitude"]
          ) {
            console.log(res.data[0]["time"]);
            return;
          } else {
            var pointArray = [
              res.data[0]["longitude"],
              res.data[0]["latitude"]
            ];
            this.amap.wardPosition = pointArray;
            var timeStr = new Date(parseInt(res.data[0]["time"]) * 1000)
              .toLocaleString()
              .replace(/:\d{1,2}$/, " ");
            this.amap.wardLabel.content = this.amap.wardLabel.content =
              "被监护人位置" + timeStr;

            this.$set(
              this.amap.wardpath,
              this.amap.wardpath.length,
              pointArray
            );
            var currentList = this.amap.wardpath;
            this.amap.wardpath = new Array();
            this.amap.wardpath = currentList;

            console.log("pathSize:" + this.amap.wardpath.length);
            console.log("位置已变动" + res.data[0]["time"]);
            this.updateOr = false;
            this.$nextTick(() => {
              this.updateOr = true;
            });
          }
        })
        .catch(err => {
          console.log(err);
        });
    },
    clear() {},
    back() {
      this.amap.tracks = this.amap.historyTracks;
      this.amap.wardpath = this.amap.historyPath;

      this.amap.startPosition = this.amap.historyStartPos;
      this.amap.startLabel.content = this.amap.historyStartLabel.content;

      this.amap.wardPosition = this.amap.historyEndPos;
      this.amap.wardLabel.content = this.amap.historyEndLabel.content;

      this.amap.center = this.amap.wardPosition;
      this.amap.zoom = 18;

      this.locationOr = true;
      this.historyOr = false;

      this.updateOr = false;
      this.$nextTick(() => {
        this.updateOr = true;
      });
    },
    //展示boundary
    showboundary() {
      this.$axios
        .get(
          "http://stu-project-test.qingxu.website:10008/OUTDOOR-PROVIDER/protect/getBoundaryById?account_id="+this.accountId
        )
        .then(res => {''
          console.log("boundary开始显示");
          this.amap.boundary = new Array();
          for (var i = 0; i < res.data.length; i++) {
            var boundaryList = res.data[i]["boundary_set_json"];
            var jsonList = eval("(" + boundaryList + ")");
            for (var j = 0; j < jsonList.length; j++) {
              var longitude = jsonList[j]["longitude"];
              var latitude = jsonList[j]["latitude"];
              var pointPosition = [longitude, latitude];
              this.amap.boundary.push(pointPosition);
            }
          }
          this.amap.zoom = 14;
          this.amap.center = this.amap.wardPosition;
          this.boundaryOr = false;
          this.$nextTick(() => {
            this.boundaryOr = true;
            this.boundaryButtonOr = false;
          });
        })
        .catch(err => {
          console.log(err);
        });
    },
    closeBoundary() {
      this.boundaryOr = true;
      this.boundaryButtonOr = false;
      this.amap.zoom = 18;
      this.amap.center = this.amap.wardPosition;
      this.$nextTick(() => {
        this.boundaryOr = false;
        this.boundaryButtonOr = true;
      });
    }
  }
};
</script>

<style scoped>
#map-div {
  height: calc(100vh - 140px);
  width: 100vw;
  z-index: 0;
}
#historyMap-div {
  height: calc(100vh - 140px);
  width: 100vw;
  z-index: 0;
}
</style>
