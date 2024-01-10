<template>
  <div class="comment-container">
    <!-- <div class="comment-text">资料管理</div> -->
    <div class="op-btn">
      <el-button
        v-if="0"
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
          tableCommentData.filter(
            (data) =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        stripe
        style="width: 100%"
      >
        <el-table-column prop="belongName" label="评论教师" :width="tableColumnWidth" />
        <el-table-column prop="sendName" label="接收教师" width="120px" />
        <el-table-column prop="content" label="评论内容" width="120px">
          <template slot-scope="scope">
            <div class="name-wrapper">
              <el-popover width="600" trigger="hover" placement="top">
                <span>{{ scope.row.content }}</span>
                <span slot="reference">
                  <el-tag>评论详情</el-tag>
                </span>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="评论状态" width="120px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0"> 正常 </el-tag>
            <el-tag v-else type="danger"> 禁用 </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="创建时间"
          :width="tableColumnWidth"
        />
        <!-- 操作 -->
        <el-table-column align="right">
          <template slot="header" slot-scope="scope">
            <el-input
              v-model="search"
              size="mini"
              placeholder="输入评论教师搜索"
            />
          </template>
          <!-- 200 020 002 111 -->
          <template slot-scope="scope">
            <div v-if="scope.row.resource !== '0'">
              <el-button
                size="mini"
                @click="handleEdit(scope.$index, scope.row)"
              >编辑</el-button>
              <el-button
                size="mini"
                type="danger"
                style="margin-left: 5px"
                @click="handleDelete(scope.$index, scope.row)"
              >删除</el-button>
            </div>
            <div v-else>
              <el-tag type="warning">系统消息，不可操作</el-tag>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination :total="total" :emit-name="$options.name" />

      <!--  -->
      <el-dialog
        :title="isAdd ? '添加' : '编辑'"
        :visible.sync="dialogShow"
        :width="dialogWidth"
        :top="dialogTop"
        :before-close="dialogCancel"
      >
        <el-form ref="queryForm" :model="dialogForm" :rules="dialogFormRules">
          <el-form-item label="评论状态" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.status" placeholder="请选择">
              <el-option
                label="正常"
                value="0"
              />
              <el-option
                label="禁用"
                value="1"
              />
            </el-select>
          </el-form-item>
          <!-- <el-form-item label="标签名" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.name"
              autocomplete="off"
            />
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
  getCommentList,
  deleteComment,
  updateComment
  // addComment,
  // deleteBatchComment,
  // getCommentOne,
} from '@/api/comment'

import { getToken } from '@/utils/auth'

export default {
  name: 'Comment',
  data() {
    return {
      // 后台数据
      tableCommentData: [], // 标签数据
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        status: 0, // 评论状态``
        id: '' // 评论交流id
      },
      dialogFormRules: {
        belong: [{ required: true, trigger: 'blur' }],
        name: [{ required: true, trigger: 'blur' }],
        id: [{ required: false, trigger: 'blur' }],
        createTime: [{ required: false, trigger: 'blur' }]
      },
      formLabelWidth: '120px',
      // 对话框dialog相关
      dialogWidth: '600px',
      dialogTop: '30px',
      isAdd: true, // 标识新增操作 | true表示新增 - false表示审批
      // isEdit: false,
      loading: true,
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      total: 10
    }
  },
  computed: {
    userInfo: () => {
      const token = getToken()
      const data = JSON.parse(token)
      return data
    }
  },
  mounted() {
    // if (this.tableCommentData) {
    this.getCommentPage(1, 10)
    // }
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getCommentPage(page, limit)
    })
  },
  methods: {
    /* 请求数据 */

    // 管理员 标签分页查询
    async getCommentPage(current, pageSize) {
      this.loading = true
      try {
        const { data } = await getCommentList(current, pageSize)
        this.total = data.total
        this.tableCommentData = data.records
        this.resetLoading(300)
      } catch (error) {
        console.log(error)
      }
    },
    /* 表格相关 */
    // 删除
    handleDelete(index, row) {
      // 获取id
      console.log(index, row)
      const id = row.id
      this.$confirm(`此操作将删除该条评论, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          deleteComment(id)
            .then((response) => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.getCommentPage(this.currentPage, this.pageSize)
            })
            .catch((error) => {
              this.$message({
                type: 'success',
                message: '删除失败!' + error.message
              })
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 添加资料标签
    handleAdd() {
      this.isAdd = true // 标识新增
      this.dialogShow = true
    },
    // 编辑资料
    handleEdit(index, row) {
      this.isAdd = false // 标识编辑
      this.dialogShow = true
      // 渲染初始值
      const data = JSON.parse(JSON.stringify(row)) // 深拷贝 防止篡改原数据

      this.dialogForm.status = data.status === 0 ? '正常' : '禁用'
      this.dialogForm.id = data.id
      // console.log(index)
      // console.log(data)
    },
    // dialog对话框相关
    // 提交按钮
    dialogSubmit() {
      this.$refs['queryForm'].validate((valid) => {
        if (valid) {
          // 编辑操作
          const editBody = {
            // status: this.dialogForm.status === '正常' ? 0 : 1,
            status: this.dialogForm.status,
            id: this.dialogForm.id
          }
          // console.log(typeof editBody.status === 'string')

          // if (typeof editBody.status === 'string') {
          //   editBody.status = this.dialogForm.status === '正常' ? 0 : 1
          // }

          updateComment(editBody)
            .then((res) => {
              this.$message.success(res.message)
              this.getCommentPage(this.currentPage, this.pageSize)
              this.dialogCancel()
            })
            .catch((err) => {
              console.log(err)
              this.$message.success(err)
              this.dialogCancel()
              // this.$message.error('审核失败')
            })
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
        id: '',
        content: '',
        result: ''
      }
      this.resetForm('queryForm')
    },
    // 导出excel
    handleExportExcel() {
      // 调用接口
      this.$axios({
        url: '/api/comment/download',
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
          link.download = '交流评论.xlsx'
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
.comment {
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
