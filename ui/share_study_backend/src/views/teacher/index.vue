<template>
  <div class="teacher-container">
    <!-- <div class="teacher-text">教师管理</div> -->
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
        :data="tableTeacherData.filter(
          (data) => {
            return !search || data.name.toLowerCase().includes(search.toLowerCase())
          }
        )"
        stripe
        style="width: 100%"
      >

        <el-table-column
          v-for="item in tableTeacherCol"
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
              placeholder="输入教师名，按Enter搜索"
            >
              <!-- @keyup.enter.native="getTeacherPage(1,1000,search)" -->
              <i slot="suffix" class="el-icon-search" style="top:5px" />
            </el-input>
          </template>
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.role === '普通用户'"
              size="mini"
              @click="handleEdit(scope.$index, scope.row)"
            >编辑</el-button>
            <el-button
              v-if="scope.row.role === '普通用户'"
              size="mini"
              type="danger"
              @click="handleDelete(scope.$index, scope.row)"
            >删除</el-button>
            <el-button
              v-if="userInfo.role === 2"
              size="mini"
              type="danger"
              @click="handleResetPwd(scope.$index, scope.row)"
            >重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination :total="total" :emit-name="$options.name" />

      <el-dialog
        title="标题"
        :visible.sync="dialogShow"
        :width="dialogWidth"
        :top="dialogTop"
        :before-close="dialogCancel"
      >
        <el-form ref="queryForm" :model="dialogForm" :rules="dialogFormRules">
          <el-form-item label="角色状态" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.role" placeholder="请选择">
              <el-option label="普通用户" value="0" />
              <el-option label="管理员" value="1" />
              <!-- <el-option label="超级管理员" value="2"></el-option> -->
            </el-select>
          </el-form-item>
          <el-form-item label="状态" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.status" placeholder="请选择">
              <el-option label="正常" value="正常" />
              <el-option label="禁用" value="禁用" />
            </el-select>
          </el-form-item>
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
  getTeacherPageInfo,
  getTeacherInfo,
  updateTeacherInfo,
  deleteOneTeacher,
  teacheResetPwd
} from '@/api/user'
import { getCollegeName } from '@/api/college'
import { getToken } from '@/utils/auth'

export default {
  name: 'Teacher',
  data() {
    return {
      tableTeacherCol: [
        // {
        //   prop: "id",
        //   label: "用户ID",
        // },
        {
          prop: 'name',
          label: '老师姓名'
        },
        // {
        //   prop: "belong",
        //   label: "所属高校",
        // },
        {
          prop: 'belongName',
          label: '所属高校'
        },
        {
          prop: 'score',
          label: '贡献度'
        },
        {
          prop: 'role',
          label: '角色状态'
        },
        {
          prop: 'status',
          label: '状态'
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
      tableTeacherData: [], // 分页数据
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        id: undefined,
        avater: undefined,
        email: undefined,
        gender: undefined,
        belong: undefined,
        role: undefined,
        status: undefined
      },
      dialogFormRules: {
        id: [{ required: true, trigger: 'blur' }],
        avater: [{ required: true, trigger: 'blur' }],
        email: [{ required: true, trigger: 'blur' }],
        gender: [{ required: true, trigger: 'blur' }],
        belong: [{ required: true, trigger: 'blur' }],
        role: [{ required: true, trigger: 'blur' }],
        status: [{ required: true, trigger: 'blur' }]
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
  created() {
    if (this.tableTeacherData) {
      this.getTeacherPage(this.currentPage, this.pageSize)
    }
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getTeacherPage(page, limit)
    })
  },
  mounted() { },
  computed: {
    userInfo: () => {
      const token = getToken()
      const data = JSON.parse(token)
      return data
    }
  },
  methods: {
    /* 请求数据 */
    // 管理员分页查询
    async getTeacherPage(current, pageSize, name = '') {
      this.loading = true
      // const body = {
      //   name: name
      // }
      try {
        const { data } = await getTeacherPageInfo(current, pageSize)
        this.total = data.total
        // console.log(data);
        /* 加工数组
          所属高校 => 字符串
          角色状态 0-普通用户 1-管理员 2-超级管理员
          状态 0-未禁用 1-禁用
          时间 数组-字符串
        */
        // 过滤数组
        // console.log(data);
        // let filterArr = data.records.filter((c) => c.role != 2);
        // console.log(filterArr);

        // const newArr = await Promise.all(
        const newArr = data.records.map((current) => {
          // 所属高校 (会有延迟问题)待定
          // let belong = await getCollegeName(current.belong);
          // current.belong = belong.data.name;
          // current.belong = await this.getCollege(current.belong);

          if (current.role === 2) {
            current.role = '超级管理员'
          } else {
            current.role = current.role === 0 ? '普通用户' : '管理员'
          }
          // 状态
          current.status = current.status === 0 ? '正常' : '禁用'
          // 时间格式化
          /* current.createTime =
            current.createTime.slice(0, 3).toString().replace(/,/g, "-") +
            " " +
            current.createTime.slice(3, 6).toString().replace(/,/g, ":"); */
          return current
        })
        // );
        this.tableTeacherData = newArr
        this.resetLoading(300)
      } catch (error) {
        console.log(error)
      }
    },
    // 获取高校消息
    async getCollege(id) {
      const collegeName = await getCollegeName(id)
      // console.log(collegeName.data.name);
      return collegeName.data.name
    },

    /* 表格相关 */
    // 编辑操作
    async handleEdit(index, row) {
      // 标识编辑操作
      // console.log(row);
      this.isAdd = false
      const id = row.id
      const result = await getTeacherInfo(id)
      // 角色
      // result.data.role = "普通用户";
      if (result.data.role === 2) {
        result.data.role = '超级管理员'
      } else {
        result.data.role = result.data.role === 0 ? '普通用户' : '管理员'
      }
      // 状态
      result.data.status = result.data.status === 0 ? '正常' : '禁用'
      this.dialogForm = result.data
      this.dialogShow = true

      // 展示信息
      // 深拷贝
      // this.dialogForm = JSON.parse(JSON.stringify(row));
      // this.dialogShow = true;
      // 修改信息
      // 调用编辑接口
    },
    // 新增操作
    handleAdd() {
      // 标识新增操作
      this.isAdd = true
      // 重置表单
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
      const id = row.id
      this.$confirm('此操作将删除该用户或管理员, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          deleteOneTeacher(id)
            .then((response) => {
              this.getTeacherPage(this.currentPage, this.pageSize)
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
            })
            .catch((res) => {
              this.$message({
                type: 'info',
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
    // 重置密码操作
    handleResetPwd(index, row) {
      // 获取id
      console.log(index, row)
      const id = row.id
      this.$confirm('此操作将重置改用户密码, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          teacheResetPwd(id)
            .then((response) => {
              this.getTeacherPage(this.currentPage, this.pageSize)
              this.$message({
                type: 'success',
                message: '重置密码成功!'
              })
            })
            .catch((res) => {
              this.$message({
                type: 'info',
                message: '重置密码失败!' + error.message
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
          if (this.dialogForm.id != null) {
            /* 对字符文字转义 */
            // 角色
            if (this.dialogForm.role === '超级管理员') {
              this.dialogForm.role === 2
            } else {
              this.dialogForm.role =
                this.dialogForm.role === '普通用户' ? 0 : 1
            }

            // this.dialogForm.role = this.dialogForm.role === "管理员" ? 1 : 0;
            // 状态
            this.dialogForm.status = this.dialogForm.status === '正常' ? 0 : 1

            // 编辑操作
            updateTeacherInfo(this.dialogForm).then((response) => {
              this.$message({
                type: 'info',
                message: '编辑成功'
              })
              // console.log(response);
              this.dialogShow = false
              this.getTeacherPage(1, 10)
              this.reset()
            })
          } else {
            // 新增操作
            // addCollegeName(this.dialogForm).then((response) => {
            // });
            this.$message({
              type: 'info',
              message: '新增成功'
            })
            this.dialogShow = false
            this.getTeacherPage(1, 10)
            this.reset()
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
        // id: undefined,
        name: undefined,
        belong: undefined,
        score: undefined,
        role: undefined,
        status: undefined,
        create_time: undefined
        // update_time: undefined,
      }
      this.resetForm('queryForm')
    },

    // 导出excel
    handleExportExcel() {
      // 调用接口
      this.$axios({
        url: '/api/teacher/download',
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
          link.download = '教师信息.xlsx'
          link.click()
          // console.log(link);
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
.teacher {
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
