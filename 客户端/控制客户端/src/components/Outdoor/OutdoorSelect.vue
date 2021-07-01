<template>
  <div ref="indoor-select-div">
<van-picker
  title="被监护人名单"
  show-toolbar
  :columns="wardIdList"
  @confirm="onConfirm"
  @change="onChange"
/>
  </div>
</template>

<script>
import defVal from "../../assets/config/default-value.js";
export default {
  props: {
    parentMethods: Object,
  },
  mounted() {
    this.loadWardList();
  },
  data() {
    return {
      accountIndex: 0,
      wardList: defVal.wardList,
      wardIdList:[],
    };
  },
  methods: {
    loadWardList() {
      // this.$axios.request({
      //   url: this.$api.getWardList,
      //   method: "get",
      // }).then((response) => {
      //  alert(response);
      // })
        this.$axios
        .get(
         this.$api.getWardList
        )
        .then(res => {
      
         this.wardList=new Array();
         for(var i=0;i<res.data.length;i++)
         {
         this.wardList.push(res.data[i]["ward"]);
         this.wardIdList.push(res.data[i]["wardName"])
          console.log(res.data[i]["wardName"]);
         }
        })
        .catch(err => {
          console.log(err);
        });
     
    },
    onConfirm(value, index)
    {
      sessionStorage.setItem('accountId',this.wardList[index]);
      this.parentMethods.onSelected(this.wardList[index]);
    }
  },
};
</script>

<style scoped>
</style>
