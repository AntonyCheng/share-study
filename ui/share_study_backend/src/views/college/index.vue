<template>
  <div class="college-container">
    <!-- <div class="college-text">高校</div> -->
    <div class="op-btn">
      <el-button
        class="add-btn"
        type="success"
        size="mini"
        @click="handleAdd()"
      >新增</el-button>
      <el-button
        class="export-btn"
        type="success"
        size="mini"
        @click="handleExportExcel()"
      >导出Excel</el-button>
    </div>
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="
          tableCollegeData.filter(
            (data) =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        stripe
        style="width: 100%"
      >
        <el-table-column
          v-for="item in tableCollegeCol"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label"
          :width="tableColumnWidth"
        />
        <el-table-column align="right">
          <template slot="header" slot-scope="scope">
            <el-input
              v-model="search"
              size="mini"
              placeholder="输入高校名搜索"
            />
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
      <Pagination :total="total" :emit-name="$options.name" />

      <el-dialog
        title="标题"
        :visible.sync="dialogShow"
        :width="dialogWidth"
        :top="dialogTop"
        :before-close="dialogCancel"
      >
        <el-form ref="queryForm" :model="dialogForm">
          <!-- <el-form-item label="用户ID" :label-width="formLabelWidth">
            <el-input v-model="dialogForm.id" autocomplete="off"></el-input>
          </el-form-item> -->
          <el-form-item label="高校" :label-width="formLabelWidth">
            <el-input v-model="dialogForm.name" autocomplete="off" />
          </el-form-item>
          <el-form-item label="院校代码" :label-width="formLabelWidth">
            <el-input v-model="dialogForm.code" autocomplete="off" />
          </el-form-item>
          <!-- <el-form-item label="录入时间" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.createTime"
              autocomplete="off"
              :disabled="true"
            ></el-input>
          </el-form-item> -->
          <!-- <el-form-item label="修改时间" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.update_time"
              autocomplete="off"
            ></el-input>
          </el-form-item> -->
        </el-form>
        <div slot="footer">
          <el-button @click="dialogCancel()">取 消</el-button>
          <el-button type="primary" @click="dialogSubmit()">确 定</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {
  getCollegePageInfo,
  getCollegeName,
  updateCollege,
  addCollegeName
} from '@/api/college'

export default {
  name: 'College',
  data() {
    return {
      tableCollegeCol: [
        // {
        //   prop: "id",
        //   label: "高校ID",
        // },
        {
          prop: 'code',
          label: '院校代码'
        },
        {
          prop: 'name',
          label: '高校'
        },
        {
          prop: 'createTime',
          label: '录入时间'
        }
        // {
        //   prop: "update_time",
        //   label: "修改时间",
        // },
      ],
      /*       tableCollegeData: [
        {
          id: "test",
          name: "test",
          belong_name: "test",
          address: "test",
          code: "test",
          create_time: "test",
          update_time: "test",
        },
        {
          id: "0",
          name: "1",
          belong_name: "2",
          address: "3",
          code: "4",
          create_time: "5",
          update_time: "6",
        },
      ], */
      tableCollegeData: [],
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        id: '',
        name: '',
        code: ''
        // create_time: "",
        // belong_name: "",
        // update_time: "",
      },
      formLabelWidth: '120px', // 输入框宽度
      // 对话框dialog相关
      dialogWidth: '600px', // 对话框宽度
      dialogTop: '30px', // 对话框距离顶部距离
      isAdd: true, // 标识新增操作 | true表示新增 - false表示编辑
      loading: false,
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      total: 10
    }
  },
  mounted() {
    if (this.tableCollegeData) {
      this.getCollegePage(this.currentPage, this.pageSize)
    }
    // console.log(this.$options.name);
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getCollegePage(page, limit)
    })
  },
  methods: {
    /* 请求数据 */

    // 管理员分页查询
    async getCollegePage(current, pageSize) {
      this.loading = true
      try {
        const { data } = await getCollegePageInfo(current, pageSize)
        const { total } = data.records
        console.log(data)
        /* 加工数组 */
        this.total = data.total
        this.resetLoading(300)
        this.tableCollegeData = data.records
      } catch (error) {
        console.log(error)
      }
    },

    /* 表格相关 */
    // 编辑操作
    async handleEdit(index, row) {
      // 标识编辑操作
      this.isAdd = false
      // 展示信息
      // 深拷贝
      // this.dialogForm = JSON.parse(JSON.stringify(row));

      // 向后台请求信息
      const result = await getCollegeName(row.id)
      this.dialogForm = result.data
      this.dialogShow = true
    },
    // 新增操作
    handleAdd() {
      // 标识新增操作
      this.isAdd = true
      // 清除表单数据
      this.reset()
      // 显示对话框
      this.dialogShow = true
      // 获取数据
      // 调用新增接口
    },
    // 删除操作
    handleDelete(index, row) {
      // 获取id
      console.log(index, row)
      // 调用删除接口
    },
    // dialog对话框相关
    // 提交按钮
    dialogSubmit() {
      this.$refs['queryForm'].validate((valid) => {
        if (valid) {
          if (this.dialogForm.id != null) {
            updateCollege(this.dialogForm)
              .then((response) => {
                this.dialogShow = false
                this.getCollegePage(this.currentPage, this.pageSize)
                this.reset()
              })
              .catch((error) => {
                console.log(error)
              })
          } else {
            addCollegeName(this.dialogForm)
              .then((response) => {
                this.dialogShow = false
                this.getCollegePage(this.currentPage, this.pageSize)
                this.reset()
              })
              .catch((error) => {
                console.log(error)
              })
          }
        }
      })
    },
    // 取消按钮
    dialogCancel() {
      this.dialogShow = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.dialogForm = {
        code: undefined,
        // create_time: undefined,
        id: undefined,
        name: undefined
        // update_time: undefined,
      }
      this.resetForm('queryForm')
    },
    // 导出excel
    handleExportExcel() {
      // 调用接口
      this.$axios({
        url: '/api/college/download',
        method: 'get',
        responseType: 'blob'
      }).then(
        (response) => {
          const blob = new Blob([response.data], {
            type: 'application/vnd.ms-excel'
          })
          const link = document.createElement('a')
          link.href = window.URL.createObjectURL(blob)
          // 这里也可以自己从headers中获取文件名.
          link.download = '高校信息.xlsx'
          link.click()
          // document.body.removeChild(link);
          this.$message.success('导出成功')
        },
        (error) => {
          this.$message.error('导出excel出错!')
          console.log('导出excel出错' + error)
        }
      )
    }
  }
}
</script>

<style lang="scss" scoped>
.college {
  &-container {
    margin: 30px;

    .op-btn {
      margin: 10px 0 30px 5px;
    }
  }

  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}
</style>
