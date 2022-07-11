<!-- 子组件 -->
<template>
<div>
  <el-tree
    ref="menuTree"
    :data="menus"
    :props="defaultProps"
    node-key="id"
    @node-click="nodeclick"

    >
  </el-tree>
</div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具 js，第三方插件 js，json 文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';

export default {
//import 引入的组件需要注入到对象中才能使用
components: {
  
},
props: {},
data() {  
//这里存放数据
    return {
      menus: [],
      defaultProps: {
        label: "name",
        children: "childrens"
      }
    } 
},
//计算属性 类似于 data 概念
computed: {
  
},
//监控 data 中的数据变化
watch: {},
//方法集合
methods: {
  //菜单节点被点击
  nodeclick(data, node, component){
    console.log("子组件category的节点被点击:", data, node, component);
    //向父组件发送事件
    //参数1:事件名, 参数2:可变参数 传入的参数数据
    this.$emit("tree-node-click", data, node, component);
  },
  //获取分类菜单
  getMenus(){
    //发送请求,最终请求的是 http://127.0.0.1:8888/api/product/category/list/tree
    //http://127.0.0.1:8888/api 是 index.js文件中 已经配置好了 baseUrl
    this.$http({
      url: this.$http.adornUrl('/product/category/list/tree'),
      method: 'get'
    }).then(({data}) => {
      console.log("成功获取到了菜单数据...", data);
      this.menus = data.data;
    })
  }
},
//生命周期 - 创建完成（可以访问当前 this 实例）
created() {
  this.getMenus();
},
//生命周期 - 挂载完成（可以访问 DOM 元素）
mounted() {

},
beforeCreate() {}, //生命周期 - 创建之前
beforeMount() {}, //生命周期 - 挂载之前
beforeUpdate() {}, //生命周期 - 更新之前
updated() {}, //生命周期 - 更新之后
beforeDestroy() {}, //生命周期 - 销毁之前
destroyed() {}, //生命周期 - 销毁完成
activated() {}, //如果页面有 keep-alive 缓存功能，这个函数会触发
}
</script>
<style lang='scss' scoped>
//@import url(); 引入公共 css 类

</style>
