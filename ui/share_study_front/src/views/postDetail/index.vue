<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import {
  defineComponent,
  onMounted,
  ref,
  computed,
  inject,
  onBeforeUnmount,
} from "vue";

import { useRouter, useRoute, LocationQueryValue } from "vue-router";
import $bus from "@/libs/eventBus";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance } from "element-plus";

import {
  getPostDetail,
  addPostComment,
  getPostCommentPage,
  addCollect,
  delPostComment,
  // delCollect
} from "@/api/post";
import { ossFileUpload, delOssFile } from "@/api/oss";
import DefaultAvatar from "@/components/DefaultAvatar/index.vue";
import VideoPlayer from "@/components/VideoPlayer/index.vue";
import VueEmojis from "@/components/VueEmojis/index.vue";

import { UserInfoMember } from "@/store/user";
import { PostListMember } from "@/views/home/index.vue";
import { getTokenData } from "@/utils";

// 规范评论表单
export interface CommentFormMember {
  send: string; // 接收评论的id
  content: string; // 内容
  url: string; // 附件url
  resource: string; // 评论所属资料ID
}
// 规范评论列表
export interface CommentListMember {
  belongAvatarUrl: string;
  belongName: string;
  belongCollege: string;
  commentId: "string";
  commentContent: string;
  commentOssUrl: string;
  belong: string;
  sendName: string;
}
// 规范收藏表单
export interface CollectFormMember {
  belong: string;
  resource: string;
}

export default defineComponent({
  components: {
    DefaultAvatar,
    VideoPlayer,
    VueEmojis,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    const router = useRouter();
    const route = useRoute();

    const $message: any = inject("$message");

    const insertHtmlRef = ref();
    // loading
    const skeletonLoading = ref<boolean>(true);
    // 帖子信息相关
    const postDetail = ref<PostListMember>({
      collectStatus: "",
      collegeName: "",
      commentCount: 0,
      createTime: "",
      resourceId: "",
      resourceInfo: "",
      resourceName: "",
      resourceScore: 0,
      resourceStatus: 0,
      resourceUrl: "",
      userAvatarUrl: "",
      userId: "",
      userName: "",
    });
    const postId = ref<string>("");
    const collectStatus = ref<LocationQueryValue>("0"); // 默认收藏
    // 帖子 显示图片/视频 文档
    const fileSuffix = ref<string>("");
    const imgType = ref<string>("png jpg jpeg");
    const vidioType = ref<string>("mp4 mpeg");
    const docType = ref<string>("pdf xlsx xls doc docx ppt pptx");
    const volume = ref<number>(0.5);
    const fileIsImg = ref<boolean>();
    const fileIsVideo = ref<boolean>();
    const fileIsDoc = ref<boolean>(false);
    const docPreviewUrl = ref<string>("");
    const isFocus = ref<boolean>(false);
    // 评论
    const commentsList = ref<CommentListMember[]>([]);
    const currentPage = ref<number>(1);
    const pageSize = ref<number>(100);
    const sendId = ref<string>("");
    const isEmojis = ref<boolean>(false);
    const sendName = ref<string>("");
    const formRef = ref<FormInstance>(); // 获取formRef dom节点
    const form = ref<CommentFormMember>({
      send: "", // 接收评论的id
      content: "", // 内容
      url: "", // 附件url
      resource: "", // 评论所属资料ID
    });

    const fileList = ref<string[]>([]); // 评论附件列表
    const fileUrl = ref<string>("");
    const fileType = ref<string[]>([
      "png",
      "jpg",
      "jpeg",
      "gif",
      "pdf",
      "xlsx",
      "xls",
      "doc",
      "docx",
      "ppt",
      "pptx",
      "mp3",
      "mp4",
      "mpeg",
      "zip",
      "rar",
      "7z",
      "py",
      "java",
      "c",
      "cpp",
      "go",
      "html",
      "js",
      "ts",
      "sql",
      "css",
    ]);
    const action = ref<string>("/api/file/oss_file_upload");
    const limit = ref<number>(1);
    const collectForm = ref<CollectFormMember>({
      belong: "", // 当前用户id
      resource: "", // 资料id
    });
    const collectId = ref<string>("");
    const userInfo = ref<UserInfoMember>({
      account: "",
      avatar: "",
      collegeName: "",
      createTime: "",
      email: "",
      gender: 0,
      id: "",
      isLogin: false,
      messageNumber: 0,
      name: "",
      role: 0,
      score: 0,
    });

    // 方法 methods
    // 获取帖子详细消息
    const getPostDetailInfo = async () => {
      const result = await getPostDetail(postId.value);
      if (result === undefined) return;

      const data = result.data;
      postDetail.value = data;
      // console.log(postDetail.value);
      // 获取 要回复的人
      sendId.value = data.userId;
      sendName.value = data.userName;

      // 设置后缀信息
      if (data.resourceUrl != "") {
        // 没有上传资源
        fileSuffix.value = data.resourceUrl
          .split(".")
          .reverse()[0]
          .toLowerCase();
        // console.log(fileSuffix.value);
        isFileType(fileSuffix.value);
      }
      // 渲染信息
      // console.log(insertHtmlRef.value);
      insertHtmlRef.value.innerHTML += postDetail.value.resourceInfo;
      skeletonLoading.value = false;
    };

    // 修改帖子信息
    const toUpdatePost = (id: string) => {
      // this.$router.push({ path: "/addPost/" + id });
      router.push({ path: "/sendPost/" + id });
    };

    const isFileType = (suffix: string) => {
      // 判断文件是什么类型
      console.log("fileIsImg=>", imgType.value.includes(suffix));
      fileIsImg.value = imgType.value.includes(suffix);
      // return str.includes(suffix);
      if (!fileIsImg.value) {
        console.log("fileIsVideo=>", vidioType.value.includes(suffix));
        fileIsVideo.value = vidioType.value.includes(suffix);
        // return str.includes(suffix);
      }
      if (!fileIsVideo.value) {
        console.log("fileIsDoc=>", docType.value.includes(suffix));
        fileIsDoc.value = docType.value.includes(suffix);

        if (fileIsDoc.value) {
          docPreviewUrl.value = `https://view.officeapps.live.com/op/view.aspx?src=${postDetail.value.resourceUrl}`;
        }
      }
    };

    // 获取路由中的参数
    const getParams = () => {
      postId.value = route.params.id as string;
      collectStatus.value = route.query.collectStatus as LocationQueryValue;
      // console.log(collectStatus.value);
      // // console.log(this.postId);
    };
    // 聚焦输入框
    const inputFocus = () => {
      isFocus.value = true;
      isEmojis.value = false; //
    };
    // 点击收藏
    const handleCollect = () => {
      collectForm.value.resource = postId.value;
      collectForm.value.belong = postDetail.value.userId;
      addCollect(collectForm.value).then((res: any) => {
        collectStatus.value = collectStatus.value === "1" ? "0" : "1";
        $message.success(res.message);
      });
    };
    /* 评论相关方法 */
    // 获取评论分页
    const getPostCommentPageInfo = async (
      id: string,
      current: number,
      pageSize: number
    ) => {
      const { data } = await getPostCommentPage(id, current, pageSize);
      commentsList.value = data.records;
    };
    const delComment = (id: string) => {
      ElMessageBox.confirm("是否删除该评论?", "删除", {
        confirmButtonText: "删除",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          delPostComment(id).then((res) => {
            // 成功
            ElMessage({
              type: "success",
              message: "删除成功",
            });
            // 更新评论
            getPostCommentPageInfo(
              postId.value,
              currentPage.value,
              pageSize.value
            );
          });
        })
        .catch(() => {
          ElMessage({
            type: "info",
            message: "已取消删除",
          });
        });
    };
    // 回复 非 帖子作者
    const commentOther = (id: string, name: string) => {
      sendId.value = id;
      sendName.value = name;
      isFocus.value = true;
    };
    // 点击评论
    const commentSubmit = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      formEl.validate(async (valid) => {
        if (valid) {
          // this.submitLoading = true;
          // 上传接口
          // 判断是否存在需要上传文件
          if (fileList.value[0]) {
            let FormDatas = new window.FormData();
            FormDatas.append("file", fileList.value[0]);
            // console.log("FormDatas=>", FormDatas);
            const { data } = await ossFileUpload(FormDatas);
            fileUrl.value = data;
            form.value.url = data; // form的url
          }
          // 调用发布接口
          form.value.resource = postDetail.value.resourceId; // 接收评论的资料id
          form.value.send = sendId.value; // 接收评论的用户id

          addPostComment(form.value)
            .then((res) => {
              $message("评论成功");
              // this.submitLoading = false;
              // this.$router.push({ path: '/' });
              resetComment(formEl); // 清除表单
              fileList.value = [];
              getPostCommentPageInfo(
                postId.value,
                currentPage.value,
                pageSize.value
              );
            })
            .catch((error) => {
              // console.log(error);
              $message("你不能回复自己！！！");
              // 上传成功，但发布失败情况
              if (fileUrl.value) {
                // 删除oss文件
                delOssFile(fileUrl.value);
              }
              // this.submitLoading = false;
            });
        }
      });
    };
    // 评论
    // handleComment() {
    //   console.log(this.commentsList);
    // },
    // 登录表单重置
    const resetComment = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      form.value = {
        send: "", // 接收评论的id
        content: "", // 内容
        url: "", // 附件url
        resource: "", // 评论所属资料ID
      };
      formEl.resetFields();
    };
    /* 评论资料上传相关方法 */
    const handleRemove = (file: any, fileList: any) => {
      // console.log(file, fileList);
    };
    const handlePreview = (file: any) => {
      // console.log(file);
    };
    const handleExceed = (files: any, fileList: any) => {
      $message.warning(
        `当前限制选择 ${limit.value} 个文件，本次选择了 ${
          files.length
        } 个文件，共选择了 ${files.length + fileList.length} 个文件`
      );
    };
    const beforeRemove = (file: any, fileList: any) => {
      return ElMessageBox.confirm(`确定移除 ${file.name}？`);
    };
    // 上传之前的回调
    const beforeUpload = (file: any) => {
      if (file.type != "" || file.type != null || file.type != undefined) {
        //截取文件的后缀，判断文件类型
        const FileExt = file.name.replace(/.+\./, "").toLowerCase();
        //计算文件的大小
        const isLt5M = file.size / 1024 / 1024 < 200; //这里做文件大小限制
        //如果大于50M
        if (!isLt5M) {
          $message.error("上传文件大小不能超过 200MB!");
          return false;
        }
        //如果文件类型不在允许上传的范围内
        if (fileType.value.includes(FileExt)) {
          return true;
        } else {
          $message.error("上传文件格式不正确!");
          return false;
        }
      }
    };
    const uploadFile = (item: any) => {
      // this.$message.info("文件已上传至浏览器，点击发布上传至服务器");
      //上传文件的需要formdata类型;所以要转
      // console.log(item.file);
      fileList.value.push(item.file);
    };

    // 计算方法 computed

    // 监听 watch

    // 事件
    const cancelFocusEvent = (val: any) => {
      isFocus.value = false;
    };

    const addEmojiEvent = (item: any) => {
      // console.log(item.text);
      form.value.content += item.text;
    };

    // 生命周期钩子
    onMounted(() => {
      $bus.on("cancelFocus", cancelFocusEvent);
      getParams(); // 获取路由中的参数
      getPostDetailInfo();
      getPostCommentPageInfo(postId.value, 1, 100);
      userInfo.value = getTokenData() as UserInfoMember;
      // 点击 emoji 时添加到输入框
      $bus.on("addEmoji", addEmojiEvent);
    });

    // 在组件卸载之前移除侦听
    onBeforeUnmount(() => {
      $bus.off("addEmoji", addEmojiEvent);
      $bus.off("cancelFocus", cancelFocusEvent);
    });

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      skeletonLoading,
      postDetail,
      postId,
      collectStatus,
      fileSuffix,
      imgType,
      vidioType,
      docType,
      volume,
      fileIsImg,
      fileIsVideo,
      fileIsDoc,
      docPreviewUrl,
      isFocus,
      commentsList,
      sendId,
      isEmojis,
      sendName,
      form,
      fileList,
      fileUrl,
      fileType,
      action,
      limit,
      collectForm,
      collectId,
      userInfo,
      insertHtmlRef,
      formRef,

      // 计算属性

      // 方法
      getPostDetailInfo,
      toUpdatePost,
      isFileType,
      getParams,
      inputFocus,
      handleCollect,
      delComment,
      commentOther,
      commentSubmit,
      handleRemove,
      handlePreview,
      handleExceed,
      beforeRemove,
      beforeUpload,
      uploadFile,
    };
  },
});
</script>

<template>
  <!-- 帖子+评论 -->
  <div class="post-detail-container">
    <!-- 骨架屏 -->
    <el-skeleton animated :loading="skeletonLoading">
      <template #template>
        <el-row>
          <!-- 帖子详细 -->
          <div class="post-detail" style="width: 100%">
            <el-row>
              <el-col :span="8">
                <el-skeleton-item></el-skeleton-item>
              </el-col>
            </el-row>
            <!-- 帖子标题 -->
            <el-row>
              <div class="post-title">
                <el-col :span="24">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
              </div>
            </el-row>
            <!-- 作者信息 -->
            <div class="post-author">
              <!-- 头像 -->
              <div class="post-avater">
                <el-row>
                  <div class="avater">
                    <el-col>
                      <el-skeleton-item
                        variant="image"
                        style="height: 40px; border-radius: 50%"
                      ></el-skeleton-item>
                    </el-col>
                  </div>
                </el-row>
              </div>

              <div class="avater-description" style="width: 9%">
                <el-row>
                  <!-- 姓名 -->
                  <el-col :span="12">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                </el-row>
                <el-row>
                  <!-- 所属高校 -->
                  <el-col :span="24">
                    <el-skeleton-item></el-skeleton-item>
                  </el-col>
                </el-row>
              </div>
            </div>
            <!-- 帖子信息 -->
            <el-row>
              <div class="post-info" style="width: 100%">
                <el-col :span="15">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
              </div>
            </el-row>

            <!-- 附件信息 -->
            <div class="post-download">
              <el-row>
                <el-col :span="3">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
              </el-row>
            </div>

            <!-- 收藏按钮 -->
            <div class="button-container">
              <el-row :gutter="10">
                <el-col :span="2">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
                <el-col :span="2">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
              </el-row>
            </div>
          </div>

          <!-- 发布评论 -->
          <div class="add-comment">
            <div class="add-comment-header">
              <el-row>
                <div class="current-avater">
                  <el-col>
                    <el-skeleton-item
                      variant="image"
                      style="height: 40px; width: 40px; border-radius: 50%"
                    ></el-skeleton-item>
                  </el-col>
                </div>
              </el-row>
              <div class="form" style="width: 100%">
                <el-row style="width: 100%">
                  <!-- 姓名 -->
                  <el-col :span="24">
                    <el-skeleton-item
                      variant="image"
                      style="height: 66px"
                    ></el-skeleton-item>
                  </el-col>
                </el-row>
              </div>
            </div>
          </div>

          <!-- 评论分页 -->
          <div class="comment-container" style="width: 100%">
            <h3 class="comment-title">
              <el-row>
                <el-col :span="6">
                  <el-skeleton-item></el-skeleton-item>
                </el-col>
              </el-row>
            </h3>
            <!-- 单个评论 -->
            <div class="comment-item">
              <!-- 作者信息 -->
              <div class="comment-author">
                <!-- 头像 -->
                <div class="comment-avater">
                  <el-row>
                    <!-- 姓名 -->
                    <el-col :span="6">
                      <el-skeleton-item
                        variant="image"
                        style="height: 40px; width: 40px; border-radius: 50%"
                      ></el-skeleton-item>
                    </el-col>
                  </el-row>
                </div>
              </div>
              <!-- 评论详细 -->
              <div class="comment-detail">
                <!-- 用户描述 -->
                <div class="avater-description">
                  <!-- 姓名 -->
                  <div class="comment-name" style="width: 10%">
                    <el-row>
                      <!-- 姓名 -->
                      <el-col :span="24">
                        <el-skeleton-item></el-skeleton-item>
                      </el-col>
                    </el-row>
                  </div>
                  <!-- 所属高校 -->
                  <div class="comment-college" style="width: 16%">
                    <el-row>
                      <el-col :span="24">
                        <el-skeleton-item></el-skeleton-item>
                      </el-col>
                    </el-row>
                  </div>
                </div>
                <!-- 帖子信息 -->
                <div class="comment-info" style="width: 600px">
                  <el-row>
                    <el-col :span="24">
                      <el-skeleton-item></el-skeleton-item>
                    </el-col>
                  </el-row>
                </div>

                <div class="comment-action">
                  <!-- 附件信息 -->
                  <div class="comment-download" style="width: 10%">
                    <el-row>
                      <el-col :span="24">
                        <el-skeleton-item></el-skeleton-item>
                      </el-col>
                    </el-row>
                  </div>
                  <!-- 评论 -->
                  <div class="add-comment-item" style="width: 10%">
                    <el-row>
                      <el-col :span="24">
                        <el-skeleton-item></el-skeleton-item>
                      </el-col>
                    </el-row>
                  </div>
                </div>

                <!-- 回复 -->
                <div class="comment-send-name" style="width: 10%">
                  <el-row>
                    <el-col :span="24">
                      <el-skeleton-item></el-skeleton-item>
                    </el-col>
                  </el-row>
                </div>
              </div>
            </div>
          </div>
        </el-row>
      </template>
    </el-skeleton>

    <!-- 帖子详细 -->
    <div class="post-detail">
      <!-- 帖子标题 -->
      <div class="post-title">
        <h2>{{ postDetail.resourceName }}</h2>
      </div>
      <!-- 作者信息 -->
      <div class="post-author">
        <!-- 头像 -->
        <div class="post-avater">
          <default-avatar
            v-if="postDetail.userAvatarUrl === ''"
            width="40px"
            height="40px"
            :avatarName="postDetail.userName.split('')[0]"
          ></default-avatar>
          <img
            class="avater"
            :src="postDetail.userAvatarUrl"
            alt="作者"
            v-else
          />
        </div>
        <div class="avater-description">
          <!-- 姓名 -->
          <div class="post-name">
            {{ postDetail.userName }}
          </div>
          <!-- 所属高校 -->
          <div class="post-college">
            {{ postDetail.collegeName }}
          </div>
        </div>
      </div>
      <!-- 帖子信息 -->
      <div class="post-info">
        <div
          class="insert-html"
          style="white-space: pre-line"
          ref="insertHtmlRef"
        >
          <!-- {{ postDetail.resourceInfo }} -->
        </div>

        <!-- 如果附件是以 图片 / 视频的形式 则显示在帖子中 -->
        <div class="img-container" v-if="fileIsImg">
          <img :src="postDetail.resourceUrl" alt="图片" style="width: 600px" />
        </div>

        <div
          class="movies-container"
          v-else-if="fileIsVideo"
          style="height: 400px"
        >
          <video-player
            :src="postDetail.resourceUrl"
            :volume="volume"
            style="width: 100%; height: 100%"
          ></video-player>
          <!-- <audio :src="" alt="视频"></audio> -->
        </div>

        <div
          class="doc-container"
          style="width: 100%; margin-top: 30px"
          v-else-if="fileIsDoc"
        >
          <iframe
            :src="docPreviewUrl"
            frameborder="0"
            width="100%"
            height="600"
          ></iframe>
        </div>
      </div>

      <!-- 附件信息 -->
      <div class="post-download">
        <a
          class="download"
          :href="postDetail.resourceUrl"
          target="blank"
          download="下载"
          v-if="postDetail.resourceUrl != ''"
          ><span class="iconfont icon-fujian"></span>附件查看/下载</a
        >
      </div>
      <!-- 收藏按钮 -->
      <div class="button-container">
        <el-button
          class="btn-collect"
          type="primary"
          size="default"
          @click="handleCollect"
          ><span
            class="iconfont icon-star"
            :class="collectStatus === '1' ? 'icon-star-fill' : 'icon-star'"
          ></span
          >{{ collectStatus === "1" ? "已收藏" : "收藏" }}</el-button
        >
        <el-button
          class="btn-collect"
          type="primary"
          size="default"
          @click="commentOther(postDetail.userId, postDetail.userName)"
          ><span class="iconfont icon-message"></span>回复</el-button
        >
        <el-button
          class="btn-collect"
          type="primary"
          size="default"
          @click="toUpdatePost(postId)"
          v-if="postDetail.userId === userInfo.id"
          ><span class="iconfont icon-message"></span>修改</el-button
        >
      </div>
    </div>

    <!-- 发布评论 -->
    <div class="add-comment" @focus="isFocus = true" tabindex="0">
      <div class="add-comment-header">
        <div class="current-avater">
          <default-avatar
            v-if="userInfo.avatar === ''"
            width="40px"
            height="40px"
            :avatarName="userInfo.name.split('')[0]"
          ></default-avatar>
          <img :src="userInfo.avatar" alt="当前用户" v-else />
        </div>
        <el-form class="form" ref="formRef" :model="form" label-width="0">
          <el-form-item class="add-comment-form-item" label="">
            <el-input
              type="textarea"
              v-model="form.content"
              @focus="inputFocus"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div
        slot="footer"
        class="add-comment-footer"
        :class="isFocus ? 'comment-footer-flex' : 'comment-footer-none'"
      >
        <div class="add-left">
          <div class="add-smile" @click="isEmojis = !isEmojis">
            <span class="iconfont icon-smile"></span>
            <vue-emojis class="vue-emojis" v-show="isEmojis"></vue-emojis>
          </div>
          <el-upload
            class="add-comment-upload"
            action="action"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            multiple
            :limit="limit"
            :on-exceed="handleExceed"
            :file-list="fileList"
            :http-request="uploadFile"
          >
            <el-link :underline="false"
              ><span class="iconfont icon-fujian"></span> 附件上传</el-link
            >
          </el-upload>
        </div>
        <el-button
          class="btn-comment"
          type="primary"
          size="default"
          @click="commentSubmit(formRef)"
          ><span class="iconfont icon-message"></span>回复{{
            sendName
          }}</el-button
        >
      </div>
    </div>

    <!-- 评论分页 -->
    <div
      class="comment-container"
      v-if="commentsList.length === 0 ? false : true"
    >
      <div class="comment-container-title">
        <h3 class="comment-title">全部评论</h3>
        <!-- <div class="btn-group">
          <button @click="handleComment">评论最多</button>
          <button @click="handleComment">最新</button>
        </div> -->
      </div>
      <div
        class="comment-item"
        v-for="(commentItem, index) in commentsList"
        :key="commentItem.commentId"
      >
        <!-- 作者信息 -->
        <div class="comment-author">
          <!-- 头像 -->
          <div class="comment-avater">
            <default-avatar
              v-if="commentItem.belongAvatarUrl === ''"
              width="40px"
              height="40px"
              :avatarName="commentItem.belongName.split('')[0]"
            ></default-avatar>
            <img
              class="avater"
              :src="commentItem.belongAvatarUrl"
              alt="评论人"
              v-else
            />
          </div>
        </div>
        <!-- 评论详细 -->
        <div class="comment-detail">
          <!-- 用户描述 -->
          <div class="avater-description">
            <!-- 姓名 -->
            <div class="comment-name">
              <h5>{{ commentItem.belongName }}</h5>
            </div>
            <!-- 所属高校 -->
            <div class="comment-college">
              {{ commentItem.belongCollege }}
            </div>
          </div>
          <!-- 帖子信息 -->
          <div class="comment-info">
            <span>{{ commentItem.commentContent }}</span>
          </div>

          <div class="comment-action">
            <!-- 附件信息 -->
            <div class="comment-download" v-if="commentItem.commentOssUrl">
              <a
                class="download"
                target="blank"
                :href="commentItem.commentOssUrl"
                download="下载"
                ><span class="iconfont icon-fujian"></span>附件下载</a
              >
            </div>
            <!-- 评论 -->
            <div
              class="add-comment-item"
              @click="commentOther(commentItem.belong, commentItem.belongName)"
            >
              <span class="iconfont icon-message"></span>
              <span class="add-action">回复</span>
            </div>
            <div
              class="add-comment-item"
              v-if="commentItem.belong === userInfo.id"
              @click="delComment(commentItem.commentId)"
            >
              <span class="iconfont icon-message"></span>
              <span class="add-action">删除</span>
            </div>
          </div>

          <!-- 回复 -->
          <div class="comment-send-name">
            <span>回复于{{ commentItem.sendName }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="nocomment-container" v-else style="height: 300px">
      <el-empty class="default" description="暂时还没有评论~" />
      <!-- <h3 class="default">暂时还没有评论~</h3> -->
    </div>
  </div>
</template>

<style lang="scss">
/*大型屏幕pc 超大屏*/
@media screen and (min-width: 1200px) {
  .post-detail-container {
    width: 960px;
  }
}
/*1200>=pc>=992 大屏，字体红色，背景黑色*/
@media screen and (min-width: 992px) and (max-width: 1199px) {
  .post-detail-container {
    width: 960px;
  }
}
/*768<=pad<992 中屏，字体黄色，背景红色*/
@media screen and (min-width: 768px) and (max-width: 991px) {
  .post-detail-container {
    width: 768px;
  }
}
/*phone<768  小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 767px) and (min-width: 480px) {
}
/* 超小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 480px) {
}

.post-detail-container {
  // width: 960px;
  padding-top: 80px;
  margin: 0 auto;

  // 帖子详情
  .post-detail {
    padding: 30px;
    background-color: #fff;
    border-radius: 10px;
    // border: 1px solid red;

    // 帖子标题
    .post-title {
      border-bottom: 1px solid #909399;
      padding-bottom: 10px;
    }

    // 帖子作者
    .post-author {
      margin: 10px 0 0 0px;
      position: relative;
      display: flex;
      border-bottom: 1px solid #909399;
      padding-bottom: 10px;
      padding-left: 10px;

      .post-avater {
        .avater {
          width: 42px;
          border-radius: 50%;
        }
      }

      .avater-description {
        margin-left: 10px;
        padding-right: 10px;
        border-right: 1px solid #909399;

        // 姓名
        .post-name {
          font-size: 16px;
          font-weight: bold;
        }

        // 高校
        .post-college {
          font-size: 8px;
          margin-top: 2px;
        }
      }
    }

    // 帖子信息
    .post-info {
      margin-top: 30px;

      // 富文本样式
      .insert-html {
        margin-bottom: 30px;

        pre {
          background: #2d2d2d;
          color: rgb(201, 209, 217);
          font-family: Consolas;
          text-align: left;
          padding: 1em;
          padding-left: 0.8em;
          margin: 1em;
          border-radius: 5px;
          counter-reset: line;
          white-space: pre;
          word-spacing: normal;
          word-break: normal;
          word-wrap: normal;
          line-height: 1.5;
        }
      }

      // 图片附件
      .img-container {
        display: flex;
        justify-content: center;
      }

      // 视频附件
      .movies-container {
        display: flex;
        justify-content: center;
      }

      // 文档附件
      .doc-container {
        display: flex;
        justify-content: center;
      }
    }

    // 附件下载
    .post-download {
      margin: 50px 20px 0px 0px;

      .download {
        color: #909399;
      }

      .download:hover {
        color: #029dff;
      }
    }

    // 按钮
    .button-container {
      margin-top: 30px;

      .btn-collect {
        .icon-star-fill {
          color: #ffd700;
        }
      }
    }
  }

  // 发布评论
  .add-comment {
    width: calc(100% - 60px);
    background-color: #fff;
    border-radius: 10px;
    margin-right: 0px;
    padding: 30px;
    margin-top: 20px;

    .add-comment-header {
      width: 100%;
      border-radius: 10px;
      display: flex;

      .current-avater {
        margin-right: 20px;

        img {
          width: 42px;
          border-radius: 50%;
        }
      }

      .el-form {
        width: calc(100% - 60px);

        .add-comment-form-item {
          margin-bottom: 0px;
          // width: 838px;

          .el-textarea {
            textarea {
              max-width: 100%;
              height: 66px;
              padding: 8px 12px 8px 12px;
              background-color: #e9ecef;
              width: 100%;
            }

            textarea:focus {
              background-color: #fff;
            }
          }
        }
      }
    }

    .add-comment-footer {
      margin-top: 10px;
      // padding-left: 62px;
      display: none;
      justify-content: space-between;
      position: relative;

      .add-left {
        display: flex;
        align-items: center;

        .add-smile {
          position: relative;
          cursor: pointer;
          font-size: 14px;
          margin-right: 10px;
          .icon-smile {
          }
        }

        // emojis 容器
        .vue-emojis {
          top: 30px;
          left: 0;
          z-index: 1;
        }

        // 上传
        .add-comment-upload {
          .el-upload-list {
            position: absolute;
          }

          // .el-link {
          //   .span {
          //     // margin-right: 7px;
          //   }
          // }
        }
      }

      //
      // .btn-comment {
      // }
    }

    .comment-footer-flex {
      display: flex;
    }

    .comment-footer-none {
      display: none;
    }
  }

  // 分页评论
  .comment-container {
    margin-top: 20px;
    border-radius: 10px;
    background-color: #fff;

    .comment-container-title {
      display: flex;
      align-items: center;

      .comment-title {
        padding: 10px 30px;
      }

      // 最新 按钮 评论最多按钮
      // .btn-group {
      //   button {
      //     appearance: none;
      //     background-color: #fafbfc;
      //     border: 1px solid rgba(27, 31, 35, 0.15);
      //     border-radius: 6px;
      //     box-shadow: rgba(27, 31, 35, 0.04) 0 1px 0,
      //       rgba(255, 255, 255, 0.25) 0 1px 0 inset;
      //     box-sizing: border-box;
      //     color: #24292e;
      //     cursor: pointer;
      //     display: inline-block;
      //     font-family: -apple-system, system-ui, "Segoe UI", Helvetica, Arial,
      //       sans-serif, "Apple Color Emoji", "Segoe UI Emoji";
      //     font-size: 10px;
      //     font-weight: 500;
      //     line-height: 20px;
      //     list-style: none;
      //     padding: 3px 8px;
      //     position: relative;
      //     transition: background-color 0.2s cubic-bezier(0.3, 0, 0.5, 1);
      //     user-select: none;
      //     -webkit-user-select: none;
      //     touch-action: manipulation;
      //     vertical-align: middle;
      //     white-space: nowrap;
      //     word-wrap: break-word;
      //   }

      //   button:hover {
      //     background-color: #f3f4f6;
      //     text-decoration: none;
      //     transition-duration: 0.1s;
      //   }

      //   button:disabled {
      //     background-color: #fafbfc;
      //     border-color: rgba(27, 31, 35, 0.15);
      //     color: #959da5;
      //     cursor: default;
      //   }

      //   button:active {
      //     background-color: #edeff2;
      //     box-shadow: rgba(225, 228, 232, 0.2) 0 1px 0 inset;
      //     transition: none 0s;
      //   }

      //   button:focus {
      //     outline: 1px transparent;
      //   }

      //   button:before {
      //     display: none;
      //   }

      //   button:-webkit-details-marker {
      //     display: none;
      //   }
      // }
    }

    .comment-item {
      padding: 10px 30px;
      display: flex;
      position: relative;
      // border: 1px solid red;

      // 帖子作者
      .comment-author {
        position: relative;
        display: flex;
        padding-left: 10px;
        // margin: 10px 0 0 0px;
        // border-bottom: 1px solid #909399;
        // padding-bottom: 10px;

        .comment-avater {
          .avater {
            width: 40px;
            border-radius: 50%;
          }
        }
      }

      // 评论详细
      .comment-detail {
        .avater-description {
          margin-left: 10px;
          padding-right: 10px;
          // border-right: 1px solid #909399;
          display: flex;

          // 姓名
          .comment-name {
            border-right: 1px solid #909399;
            padding-right: 10px;
            font-size: 14px;
          }

          // 高校
          .comment-college {
            padding-left: 5px;
            font-size: 8px;
            margin-top: 2px;
          }
        }

        // 评论信息
        .comment-info {
          margin-top: 20px;
          margin-left: 20px;
          white-space: pre-line; // 遇到/n 或者 <br/>自动换行

          span {
            font-size: 12px;
          }
        }

        .comment-action {
          display: flex;

          // 附件下载
          .comment-download {
            margin: 20px 0px 0px 10px;
            font-size: 12px;

            .download {
              color: #909399;
            }

            .download:hover {
              color: #029dff;
            }
          }

          // 回复
          .add-comment-item {
            margin: 20px 0px 0px 10px;
            font-size: 12px;
            cursor: pointer;
            color: #909399;

            // .icon-message {}

            .add-action {
              font-size: 10px;
            }
          }

          .add-comment-item:hover {
            color: #029dff;
          }
        }
      }

      .comment-send-name {
        position: absolute;
        top: 10px;
        right: 30px;

        span {
          font-size: 12px;
        }
      }
    }

    .comment-item:nth-child(odd) {
      background-color: #fafafa;
    }
  }

  // 无评论时

  .nocomment-container {
    height: 200px;
    margin-top: 20px;
    border-radius: 10px;
    background-color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;

    .nocomment-title {
      color: #aaaaaa;
    }
  }
}
</style>
