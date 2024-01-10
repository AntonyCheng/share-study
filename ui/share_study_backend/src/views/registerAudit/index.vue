<template>
  <div class="audit-container">
    <!-- <div class="audit-text">资料管理</div> -->
    <div class="op-btn">
      <el-button
        v-if="0"
        class="add-btn"
        type="success"
        size="mini"
        @click="handleAdd()"
      >新增</el-button>
      <el-button
        v-if="0"
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
          tableAuditData.filter(
            (data) =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        stripe
        style="width: 100%"
      >
        <!-- <el-table-column
          v-for="item in tableAuditCol"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label"
          :width="tableColumnWidth"
        >
        </el-table-column> -->
        <!-- <el-table-column prop="id" label="资料ID" :width="tableColumnWidth">
        </el-table-column> -->
        <el-table-column prop="name" label="注册名" :width="tableColumnWidth" />
        <el-table-column prop="email" label="邮箱" width="180px" />
        <el-table-column
          prop="account"
          label="注册账号"
          :width="tableColumnWidth"
        />
        <el-table-column prop="gender" label="性别" :width="tableColumnWidth">
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span slot="reference">
                <!-- 审查状态（0是男，1是女） -->
                <span v-if="scope.row.gender === 0">男</span>
                <span v-if="scope.row.gender === 1">女</span>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="belongName"
          label="所属高校"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="status"
          label="审核状态"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span slot="reference">
                <!-- 审查状态（0是未审查，1是通过，2是不通过，3是已发布） -->
                <el-tag
                  v-if="scope.row.status === 0"
                  size="medium"
                  type="info"
                >未审查</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 1"
                  size="medium"
                  type="success"
                >已通过</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 2"
                  size="medium"
                  type="danger"
                >不通过</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 3"
                  size="medium"
                  type="success"
                >已发布</el-tag>
                <!-- <el-tag size="medium" type="success" v-else>已发布</el-tag> -->
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="censorAdminName"
          label="审核人"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span v-if="scope.row.status === 0">
                <el-tag size="medium" type="info">无</el-tag>
              </span>
              <el-tag v-else>审核人：{{ scope.row.censorAdminName }}</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column
          prop="createTime"
          label="录入时间"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="updateTime"
          label="更新时间"
          :width="tableColumnWidth"
        />
        <!-- 操作 -->
        <el-table-column fixed="right" align="right" width="180px">
          <template slot="header" slot-scope="scope">
            <el-input
              v-model="search"
              size="mini"
              placeholder="输入注册名搜索"
            />
          </template>
          <!-- 200 020 002 111 -->
          <template slot-scope="scope">
            <!-- !(
                  scope.row.censorAdmin1Result === 2 ||
                  scope.row.censorAdmin2Result === 2 ||
                  scope.row.censorAdmin3Result === 2
                ) &&
                !(
                  scope.row.censorAdmin1Result === 1 &&
                  scope.row.censorAdmin2Result === 1 &&
                  scope.row.censorAdmin3Result === 1
                ) -->
            <span v-if="scope.row.status === 0">
              <el-button
                size="mini"
                @click="handleAuditPass(scope.$index, scope.row)"
              >通过</el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleAuditNoPass(scope.$index, scope.row)"
              >不通过</el-button>
            </span>

            <!--
                scope.row.censorAdmin1Result === 1 &&
                scope.row.censorAdmin2Result === 1 &&
                scope.row.censorAdmin3Result === 1 -->
            <span v-else-if="scope.row.status === 1">
              <el-button
                size="mini"
                :disabled="!(userInfo.role === 2)"
                @click="publishAudit(scope.$index, scope.row)"
              >发布</el-button>
            </span>
            <span v-else-if="scope.row.status === 2">
              <el-tag type="danger">审核不通过</el-tag>
            </span>
            <span v-else-if="scope.row.status === 3">
              <el-tag type="success">已发布</el-tag>
            </span>
            <el-button
              v-if="
                scope.row.status === 1 ||
                  scope.row.status === 3 ||
                  scope.row.status === 2
              "
              size="mini"
              type="danger"
              :disabled="!(userInfo.role === 2)"
              style="margin-left: 5px"
              @click="handleDelete(scope.$index, scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination :total="total" :emit-name="$options.name" />

      <el-dialog
        title="请输入理由"
        :visible.sync="dialogShow"
        :width="dialogWidth"
        :top="dialogTop"
        :before-close="dialogCancel"
      >
        <el-form ref="queryForm" :model="dialogForm" :rules="dialogFormRules">
          <el-form-item label="原因" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.content"
              autocomplete="off"
            />
          </el-form-item>
          <!-- <el-form-item label="资料状态" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.status" placeholder="请选择">
              <el-option label="正常" value="正常"></el-option>
              <el-option label="禁用" value="禁用"></el-option>
            </el-select>
          </el-form-item> -->
          <!-- <el-form-item label="所属老师id" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.belong_id"
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
  // getAuditPageInfo,
  // updateAuditResource,
  // publishAuditResource,
  // delAudit,
  getRegisterAuditPageInfo,
  updateRegisterAudit,
  publishRegisterAudit,
  delRegisterAudit
} from '@/api/audit'
import { getToken } from '@/utils/auth'

export default {
  name: 'RegisterAudit',
  data() {
    return {
      // 后台数据
      tableAuditData: [],
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        id: '',
        content: '',
        result: ''
      },
      dialogFormRules: {
        id: [{ required: true, trigger: 'blur' }],
        content: [{ required: true, trigger: 'blur' }],
        result: [{ required: true, trigger: 'blur' }]
      },
      formLabelWidth: '120px',
      // 对话框dialog相关
      dialogWidth: '600px',
      dialogTop: '30px',
      isAdd: true, // 标识新增操作 | true表示新增 - false表示审批
      loading: true,
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      total: 10,
      // 文件预览
      imgType: 'png jpg jpeg' // 图片类型
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
    // if (this.tableAuditData) {
    this.getAuditPage(1, 10)
    // }
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getAuditPage(page, limit)
    })
  },
  methods: {
    /* 请求数据 */
    // 管理员分页查询
    async getAuditPage(current, pageSize) {
      this.loading = true
      try {
        const { data } = await getRegisterAuditPageInfo(current, pageSize)
        // console.log(data.records);
        // data.records.forEach((current) => {
        //   // 资料状态
        //   current.status = current.status === 0 ? "正常" : "禁用";
        //   //
        // });
        this.total = data.total
        this.tableAuditData = data.records
        this.resetLoading(300)
      } catch (error) {
        console.log(error)
      }
    },
    /* 表格相关 */
    // 审批通过操作
    handleAuditPass(index, row) {
      const passObj = {
        content: '',
        id: '',
        result: 1
      }
      // 标识审批操作
      this.isAdd = false
      // 展示信息
      // console.log(index, row);
      passObj.id = row.id

      updateRegisterAudit(passObj)
        .then((res) => {
          this.$message.success(res.message)
          this.getAuditPage(this.currentPage, this.pageSize)
        })
        .catch((err) => {
          console.log(err)
          // this.$message.error('审核失败')
        })
      // console.log(result.data);
      // result.data.status = result.data.status === 0 ? "正常" : "禁用";
      // this.dialogForm = result.data;
      // this.dialogShow = true;
    },
    // 审批不通过操作
    handleAuditNoPass(index, row) {
      // let passObj = {
      //   content: "",
      //   id: "",
      //   result: 2,
      // };
      // 展示信息
      // console.log(index, row);
      // passObj.id = row.id;

      this.dialogForm.id = row.id
      this.dialogForm.result = 2

      this.dialogShow = true
      // console.log(result.data);
      // result.data.status = result.data.status === 0 ? "正常" : "禁用";
      // this.dialogForm = result.data;
      // this.dialogShow = true;
    },
    publishAudit(index, row) {
      const id = row.id
      publishRegisterAudit(id)
        .then((res) => {
          this.$message.success(res.message)
          this.getAuditPage(this.currentPage, this.pageSize)
        })
        .catch((err) => {
          console.log(err)
        })
    },
    // 删除
    handleDelete(index, row) {
      // 获取id
      console.log(index, row)
      const id = row.id
      this.$confirm('此操作将删除该审核资料, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          delRegisterAudit(id)
            .then((response) => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.getAuditPage(this.currentPage, this.pageSize)
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
    // dialog对话框相关
    // 提交按钮
    dialogSubmit() {
      this.$refs['queryForm'].validate((valid) => {
        if (valid) {
          // 编辑操作
          updateRegisterAudit(this.dialogForm)
            .then((res) => {
              this.$message.success(res.message)
              this.getAuditPage(this.currentPage, this.pageSize)
              this.dialogCancel()
            })
            .catch((err) => {
              console.log(err)
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
        url: '/api/resource/download',
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
          link.download = '教学资料.xlsx'
          link.click()
          // document.body.removeChild(link);
          this.$message.success('导出成功')
        },
        (error) => {
          this.$message.error('导出excel出错!')
          console.log('导出excel出错' + error)
        }
      )
    },
    // 文件预览相关
    // 获取文件后缀
    getFileSuffix(fileUrl) {
      return fileUrl.split('.').reverse()[0].toLowerCase()
    }
  }
}
</script>

<style lang="scss" scoped>
.audit {
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
