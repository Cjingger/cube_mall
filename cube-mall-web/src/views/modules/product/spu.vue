<template>
    <div>
      <el-row>
        <el-col :span="24">
          <el-form :inline="true" :model="dataForm">
            <el-form-item label="分类">
              <category-cascader :categoryPath.sync="categoryPath"></category-cascader>
            </el-form-item>
            <el-form-item label="品牌">
              <brand-select style="width:160px"></brand-select>
            </el-form-item>
            <el-form-item label="状态">
              <el-select style="width:160px" v-model="dataForm.status" clearable>
                <el-option label="新建" :value="0"></el-option>
                <el-option label="上架" :value="1"></el-option>
                <el-option label="下架" :value="2"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="检索">
              <el-input style="width:160px" v-model="dataForm.key" clearable></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="searchSpuInfo">查询</el-button>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="24">
          <div class="mod-config">
            <el-table
              :data="dataList"
              border
              v-loading="dataListLoading"
              @selection-change="selectionChangeHandle"
              style="width: 100%;"
            >
              <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
              <el-table-column prop="id" header-align="center" align="center" label="id"></el-table-column>
              <el-table-column prop="spuName" header-align="center" align="center" label="名称"></el-table-column>
              <el-table-column prop="spuDescription" header-align="center" align="center" label="描述"></el-table-column>
              <el-table-column prop="categoryId" header-align="center" align="center" label="分类"></el-table-column>
              <el-table-column prop="brandId" header-align="center" align="center" label="品牌"></el-table-column>
              <el-table-column prop="weight" header-align="center" align="center" label="重量"></el-table-column>
              <el-table-column prop="publishStatus" header-align="center" align="center" label="上架状态">
                <template slot-scope="scope">
                  <el-tag v-if="scope.row.publishStatus == 0">新建</el-tag>
                  <el-tag v-if="scope.row.publishStatus == 1">已上架</el-tag>
                  <el-tag v-if="scope.row.publishStatus == 2">已下架</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" header-align="center" align="center" label="创建时间"></el-table-column>
              <el-table-column prop="updateTime" header-align="center" align="center" label="修改时间"></el-table-column>
              <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
                <template slot-scope="scope">
                  <el-button
                    v-if="scope.row.publishStatus == 0"
                    type="text"
                    size="small"
                    @click="productUp(scope.row.id)"
                  >上架</el-button>
                  <el-button type="text" size="small" @click="attrUpdateShow(scope.row)">规格</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              @size-change="sizeChangeHandle"
              @current-change="currentChangeHandle"
              :current-page="pageIndex"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pageSize"
              :total="totalPage"
              layout="total, sizes, prev, pager, next, jumper"
            ></el-pagination>
          </div>
        </el-col>
      </el-row>
    </div>
</template>

<script>
//这里可以导入其他文件（比如：组件，工具js，第三方插件js，json文件，图片文件等等）
//例如：import 《组件名称》 from '《组件路径》';
import CategoryCascader from "../common/category-cascader";
import BrandSelect from "../common/brand-select";
export default {
  //import引入的组件需要注入到对象中才能使用
  components: { CategoryCascader, BrandSelect },
  props: {
    // catId: {
    //   type: Number,
    //   default: 0
    // }
  },
  data() {
    //这里存放数据
    return {
      //catId: 0,
      categoryPath: [],
      dataForm: {
        status: "",
        key: "",
        brandId: 0,
        categoryId: 0
      },
      catPathSub: null,
      brandIdSub: null,
      dataSub: null,
      dataForm: {},
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    };
  },
  //计算属性 类似于data概念
  computed: {},
  //监控data中的数据变化
  watch: {},
  //方法集合
  methods: {
    searchSpuInfo() {
      console.log("搜索条件", this.dataForm);
      this.getDataList();
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let param = {
        page: this.pageIndex,
        limit: this.pageSize,
        key: this.dataForm.key,
        categoryId: this.dataForm.categoryId,
        brandId: this.dataForm.brandId
      };
      this.$http({
        url: this.$http.adornUrl("/product/spuinfo/list"),
        method: "get",
        params: this.$http.adornParams(param)
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list;
          this.totalPage = data.page.totalCount;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    productUp(id) {
      this.$http({
        url: this.$http.adornUrl("/product/spuinfo/" + id + "/up"),
        method: "post"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1500,
            onClose: () => {
              this.getDataList();
            }
          });
        } else {
          this.$message.error(data.msg);
        }
      });
    },
    attrUpdateShow(row) {
      console.log(row);
      this.$router.push({
        path: "/product-attrupdate",
        query: { spuId: row.id, categoryId: row.categoryId }
      });
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {}
  },
  //生命周期 - 创建完成（可以访问当前this实例）
  created() {},
  //生命周期 - 挂载完成（可以访问DOM元素）
  mounted() {
    this.dataSub = PubSub.subscribe("dataForm", (msg, val) => {
      console.log("~~~~~", val);
      this.dataForm = val;
      this.getDataList();
    });
    this.catPathSub = PubSub.subscribe("catPath", (msg, val) => {
      this.dataForm.categoryId = val[val.length-1];
    });
    this.brandIdSub = PubSub.subscribe("brandId", (msg, val) => {
      this.dataForm.brandId = val;
    });
  },
  beforeCreate() {}, //生命周期 - 创建之前
  beforeMount() {}, //生命周期 - 挂载之前
  beforeUpdate() {}, //生命周期 - 更新之前
  updated() {}, //生命周期 - 更新之后
  beforeDestroy() {
     PubSub.unsubscribe(this.dataSub);
     PubSub.unsubscribe(this.catPathSub);
     PubSub.unsubscribe(this.brandIdSub);
  }, //生命周期 - 销毁之前
  destroyed() {}, //生命周期 - 销毁完成
  activated() {
      this.getDataList();
  } //如果页面有keep-alive缓存功能，这个函数会触发
};
</script>
<style scoped>
</style>
