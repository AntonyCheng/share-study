<template>
  <div class="table-container">
    <el-table
      :data="
        tableData.filter(
          (data) =>
            !search || data.name.toLowerCase().includes(search.toLowerCase())
        )
      "
      stripe
      style="width: 100%"
    >
      <el-table-column
        v-for="item in tableColumn"
        :key="item.prop"
        :prop="item.prop"
        :label="item.label"
        width="120"
      />
      <el-table-column align="right">
        <template slot="header" slot-scope="scope">
          <el-input v-model="search" size="mini" placeholder="输入关键字搜索" />
        </template>
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      title="标题"
      :visible.sync="dialogShow"
      :width="dialogWidth"
      :top="dialogTop"
    >
      <el-form :model="dialogForm">
        <el-form-item
          v-for="col in tableColumn"
          :label="col.label"
          :label-width="formLabelWidth"
        >
          <el-input
            v-model="col.prop"
            autocomplete="off"
            size="small"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogShow = false">取 消</el-button>
        <el-button type="primary" @click="dialogShow = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
/* 废弃 */
export default {
  name: 'Table',
  props: {
    tableColumn: {
      type: Array,
      default: []
    },
    tableData: {
      type: Array,
      default: []
    }
  },
  data() {
    return {
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {},
      currentFormData: {},
      formLabelWidth: '120px',
      // 对话框dialog相关
      dialogWidth: '600px',
      dialogTop: '30px'
    }
  },
  computed: {},
  methods: {
    // 表格相关
    // 编辑操作
    handleEdit(index, row) {
      // 展示信息
      console.log(index, row)
      this.currentFormData = row
      console.log(this.currentFormData)
      this.dialogShow = true
      // 修改信息

      // 调用接口
    },
    // 删除操作
    handleDelete(index, row) {
      console.log(index, row)
    }
    // dialog对话框相关
  }
}
</script>

<style lang="scss">
</style>
