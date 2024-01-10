<template>
  <div class="materialTag-container">
    <!-- <div class="materialTag-text">资料管理</div> -->
    <div class="op-btn">
      <el-button
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
          tableMaterialTagData.filter(
            (data) =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        stripe
        style="width: 100%"
      >
        <el-table-column prop="belong" label="所属高校" :width="tableColumnWidth" />
        <el-table-column prop="name" label="标签名" width="300px" />
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
              placeholder="输入注册名搜索"
            />
          </template>
          <!-- 200 020 002 111 -->
          <template slot-scope="scope">
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
          <el-form-item v-if="isAdd" label="所属高校" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.belong" placeholder="请选择">
              <el-option
                v-for="item in tableCollegeData"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="标签名" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.name"
              autocomplete="off"
            />
          </el-form-item>
          <!-- <el-form-item label="标签id" :label-width="formLabelWidth">
            <el-input
              v-model="dialogForm.id"
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
  getMaterialTagList,
  addMaterialTag,
  deleteMaterialTag,
  deleteBatchMaterialTag,
  getMaterialTagOne,
  updateMaterialTag
} from '@/api/materialTags'

import { getCollegeList } from '@/api/college'
import { getToken } from '@/utils/auth'

export default {
  name: 'MaterialTag',
  data() {
    return {
      // 后台数据
      tableMaterialTagData: [], // 标签数据
      tableCollegeData: [], // 高校数据
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        belong: '', // 所属高校
        name: '', // 标签名
        id: '', // 标签id
        createTime: '' // 创建时间
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
    // if (this.tableMaterialTagData) {
    this.getMaterialTagPage(1, 10)
    this.getCollegePageList() // 高校信息
    // }
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getMaterialTagPage(page, limit)
    })
  },
  methods: {
    /* 请求数据 */
    // 管理员 获取高校信息查询
    async getCollegePageList() {
      // this.loading = true
      try {
        const { data } = await getCollegeList()
        /* 加工数组 */
        // this.total = data.total
        // this.resetLoading(300)
        this.tableCollegeData = data
      } catch (error) {
        console.log(error)
      }
    },
    // 管理员 标签分页查询
    async getMaterialTagPage(current, pageSize) {
      this.loading = true
      try {
        const { data } = await getMaterialTagList(current, pageSize)
        this.total = data.total
        this.tableMaterialTagData = data.records
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
      this.$confirm(`此操作将删除该资料标签${row.name}, 是否继续?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          deleteMaterialTag(id)
            .then((response) => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.getMaterialTagPage(this.currentPage, this.pageSize)
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
      this.dialogForm = data
      // console.log(index)
      // console.log(data)
    },
    // dialog对话框相关
    // 提交按钮
    dialogSubmit() {
      this.$refs['queryForm'].validate((valid) => {
        if (valid) {
          if (this.isAdd) {
            // 新增
            const addBody = {
              belong: this.dialogForm.belong,
              name: this.dialogForm.name
            }
            addMaterialTag(addBody).then((res) => {
              this.$message.success(res.message)
              this.getMaterialTagPage(this.currentPage, this.pageSize)
              this.dialogCancel()
            }).catch((err) => {
              this.$message.success(err)
              console.log(err)
              this.dialogCancel()
            })
          } else if (!this.isAdd) {
            // 编辑操作
            const editBody = {
              name: this.dialogForm.name,
              id: this.dialogForm.id
            }

            updateMaterialTag(editBody)
              .then((res) => {
                this.$message.success(res.message)
                this.getMaterialTagPage(this.currentPage, this.pageSize)
                this.dialogCancel()
              })
              .catch((err) => {
                console.log(err)
                this.$message.success(err)
                this.dialogCancel()
                // this.$message.error('审核失败')
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
        id: '',
        content: '',
        result: ''
      }
      this.resetForm('queryForm')
    },
    // 导出excel
    handleExportExcel() {
      // 调用接口
      console.log('导出成功')
    }
  }
}
</script>

<style lang="scss" scoped>
.materialTag {
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
