<template>
<!--
使用说明：
1）、引入category-cascader.vue
2）、语法：<category-cascader :catelogPath.sync="categoryPath"></category-cascader>
      -->
  <div>
    <el-cascader
      filterable
      clearable
      placeholder="试试搜索：手机"
      v-model="paths"
      :options="categorys"
      :props="setting"
    ></el-cascader>
  </div>
</template>

<script>

import PubSub from "pubsub-js"
export default {
  //import引入的组件需要注入到对象中才能使用
  components: {},
  //接受父组件传来的值
  props: {
    categoryPath: {
      type: Array,
      default(){
        return [];
      }
    }
  },
  data() {
    //这里存放数据
    return {
      setting: {
        value: "id",
        label: "name",
        children: "childrens"
      },
      categorys: [],
      paths: this.categoryPath
    };
  },
  watch:{
    categoryPath(v){
      this.paths = this.categoryPath;
    },
    paths(v){
      this.$emit("update:categoryPath",v);
      //还可以使用pubsub-js进行传值
      // this.PubSub.publish("catPath",v);
        PubSub.publish("categoryPath",v);
    }
  },
  //方法集合
  methods: {
    getCategorys() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(({ data }) => {
        this.categorys = data.data;
      });
    }
  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.getCategorys();
  }
};
</script>
<style scoped>
</style>
