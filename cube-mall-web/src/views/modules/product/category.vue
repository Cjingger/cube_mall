<template>
  <div>
    <el-button type="danger" @click="batchDelete">批量删除</el-button>
    <el-tree
      :data="menus"
      :props="defaultProps"
      :expand-on-click-node="false"
      show-checkbox
      node-key="id"
      :default-expanded-keys="expandedKey"
      ref="menuTree"
    >
       <span class="custom-tree-node" slot-scope="{ node, data }">
        <span>{{ node.label }}</span>
        <span>
          <el-button
            v-if="node.level <=2"
            type="text"
            size="mini"
            @click="() => append(data)"
          >Append</el-button>
          <el-button type="text" size="mini" @click="edit(data)">edit</el-button>
          <el-button
            v-if="node.childNodes.length==0"
            type="text"
            size="mini"
            @click="() => remove(node, data)"
          >Delete</el-button>
        </span>
      </span>
    </el-tree>

    <el-dialog
      :title="title"
      :visible.sync="dialogVisible"
      width="30%"
      :close-on-click-modal="false"
    >
      <el-form :model="category">
        <el-form-item label="分类名称">
          <el-input v-model="category.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitData">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>


export default {
  //import引入的组件需要注入到对象中才能使用
  components: {},
  props: {},
  data() {
    return {
      pCid: [],
      draggable: false,
      updateNodes: [],
      maxLevel: 0,
      title: "",
      dialogType: "", //edit,add
      category: {
        name: "",
        parentId: 0,
        isShow: 1,
        isMenu: 0,
        seq: 0,
        templateId: 0,
        id: null
      },
      dialogVisible: false,
      menus: [],
      expandedKey: [],
      defaultProps: {
        children: "childrens",
        label: "name"
      }
    };
  },

  //计算属性 类似于data概念
  computed: {},
  //监控data中的数据变化
  watch: {},
  //方法集合
  methods: {
    getMenus() {
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(({ data }) => {
        console.log("成功获取到菜单数据...", data.data);
        this.menus = data.data;
      });
    },

    edit(data) {
      console.log("要修改的数据", data);
      //设置对话框类型是编辑
      this.dialogType = "edit";
      //设置title名称
      this.title = "修改分类";
      //显示对话框
      this.dialogVisible = true;

      //根据当前id获取分类详细信息
      this.$http({
        url: this.$http.adornUrl(`/product/category/info/${data.id}`),
        method: "get"
      }).then(({ data }) => {
        //请求成功
        //回填分类信息，与添加逻辑不通点
        this.category.name = data.data.name;
        this.category.id = data.data.id;
        this.category.goodsNum = data.data.goodsNum;
        this.category.isMenu = data.data.isMenu;
        this.category.parentId = data.data.parentId;
        this.category.seq = data.data.seq;
        this.category.isShow = data.data.isShow;

      });
    },


    append(data) {
      console.log("append", data);

      //为了区分是添加还是修改
      this.dialogType = "add";
      //设置名称
      this.title = "添加分类";
      //显示添加对话框
      this.dialogVisible = true;
         //以下封装catgroy对象值
      this.category.parentId = data.id; //当前节点父id
      this.category.id = null;
      this.category.name = "";
      this.category.goodsNum = 0;
      this.category.isMenu = "";
      this.category.isShow = 1;
      this.category.templateId = 0;
      this.category.seq = 0;
    },

    submitData() {
      //添加分类
      if (this.dialogType == "add") {
        this.addCategory();
      }
      //编辑分类
      if (this.dialogType == "edit") {
        this.editCategory();
      }
    },
 //修改三级分类数据
    editCategory() {
      var { id, name, goodsNum, isMenu } = this.category; //设置分类名称
      this.$http({
        url: this.$http.adornUrl("/product/category/update"),
        method: "post",
        data: this.$http.adornData({ id, name, goodsNum, isMenu}, false) //添加修改参数
      }).then(({ data }) => {
        this.$message({
          message: "菜单修改成功",
          type: "success"
        });
        //关闭对话框
        this.dialogVisible = false;
        //更新最新的菜单列表
        this.getMenus();
        //设置默认打开行
        this.expandedKey = [this.category.parentId];
      });
    },

    addCategory() {
      console.log("提交的三级分类数据", this.category);
      this.$http({
        url: this.$http.adornUrl("/product/category/save"), //请求的url
        method: "post", //请求的方式
        data: this.$http.adornData(this.category, false) //请求封装的参数
      }).then(({ data }) => {
        this.$message({
          message: "菜单保存成功",
          type: "success"
        });
        //对话框设置为隐藏
        this.dialogVisible = false;
        //重新加载菜单列表
        this.getMenus();
        //设置默认展开
        this.expandedKey = [this.category.parentId];
      });
    },

    batchDelete() { //批量删除
      let ids = []; //封装参数ID集合
      let checkedNodes = this.$refs.menuTree.getCheckedNodes(); //通过判断checkbox是否被选中
      console.log("被选中的元素", checkedNodes);
      for (let i = 0; i < checkedNodes.length; i++) {
        ids.push(checkedNodes[i].id); //把选中checkbox id值添加的ids集合中
      }
      this.$confirm(`是否批量删除【${ids}】菜单?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/product/category/delete"),
            method: "post",
            data: this.$http.adornData(ids, false)
          }).then(({ data }) => {
            this.$message({
              message: "菜单批量删除成功",
              type: "success"
            });
            //刷新菜单列表
            this.getMenus();
          });
        })
        .catch(() => {});
    },

    remove(node, data) { //单条删除
      var ids = [data.id]; //删除逻辑根据ID ，并且封装成数组
      this.$confirm(`是否删除【${data.name}】菜单?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/product/category/delete"), //删除API url
            method: "post", //删除api ，请求方式
            data: this.$http.adornData(ids, false) //传入参数 ，id集合
          }).then(({ data }) => {
            this.$message({
              message: "菜单删除成功",
              type: "success"
            });
            //重新刷新当前的菜单
            this.getMenus();
            //设置需要默认展开的菜单，删除节点父节点
            this.expandedKey = [node.parent.data.id];
          });
        })
        .catch(() => {});

      console.log("remove", node, data);
    }


  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() {
    this.getMenus();
  },
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {},
  beforeCreate() {}, //生命周期 - 创建之前
  beforeMount() {}, //生命周期 - 挂载之前
  beforeUpdate() {}, //生命周期 - 更新之前
  updated() {}, //生命周期 - 更新之后
  beforeDestroy() {}, //生命周期 - 销毁之前
  destroyed() {}, //生命周期 - 销毁完成
  activated() {} //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style scoped>
</style>
