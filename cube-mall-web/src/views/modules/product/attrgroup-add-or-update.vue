<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @closed="dialogClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="组名" prop="name">
        <el-input v-model="dataForm.name" placeholder="组名"></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="descript">
        <el-input v-model="dataForm.descript" placeholder="描述"></el-input>
      </el-form-item>
      <el-form-item label="组图标" prop="icon">
        <el-input v-model="dataForm.icon" placeholder="组图标"></el-input>
      </el-form-item>
      <el-form-item label="所属分类" prop="categoryId">
        <!-- <el-input v-model="dataForm.catelogId" placeholder="所属分类id"></el-input> -->
        <category-cascader :categoryPath.sync="categoryPath"></category-cascader>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import CategoryCascader from '../common/category-cascader'
export default {
  data() {
    return {
      props:{
        value:"id",
        label:"name",
        children:"childrens"
      },
      visible: false,
      categorys: [],
      categoryPath: [],
      dataForm: {
        id: 0,
        name: "",
        sort: "",
        descript: "",
        icon: "",
        categoryId: 0
      },
      dataRule: {
        name: [
          { required: true, message: "组名不能为空", trigger: "blur" }
        ],
        sort: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        descript: [
          { required: true, message: "描述不能为空", trigger: "blur" }
        ],
        icon: [{ required: true, message: "组图标不能为空", trigger: "blur" }],
        categoryId: [
          { required: true, message: "所属分类id不能为空", trigger: "blur" }
        ]
      }
    };
  },
  components:{CategoryCascader},

  methods: {
    dialogClose(){
      this.categoryPath = [];
    },
    getCategorys(){
      this.$http({
        url: this.$http.adornUrl("/product/category/list/tree"),
        method: "get"
      }).then(({ data }) => {
        this.categorys = data.data;
      });
    },
    init(id) {
      this.dataForm.id = id || 0;
      this.visible = true;
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        if (this.dataForm.id) {

          this.$http({
            url: this.$http.adornUrl(
              `/product/attrgroup/info/${this.dataForm.id}`
            ),
            method: "get",
            params: this.$http.adornParams()
          }).then(({ data }) => {

            if (data && data.code === 0) {

              this.dataForm.name = data.attrGroup.name;
              this.dataForm.sort = data.attrGroup.sort;
              this.dataForm.descript = data.attrGroup.descript;
              this.dataForm.icon = data.attrGroup.icon;
              this.dataForm.categoryId = data.attrGroup.categoryId;
              this.categoryPath =  data.attrGroup.categoryPath;
            }
          });
        }
      });
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs["dataForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(
              `/product/attrgroup/${
                !this.dataForm.id ? "save" : "update"
              }`
            ),
            method: "post",
            data: this.$http.adornData({
              id: this.dataForm.id || undefined,
              name: this.dataForm.name,
              sort: this.dataForm.sort,
              descript: this.dataForm.descript,
              icon: this.dataForm.icon,
              // 去最后一个categoryId
              categoryId: this.categoryPath[this.categoryPath.length-1]
            })
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    }
  },
  created(){
    this.getCategorys();
  }
};
</script>
