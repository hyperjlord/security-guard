<template>
  <div ref="indoor-setting-div" class="align-left">
    <van-cell-group title="危险预警" class="align-left">
      <van-cell title="AI预警" label="智能识别摄像头视野内的危险威胁，主动预警">
        <!-- 使用 right-icon 插槽来自定义右侧图标 -->
        <template #right-icon>
          <van-switch v-model="isAIon" @change="onSettingChange" size="24px"/>
        </template>
      </van-cell>
      <van-cell v-if="isAIon" title="AI预警灵敏度" label="灵敏度越高越容易触发报警" @click="activeSlider">
        <!-- 使用 right-icon 插槽来自定义右侧图标 -->
        <template #right-icon>
          <label id="ai-sens-label">{{ AISensitivity }}</label>
        </template>
      </van-cell>
      <van-slider v-if="isAIon" v-model="AISensitivity" :min="0" :max="100"
                  @change="continueSwipe();onSettingChange()"
                  @input="pauseSwipe"
                  id="ai-sens-slider"/>
      <van-cell v-if="isAIon" title="设定危险区域" label="当危险动作（如：摔倒）发生在危险区域内会触发报警" @click="setDangerArea">
        <!-- 使用 right-icon 插槽来自定义右侧图标 -->
      </van-cell>
    </van-cell-group>
    <van-cell-group title="提醒" class="align-left">
      <van-cell title="设置定时提醒" @click="setScheduledReminders" label=" "></van-cell>
    </van-cell-group>
  </div>
</template>

<script>
export default {
  props: {
    parentMethods: Object
  },
  data() {
    return {
      isAIon: null,
      AISensitivity: null,
      showSlider: false,
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
