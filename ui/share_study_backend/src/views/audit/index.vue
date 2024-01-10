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
        <el-table-column prop="name" label="资料名" :width="tableColumnWidth" />
        <el-table-column prop="info" label="整体预览" :width="tableColumnWidth">
          <template slot-scope="scope">
            <!-- 其他文件下载 -->
            <el-link
              @click="handlePreview(scope.$index, scope.row)"
            >预览<i class="el-icon-view el-icon--right" />
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="文件预览" :width="tableColumnWidth">
          <template slot-scope="scope">
            <span v-if="scope.row.url === ''">无</span>
            <!-- 图片预览 -->
            <img
              v-else-if="imgType.includes(getFileSuffix(scope.row.url))"
              :src="scope.row.url"
              alt=""
              style="width: 150px"
            >
            <!-- 其他文件下载 -->
            <el-link
              v-else
              :href="scope.row.url"
              target="blank"
            >下载查看<i class="el-icon-view el-icon--right" />
            </el-link>
          </template>
        </el-table-column>
        <el-table-column
          prop="censorAdmin1Result"
          label="第一次审核"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span v-if="scope.row.censorAdmin1Result === 0">
                <el-tag size="medium" type="info">未审核</el-tag>
              </span>
              <el-popover v-else trigger="hover" placement="top">
                <el-tag>审核人：{{ scope.row.censorAdmin1Name }}</el-tag>
                <span slot="reference">
                  <el-tag
                    size="medium"
                    :type="
                      scope.row.censorAdmin1Result === 1 ? 'success' : 'danger'
                    "
                  >{{
                    scope.row.censorAdmin1Result === 1 ? "通过" : "不通过"
                  }}</el-tag>
                </span>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="censorAdmin2Result"
          label="第二次审核"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span v-if="scope.row.censorAdmin2Result === 0">
                <el-tag size="medium" type="info">未审核</el-tag>
              </span>
              <el-popover v-else trigger="hover" placement="top">
                <el-tag>审核人：{{ scope.row.censorAdmin2Name }}</el-tag>
                <span slot="reference">
                  <el-tag
                    size="medium"
                    :type="
                      scope.row.censorAdmin2Result === 1 ? 'success' : 'danger'
                    "
                  >{{
                    scope.row.censorAdmin2Result === 1 ? "通过" : "不通过"
                  }}</el-tag>
                </span>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="censorAdmin3Result"
          label="第三次审核"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span v-if="scope.row.censorAdmin3Result === 0">
                <el-tag size="medium" type="info">未审核</el-tag>
              </span>
              <el-popover v-else trigger="hover" placement="top">
                <el-tag>审核人：{{ scope.row.censorAdmin3Name }}</el-tag>
                <span slot="reference">
                  <el-tag
                    size="medium"
                    :type="
                      scope.row.censorAdmin3Result === 1 ? 'success' : 'danger'
                    "
                  >{{
                    scope.row.censorAdmin3Result === 1 ? "通过" : "不通过"
                  }}</el-tag>
                </span>
              </el-popover>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="审核状态"
          :width="tableColumnWidth"
        >
          <template slot-scope="scope">
            <div class="name-wrapper">
              <span slot="reference">
                <!-- 审查状态（0是未审查，1是正在审查，2是审查通过，3是审查未通过） -->
                <el-tag
                  v-if="scope.row.status === 0"
                  size="medium"
                  type="info"
                >未审查</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 1"
                  size="medium"
                  type="success"
                >正在审查</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 2"
                  size="medium"
                  type="success"
                >通过</el-tag>
                <el-tag
                  v-else-if="scope.row.status === 3"
                  size="medium"
                  type="danger"
                >不通过</el-tag>
                <el-tag v-else size="medium" type="success">已发布</el-tag>
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="belongName"
          label="所属老师"
          :width="tableColumnWidth"
        />

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
              placeholder="输入资料名搜索"
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
            <span v-if="scope.row.status === 0 || scope.row.status === 1">
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
            <span v-else-if="scope.row.status === 2">
              <el-button
                size="mini"
                :disabled="!(userInfo.role === 2)"
                @click="publishAudit(scope.$index, scope.row)"
              >发布</el-button>
            </span>
            <span v-else-if="scope.row.status === 3">
              <el-tag type="danger">审核不通过</el-tag>
            </span>
            <span v-else-if="scope.row.status === 4">
              <el-tag type="success">已发布</el-tag>
            </span>
            <el-button
              v-if="
                scope.row.status === 3 ||
                  scope.row.status === 2 ||
                  scope.row.status === 4
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

      <!-- 不同意理由 -->
      <el-dialog
        title="请输入理由"
        :visible.sync="dialogShow"
        :width="dialogWidth"
        :top="dialogTop"
        :before-close="dialogCancel"
      >
        <el-form ref="queryForm" :model="dialogForm" :rules="dialogFormRules">
          <el-form-item label="原因" :label-width="formLabelWidth">
            <el-input v-model="dialogForm.reason" autocomplete="off" />
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

      <!-- 整体预览 -->
      <el-dialog
        class="preview-dialog-container"
        title="预览"
        :visible.sync="previewDialogShow"
        :top="dialogTop"
        :before-close="previewDialogCancel"
      >
        <!-- 帖子标题 -->
        <div class="preview-dialog-title">
          <h2>{{ previewDialogObj.title }}</h2>
        </div>
        <!-- 帖子信息 -->
        <div class="preview-dialog-info">
          <div ref="inserthtml" class="insert-html">
            <!-- {{ previewDialogObj.resourceInfo }} -->
          </div>

          <!-- 如果附件是以 图片 / 视频的形式 则显示在帖子中 -->
          <div v-if="fileIsImg" class="img-container">
            <img :src="previewDialogObj.url" alt="图片" style="width: 600px">
          </div>

          <div v-else-if="fileIsVideo" class="movies-container">
            <video-player
              style="width:600px;height:400px;text-align: center;"
              :src="previewDialogObj.url"
              :volume="volume"
            />
            <!-- <audio :src="" alt="视频"></audio> -->
          </div>

          <div
            v-else-if="fileIsDoc"
            class="doc-container"
            style="width: 100%; margin-top: 30px"
          >
            <iframe
              :src="docPreviewUrl"
              frameborder="0"
              width="100%"
              height="600"
            />
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import {
  getAuditPageInfo,
  updateAuditResource,
  publishAuditResource,
  delAudit
} from '@/api/audit'
import { getToken } from '@/utils/auth'

import VideoPlayer from '@/components/VideoPlayer'
import { nextTick } from 'process'

export default {
  name: 'Audit',
  components: {
    VideoPlayer
  },
  data() {
    return {
      // 后台数据
      tableAuditData: [],
      // 表格相关
      search: '',
      // 预览表单
      previewDialogObj: {
        title: '',
        info: '',
        url: ''
      },
      previewDialogShow: false,
      // 帖子 显示图片/视频 文档
      fileSuffix: undefined,
      imgType: 'png jpg jpeg', // 图片类型
      vidioType: 'mp4 mpeg', // 视频类型
      docType: 'pdf xlsx xls doc docx ppt pptx', // 文档类型
      volume: 0.5, // 视频声音
      fileIsImg: undefined, // 是否 图片
      fileIsVideo: undefined, // 是否是视频
      fileIsDoc: undefined, // 是否是文档
      docPreviewUrl: undefined, // doc预览地址
      // 表单相关
      dialogShow: false,
      dialogForm: {
        id: '',
        reason: '',
        result: ''
      },
      dialogFormRules: {
        id: [{ required: true, trigger: 'blur' }],
        reason: [{ required: true, trigger: 'blur' }],
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
        const { data } = await getAuditPageInfo(current, pageSize)
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
        reason: '',
        id: '',
        result: 1
      }
      // 标识审批操作
      this.isAdd = false
      // 展示信息
      // console.log(index, row);
      passObj.id = row.id

      updateAuditResource(passObj)
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
      //   reason: "",
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
      publishAuditResource(id)
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
          delAudit(id)
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
          updateAuditResource(this.dialogForm)
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
      // 如果
      this.reset()
    },
    // 表单重置
    reset() {
      this.dialogForm = {
        id: '',
        reason: '',
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
    },
    handlePreview(index, row) {
      console.log(index)
      console.log(row)

      // 赋予初值
      this.previewDialogObj.title = row.name
      this.previewDialogObj.info = row.info
      this.previewDialogObj.url = row.url

      // 渲染信息
      // console.log(this.$refs.inserthtml.innerHTML);
      nextTick(() => {
        console.log(this.$refs)

        this.$refs.inserthtml.innerHTML += this.previewDialogObj.info
      }, 1000)

      // 文件后缀处理
      if (row.url === '') {
        // console.log(row.url)
        this.fileIsImg = false
        this.fileIsVideo = false
        this.fileIsDoc = false
      } else {
        this.fileSuffix = this.previewDialogObj.url
          .split('.')
          .reverse()[0]
          .toLowerCase()

        // console.log(this.fileSuffix)
        this.isFileType(this.fileSuffix)
      }
      // 展示
      this.previewDialogShow = true
    },
    previewDialogCancel() {
      this.previewDialogShow = false
      // 通知video组件
      this.$bus.$emit(`dialogCancel`)
    },
    isFileType(suffix) {
      console.log(suffix)
      // 判断文件是什么类型
      // console.log("fileIsImg=>", this.imgType.includes(suffix));
      this.fileIsImg = this.imgType.includes(suffix)
      // return str.includes(suffix);
      if (!this.fileIsImg) {
        // console.log("fileIsVideo=>", this.vidioType.includes(suffix));
        this.fileIsVideo = this.vidioType.includes(suffix)
        // return str.includes(suffix);
      }
      if (!this.fileIsVideo) {
        // console.log("fileIsDoc=>", this.docType.includes(suffix));
        this.fileIsDoc = this.docType.includes(suffix)
        if (this.fileIsDoc) {
          this.docPreviewUrl = `https://view.officeapps.live.com/op/view.aspx?src=${this.previewDialogObj.url}`
        }
      }
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
