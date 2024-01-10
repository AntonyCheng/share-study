<template>
  <div class="material-container">
    <!-- <div class="material-text">资料管理</div> -->
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
          tableMaterialData.filter(
            (data) =>
              !search || data.name.toLowerCase().includes(search.toLowerCase())
          )
        "
        stripe
        style="width: 100%"
      >
        <!-- <el-table-column
          v-for="item in tableMaterialCol"
          :key="item.prop"
          :prop="item.prop"
          :label="item.label"
          :width="tableColumnWidth"
        /> -->
        <el-table-column
          prop="name"
          label="资料名"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="tags"
          label="标签"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <el-popover trigger="hover" placement="top">
                <div v-if="scope.row.tags.length !== 0" style="textAlign:center">
                  <span v-for="(item,index) in scope.row.tags" :key="item" style="textAlign:center">{{ (index+1) + '：' + item }} <br> </span>
                </div>
                <div v-else style="textAlign:center">无</div>
                <span slot="reference">
                  <el-tag>标签详情</el-tag>
                </span>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="score"
          label="收藏数"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="commentNumber"
          label="评论数"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="belongName"
          label="所属老师"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="status"
          label="资料状态"
          :width="tableColumnWidth"
        />
        <el-table-column
          prop="createTime"
          label="录入时间"
          :width="tableColumnWidth"
        />
        <el-table-column align="right">
          <template slot="header" slot-scope="scope">
            <el-input
              v-model="search"
              size="mini"
              placeholder="输入资料名搜索"
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
          <!-- <el-form-item label="资料ID" :label-width="formLabelWidth">
            <el-input v-model="dialogForm.id" autocomplete="off"></el-input>
          </el-form-item> -->
          <el-form-item label="资料状态" :label-width="formLabelWidth">
            <el-select v-model="dialogForm.status" placeholder="请选择">
              <el-option label="正常" value="正常" />
              <el-option label="禁用" value="禁用" />
            </el-select>
          </el-form-item>
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
  getMaterialPageInfo,
  getMaterial,
  updateMaterial,
  delMaterial,
  getMaterialExcel
} from '@/api/material'

export default {
  name: 'Material',
  data() {
    return {
      // 后台数据
      tableMaterialCol: [
        // {
        //   prop: "id",
        //   label: "资料ID",
        // },
        {
          prop: 'name',
          label: '资料名'
        },
        {
          prop: 'score',
          label: '收藏数'
        },
        {
          prop: 'commentNumber',
          label: '评论数'
        },
        {
          prop: 'belongName',
          label: '所属老师'
        },
        {
          prop: 'status',
          label: '资料状态'
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
      /*       tableMaterialData: [
        {
          id: "test",
          name: "test",
          belong_id: "test",
          status: "test",
          create_time: "test",
          update_time: "test",
        },
        {
          id: "0",
          name: "1",
          belong_id: "2",
          status: "3",
          create_time: "4",
          update_time: "5",
        },
      ], */
      tableMaterialData: [],
      // 表格相关
      search: '',
      dialogShow: false,
      // 表单相关
      dialogForm: {
        id: undefined,
        status: undefined
      },
      dialogFormRules: {
        id: [{ required: true, trigger: 'blur' }],
        status: [{ required: true, trigger: 'blur' }]
      },
      formLabelWidth: '120px',
      // 对话框dialog相关
      dialogWidth: '600px',
      dialogTop: '30px',
      isAdd: true, // 标识新增操作 | true表示新增 - false表示编辑
      loading: true,
      // 分页相关
      currentPage: 1,
      pageSize: 10,
      total: 10
    }
  },
  mounted() {
    // if (this.tableMaterialData) {
    this.getMaterialPage(1, 10)
    // }
    this.$bus.$on(`pagination${this.$options.name}`, ({ page, limit }) => {
      // console.log(page, limit);
      this.currentPage = page
      this.pageSize = limit
      this.getMaterialPage(page, limit)
    })
  },
  methods: {
    /* 请求数据 */
    // 管理员分页查询
    async getMaterialPage(current, pageSize) {
      this.loading = true
      try {
        const { data } = await getMaterialPageInfo(current, pageSize)
        // console.log(data.records);
        data.records.forEach((current) => {
          current.status = current.status === 0 ? '正常' : '禁用'
        })
        this.total = data.total
        this.tableMaterialData = data.records
        this.resetLoading(300)
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
      // console.log(index, row);
      const id = row.id
      const result = await getMaterial(id)
      // console.log(result.data);
      result.data.status = result.data.status === 0 ? '正常' : '禁用'
      this.dialogForm = result.data
      this.dialogShow = true
    },
    // 新增操作
    handleAdd() {
      // 标识新增操作
      this.isAdd = true
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
      this.$confirm('此操作将删除教学资源, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          // 调用删除接口
          delMaterial(id)
            .then((response) => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.getMaterialPage(1, 10)
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
          if (this.dialogForm.id != null) {
            /* 对字符文字转义 */
            // 状态
            this.dialogForm.status = this.dialogForm.status === '正常' ? 0 : 1

            // 编辑操作
            updateMaterial(this.dialogForm).then((response) => {
              this.dialogShow = false
              this.$message({
                type: 'info',
                message: '编辑成功'
              })
              // console.log(response);
              this.getMaterialPage(1, 10)
              this.reset()
            })
          } else {
            // 新增操作
            // addCollegeName(this.dialogForm).then((response) => {
            // });
            this.dialogShow = false
            this.$message({
              type: 'info',
              message: '新增成功'
            })
            this.getMaterialPage(1, 10)
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
        id: undefined,
        status: undefined
        // name: undefined,
        // belong: undefined,
        // score: undefined,
        // role: undefined,
        // create_time: undefined,
        // update_time: undefined,
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
    }
  }
}
</script>

<style lang="scss" scoped>
.material {
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
