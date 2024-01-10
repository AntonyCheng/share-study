<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, ref, watch, computed, inject } from "vue";
import DefaultAvatar from "@/components/DefaultAvatar/index.vue";

import $bus from "@/libs/eventBus";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/store";

import { UserInfoMember } from "@/store/user";

import {
  getUserCommentPage,
  getCollectPage,
  getUserPostPage,
} from "@/api/post";
import { ossAvatarUpload, delOssFile } from "@/api/oss";
import { updateUserInfo, getInfo } from "@/api/login";

import type {
  FormInstance,
  FormRules,
  UploadProps,
  UploadUserFile,
} from "element-plus";
import { CommentListMember } from "@/layout/Header.vue";

// 接口规范 表单
export interface SelfFormMember {
  email: string;
  name: string;
  avatar: string;
  account: string;
  password: string;
  gender: number | string;
  belong: string;
  id: string;
}

// 接口规范 收藏列表
export interface PostMember {
  collectId: string;
  userAvatarUrl: string;
  userName: string;
  collegeName: string;
  resourceName: string;
  resourceUrl: string;
  resourceId: string;
  createTime: string;
}

export default defineComponent({
  components: {
    DefaultAvatar,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    const route = useRoute();
    const router = useRouter();
    const userStore = useUserStore();
    const $message: any = inject("$message");

    // 校验
    const validatePass = (rule: any, value: any, callback: any) => {
      if (value === "") {
        callback(new Error("请输入密码"));
      } else {
        if (selfForm.value.password !== "") {
          if (!selfFormRef.value) return;
          selfFormRef.value.validateField("password", () => {});
        }
        callback();
      }
    };

    // 变量
    const activeName = ref<string>("userInfo");
    const userCommentList = ref<CommentListMember[]>([]);
    const collectList = ref<PostMember[]>([]);
    const collectTotal = ref<number>(0);
    const postList = ref<PostMember[]>([]);
    const postTotal = ref<number>(0);

    const selfForm = ref<SelfFormMember>({
      email: "",
      name: "",
      avatar: "",
      account: "",
      password: "",
      gender: "",
      belong: "",
      id: "",
    });
    const selfFormRef = ref<FormInstance>();
    const selfFormRules = ref<FormRules>({
      email: [{ required: true, trigger: "blur" }],
      account: [{ required: true, trigger: "blur" }],
      password: [{ validator: validatePass, trigger: "blur" }],
      avatar: [{ required: true, trigger: "blur" }],
      name: [{ required: true, trigger: "blur" }],
      gender: [{ required: true, trigger: "blur" }],
      belong: [{ required: false, trigger: "blur" }],
      id: [{ required: true, trigger: "blur" }],
    });
    const isSelfForm = ref<boolean>(true);
    const dialogForm = ref<SelfFormMember>({
      email: "",
      name: "",
      avatar: "",
      account: "",
      password: "",
      gender: "",
      belong: "",
      id: "",
    });
    const difalogFormRef = ref<FormInstance>();
    const dialogFormRules = ref<FormRules>({
      email: [{ required: true, trigger: "blur" }],
      account: [{ required: true, trigger: "blur" }],
      password: [{ validator: validatePass, trigger: "blur" }],
      avatar: [{ required: true, trigger: "blur" }],
      name: [{ required: true, trigger: "blur" }],
      gender: [{ required: true, trigger: "blur" }],
      belong: [{ required: false, trigger: "blur" }],
      id: [{ required: true, trigger: "blur" }],
    });
    // 头像上传
    const lastImageUrl = ref<string>(""); // 上一次的图片路径，用于删除
    const newImageUrl = ref<string>(""); // 当前图片路径，用于更新
    const fileType = ref<string[]>(["bmp", "gif", "jpg", "jpeg", "png"]);
    const action = ref<string>("/api/file/oss_file_upload"); // 上传的地址
    const headers = ref<object>({ "Content-Type": "multipart/form-data" }); // 上传请求头
    const passwordRules = ref<FormRules>({
      password: [{ validator: validatePass, trigger: "blur" }],
    }); // 上一次的图片路径，用于删除
    const showDialog = ref<boolean>(false); // 上一次的图片路径，用于删除
    const dialogTitle = ref<string>("请输入密码以完成上传"); // 上一次的图片路径，用于删除
    const submitLoad = ref<boolean>(false); // 提交loading
    const dialogWidth = ref<string>("620px"); //
    const dialogTop = ref<string>("30px"); //
    const passwordFormLabelWidth = ref<string>("40px"); //

    // 生命周期钩子
    onMounted(async () => {
      await getCurrentUserInfo(queryId.value);
      getUserCommentPageInfo(1, 100);
      getCollectPageInfo(queryId.value, 1, 100);
      getSelfFormInfo(queryId.value);
      getUserPostPageInfo(queryId.value, 1, 100);
      $bus.emit("updateUserInfo", "");
    });

    // 方法 methods
    // 获取用户资料分页
    const getUserPostPageInfo = async (
      id: string,
      current: number,
      pageSize: number
    ) => {
      const { data } = await getUserPostPage(id, current, pageSize);
      postList.value = data.records;
      postTotal.value = data.total;
    };
    // 获取评论资料
    const getUserCommentPageInfo = async (
      current: number,
      pageSize: number
    ) => {
      const { data } = await getUserCommentPage(current, pageSize);
      userCommentList.value = data.records;
    };
    // 获取评论资料
    const getCollectPageInfo = async (
      id: string,
      current: number,
      pageSize: number
    ) => {
      const { data } = await getCollectPage(id, current, pageSize);
      collectList.value = data.records;
      collectTotal.value = data.total;
    };
    // 获取最新用户信息
    const getCurrentUserInfo = async (id: string) => {
      // const { data } = await getCurrentUser(id);
      return await userStore.getInfo(id);
      // console.log(data);
      // this.userInfo = data;
    };
    // 修改个人资料 按钮
    const handleUpdate = () => {
      // 跳转路由
      activeName.value = "";
    };
    // 标签页
    const handleClick = () => {};
    // 跳转资源页
    const handlePushPost = (resourceId: string) => {
      router.push({ path: "/postDetail/" + resourceId });
    };
    /* 头像上传 */
    // 文件上传成功时的钩子
    const handleAvatarSuccess = (res: any, file: any) => {
      // this.imageUrl = URL.createObjectURL(file.raw);
      console.log(res, file);
    };
    // 上传文件之前的钩子，参数为上传的文件，
    // 若返回 false 或者返回 Promise 且被 reject，则停止上传。
    const beforeAvatarUpload = (file: any) => {
      if (file.type != "" || file.type != null || file.type != undefined) {
        //截取文件的后缀，判断文件类型
        const FileExt = file.name.replace(/.+\./, "").toLowerCase();
        //计算文件的大小
        const isLt5M = file.size / 1024 / 1024 < 2; //这里做文件大小限制
        //如果大于50M
        if (!isLt5M) {
          $message.error("上传文件大小不能超过 2MB!");
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
    // 上传图片
    const uploadAvater = async (item: any) => {
      let FormDatas = new window.FormData();
      FormDatas.append("file", item.file);
      console.log(item.file);
      console.log("FormDatas=>", FormDatas);

      $message.info("请输入密码以继续上传");

      const { data } = await ossAvatarUpload(FormDatas);

      //
      lastImageUrl.value = userInfo.value.avatar; // 上一次
      newImageUrl.value = data; // 新上传
      // 输入密码框
      showDialog.value = true;
      // 更新头像接口
      // this.form.url = data; // form的url
    };
    /* dialog密码 */
    // 登录表单重置
    const resetSelfForm = () => {
      selfForm.value = {
        email: "",
        name: "",
        avatar: "",
        account: "",
        password: "",
        gender: "",
        belong: "",
        id: "",
      };
      if (!selfFormRef.value) return;
      selfFormRef.value.resetFields();
      // this.resetForm("passwordForm");
    };
    const resetDialogForm = () => {
      dialogForm.value = {
        email: "",
        name: "",
        avatar: "",
        account: "",
        password: "",
        gender: "",
        belong: "",
        id: "",
      };
      if (!difalogFormRef.value) return;
      difalogFormRef.value.resetFields();
      // difalogFormRef.value.resetFields();
    };

    // 获取表单相关数据数据
    const getdialogFormInfo = async (id: string) => {
      const { data } = await getInfo(id);
      // let key: keyof SelfFormMember;
      let key: string;
      for (key in data) {
        if (dialogForm.value.hasOwnProperty(key)) {
          dialogForm.value[key] = data[key];
        }
      }
    };
    // 获取表单相关数据数据
    const getSelfFormInfo = async (id: string) => {
      let { data } = await getInfo(id);
      let key: keyof SelfFormMember;
      for (key in data) {
        selfForm.value[key] = data[key];
      }
      selfForm.value.gender === 1 ? "男" : "女";
    };

    // 关闭对话框
    const dialogCancel = () => {
      showDialog.value = false;
      // 删除 新头像
      if (newImageUrl.value != "") {
        delOssFile(newImageUrl.value);
        newImageUrl.value = "";
      }
      $message.info("已取消上传。");
      // this.resetRegister();
      resetDialogForm();
    };
    const dialogSubmit = async (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      // 获取数据
      if (queryId.value) await getdialogFormInfo(queryId.value);
      formEl.validate((valid) => {
        if (valid) {
          submitLoad.value = true;
          // 输入密码
          // console.log(this.$store);
          dialogForm.value.avatar = newImageUrl.value;
          // 更新头像数据
          updateUserInfo(dialogForm.value)
            .then((res) => {
              console.log("update finish" + res);
              submitLoad.value = false;
              showDialog.value = false;
              $message.success("上传成功");
              // 删除 旧头像
              if (lastImageUrl.value != "") {
                delOssFile(lastImageUrl.value);
                lastImageUrl.value = "";
              }
              resetDialogForm(); // 重置表单
              // 更新数据
              getCurrentUserInfo(queryId.value).then((res) => {
                // 更新Header数据
                $bus.emit("updateUserInfo", userInfo.value);
              });
            })
            .catch((error) => {
              console.log("update error! Error message:" + error);
              $message.error("上传失败，请重新上传");
              resetDialogForm();
              submitLoad.value = false;
              showDialog.value = false;
              // 删除 新头像
              if (newImageUrl.value != "") {
                delOssFile(newImageUrl.value);
                newImageUrl.value = "";
              }
            });
        }
      });
    };

    // 计算方法 computed
    const queryId = computed(() => {
      // console.log(route.query.id);
      return route.query.id as string;
    });

    const userInfo = computed(() => {
      console.log(userStore.userInfo);
      return userStore.userInfo as UserInfoMember;
    });

    // 监听 watch
    // watch(question, async (newQuestion, oldQuestion) => {
    //   if (newQuestion.indexOf("?") > -1) {
    //     answer.value = "Thinking...";
    //     try {
    //       const res = await fetch("https://yesno.wtf/api");
    //       answer.value = (await res.json()).answer;
    //     } catch (error) {
    //       answer.value = "Error! Could not reach the API. " + error;
    //     }
    //   }
    // });

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      // 变量
      activeName,
      userCommentList,
      collectList,
      collectTotal,
      postList,
      postTotal,
      selfForm,
      selfFormRef,
      selfFormRules,
      isSelfForm,
      dialogForm,
      difalogFormRef,
      dialogFormRules,
      lastImageUrl,
      newImageUrl,
      fileType,
      action,
      headers,
      passwordRules,
      showDialog,
      dialogTitle,
      submitLoad,
      dialogWidth,
      dialogTop,
      passwordFormLabelWidth,

      // 计算属性
      queryId,
      userInfo,

      // 方法
      handleUpdate,
      handleClick,
      handlePushPost,
      handleAvatarSuccess,
      beforeAvatarUpload,
      uploadAvater,
      resetSelfForm,
      resetDialogForm,
      getdialogFormInfo,
      getSelfFormInfo,
      dialogCancel,
      dialogSubmit,
    };
  },
});
</script>

<template>
  <div class="about-container">
    <!-- 头像以及个人资料 -->
    <div class="about-header">
      <div class="about-header-avater">
        <!-- <img class="avater" :src="userInfo.avatar" alt="头像"> -->
        <el-upload
          class="avatar-uploader"
          :show-file-list="false"
          :action="action"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
          :http-request="uploadAvater"
        >
          <default-avatar
            v-if="userInfo.avatar === ''"
            width="125px"
            height="125px"
            :avatarName="userInfo.name.split('')[0]"
          ></default-avatar>
          <img
            v-else
            :src="userInfo.avatar"
            class="avatar"
            style="width: 125px; border-radius: 50%"
          />

          <el-icon
            class="el-icon-plus avatar-uploader-icon"
            style="font-size: 40px; color: #f9fbff"
            ><Plus
          /></el-icon>
        </el-upload>
      </div>
      <div class="about-header-description">
        <h2 class="about-name">{{ userInfo.name }}</h2>
        <div class="description-container">
          <div class="description-item">
            <strong class="count">{{ postTotal }}</strong>
            <div class="description-tag">发帖数</div>
          </div>
          <div class="description-item">
            <strong class="count">{{ userInfo.score }}</strong>
            <div class="description-tag">贡献值</div>
          </div>
          <div class="description-item">
            <strong class="count">{{ collectTotal }}</strong>
            <div class="description-tag">收藏数</div>
          </div>
        </div>
      </div>
      <!-- 修改个人资料 -->
      <el-button class="about-update" type="primary" plain @click="handleUpdate"
        >修改个人资料</el-button
      >
    </div>

    <!-- 展示帖子 收藏等 -->
    <div class="about-body">
      <!-- 帖子 -->
      <div class="about-body-left">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="我的帖子" name="mypost">
            <!-- 我的帖子 -->
            <div class="post-container" v-if="postList.length != 0">
              <div
                class="post-item"
                v-for="(postItem, index) in postList"
                :key="postItem.collectId"
              >
                <!-- 作者信息 -->
                <div class="post-author">
                  <!-- 头像 -->
                  <div class="post-avater" v-if="postItem.userAvatarUrl">
                    <img
                      class="avater"
                      :src="postItem.userAvatarUrl"
                      alt="作者"
                    />
                  </div>
                </div>
                <!-- 评论详细 -->
                <div class="post-detail">
                  <!-- 用户描述 -->
                  <div class="avater-description">
                    <!-- 姓名 -->
                    <div class="post-name">
                      <h5>{{ postItem.userName }}</h5>
                    </div>
                    <!-- 所属高校 -->
                    <div class="post-college">
                      {{ postItem.collegeName }}
                    </div>
                  </div>
                  <!-- 帖子信息 -->
                  <div class="post-info">
                    <span>{{ postItem.resourceName }}</span>
                  </div>

                  <div class="post-action">
                    <!-- 附件信息 -->
                    <div class="post-download" v-if="postItem.resourceUrl">
                      <a
                        class="download"
                        target="blank"
                        :href="postItem.resourceUrl"
                        download="下载"
                        ><span class="iconfont icon-fujian"></span>附件下载</a
                      >
                    </div>
                    <!-- 评论 -->
                    <div
                      class="add-post-item"
                      @click="handlePushPost(postItem.resourceId)"
                    >
                      <span class="iconfont icon-message"></span>
                      <span class="add-action">查看详情</span>
                    </div>
                  </div>

                  <!-- 回复 -->
                  <div class="post-send-name">
                    <span>发布于{{ postItem.createTime }}</span>
                  </div>
                </div>
              </div>
              <!-- <div class="collect-none" v-if="collectList === []">
                <h2>无</h2>
              </div> -->
            </div>
            <div class="nocomment-container" v-else style="height: 300px">
              <el-empty class="default" description="暂时还没有帖子哦~" />
              <!-- <h3 class="default">暂时还没有评论~</h3> -->
            </div>
          </el-tab-pane>
          <!-- <el-tab-pane label="收到评论" name="comment"> -->
          <el-tab-pane label="收到消息" name="comment">
            <!-- 我的评论 -->
            <!-- 评论分页 -->
            <div class="comment-container">
              <router-link
                class="comment-link"
                v-for="(commentItem, index) in userCommentList"
                :key="commentItem.id"
                :to="'/postDetail/' + commentItem.resourceId"
              >
                <div class="comment-item">
                  <!-- 评论详细 -->
                  <div class="comment-detail">
                    <!-- 用户描述 -->
                    <div class="avater-description">
                      <!-- 姓名 -->
                      <div class="comment-name">
                        <h5>{{ commentItem.belongName }}</h5>
                      </div>
                      <!-- 所属高校
                    <div class="comment-college">
                      {{ commentItem.belongCollege }}
                    </div> -->
                    </div>
                    <!-- 帖子信息 -->
                    <div class="comment-info">
                      <span>{{ commentItem.content }}</span>
                    </div>

                    <!-- 回复 -->
                    <div class="comment-send-name">
                      <span>发布于{{ commentItem.createTime }}</span>
                    </div>
                  </div>
                </div>
              </router-link>
              <!-- <div class="comment-none" v-if="userCommentList === []">
                <h2>无</h2>
              </div> -->
            </div>
          </el-tab-pane>
          <el-tab-pane label="我的收藏" name="collect">
            <!-- 我的收藏 -->
            <div class="collect-container">
              <div
                class="collect-item"
                v-for="(collectItem, index) in collectList"
                :key="collectItem.collectId"
              >
                <!-- 作者信息 -->
                <div class="collect-author">
                  <!-- 头像 -->
                  <div class="collect-avater" v-if="collectItem.userAvatarUrl">
                    <img
                      class="avater"
                      :src="collectItem.userAvatarUrl"
                      alt="作者"
                    />
                  </div>
                </div>
                <!-- 评论详细 -->
                <div class="collect-detail">
                  <!-- 用户描述 -->
                  <div class="avater-description">
                    <!-- 姓名 -->
                    <div class="collect-name">
                      <h5>{{ collectItem.userName }}</h5>
                    </div>
                    <!-- 所属高校 -->
                    <div class="collect-college">
                      {{ collectItem.collegeName }}
                    </div>
                  </div>
                  <!-- 帖子信息 -->
                  <div class="collect-info">
                    <span>{{ collectItem.resourceName }}</span>
                  </div>

                  <div class="collect-action">
                    <!-- 附件信息 -->
                    <div
                      class="collect-download"
                      v-if="collectItem.resourceUrl"
                    >
                      <a
                        class="download"
                        target="blank"
                        :href="collectItem.resourceUrl"
                        download="下载"
                        ><span class="iconfont icon-fujian"></span>附件下载</a
                      >
                    </div>
                    <!-- 评论 -->
                    <div
                      class="add-collect-item"
                      @click="handlePushPost(collectItem.resourceId)"
                    >
                      <span class="iconfont icon-message"></span>
                      <span class="add-action">回复</span>
                    </div>
                  </div>

                  <!-- 回复 -->
                  <div class="collect-send-name">
                    <span>收藏于{{ collectItem.createTime }}</span>
                  </div>
                </div>
              </div>
              <!-- <div class="collect-none" v-if="collectList === []">
                <h2>无</h2>
              </div> -->
            </div>
          </el-tab-pane>
          <el-tab-pane label="我的信息" name="userInfo">
            <!-- 我的信息 -->
            <el-form
              ref="selfFormRef"
              :model="selfForm"
              :rules="selfFormRules"
              label-width="80px"
              :disabled="isSelfForm"
            >
              <el-form-item label="姓名">
                <el-input v-model="selfForm.name"></el-input>
                <!-- <span class="form-item-action">修改</span> -->
              </el-form-item>
              <el-form-item label="账号">
                <el-input v-model="selfForm.account"></el-input>
                <!-- <span class="form-item-action">修改</span> -->
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="selfForm.email"></el-input>
                <!-- <span class="form-item-action">修改</span> -->
              </el-form-item>
              <!-- <el-form-item label="密码">
                <el-input v-model="selfForm.password"></el-input>
              </el-form-item> -->
              <!-- <el-form-item label="性别">
                <el-select v-model="selfForm.gender" placeholder="请选择">
                  <el-option label="女" value="0"></el-option>
                  <el-option label="男" value="1"></el-option>
                </el-select>
              </el-form-item> -->
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 收藏 -->
      <!-- <div class="about-body-right">
      </div> -->
    </div>

    <el-dialog
      class="dialog"
      :title="dialogTitle"
      :visible.sync="showDialog"
      :width="dialogWidth"
      :top="dialogTop"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :before-close="dialogCancel"
      append-to-body
      show-close
    >
      <el-form
        class="dialog-password-form"
        ref="difalogFormRef"
        :model="dialogForm"
        :rules="dialogFormRules"
      >
        <el-form-item label="密码" :label-width="passwordFormLabelWidth">
          <el-input
            type="password"
            v-model="dialogForm.password"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogCancel()">取 消</el-button>
        <el-button
          type="primary"
          @click="dialogSubmit(selfFormRef)"
          :disabled="submitLoad"
          >确 定</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<style lang="scss">
/*大型屏幕pc 超大屏*/
@media screen and (min-width: 1200px) {
  .about-container {
    width: 960px;
  }
}
/*1200>=pc>=992 大屏，字体红色，背景黑色*/
@media screen and (min-width: 992px) and (max-width: 1199px) {
  .about-container {
    width: 960px;
  }
}
/*768<=pad<992 中屏，字体黄色，背景红色*/
@media screen and (min-width: 768px) and (max-width: 991px) {
  .about-container {
    width: 768px;
  }
}
/*phone<768  小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 767px) and (min-width: 480px) {
  .about-container {
    width: 480px;
  }
}
/* 超小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 480px) {
  // 修改个人资料按钮
  .about-container .about-header .about-update {
    position: absolute;
    width: 100px !important;
    // 方案一
    height: 30px !important;
    padding: 4px 6px;
    // 方案二
    // min-height: 40px;

    right: 10px !important;
    bottom: 10px !important;
    span {
      font-size: 8px;
    }
  }

  .description-container {
    .description-item {
      .count {
        font-size: 8px;
      }

      .description-tag {
        font-size: 8px;
      }
    }
  }
}

.about-container {
  // width: 960px;
  // 方案一
  // height: 680px;

  // 方案二
  // min-height: 680px;
  // padding-bottom: 50px;

  // border: 1px solid red;
  border-radius: 10px;
  padding-top: 62px;
  margin: 0 auto;
  background-color: #e9ecef;
  overflow: hidden;

  // 头部——头像 描述
  .about-header {
    height: 250px;
    display: flex;
    // border: 1px solid red;
    border-radius: 10px;
    position: relative;
    // background-color: #fff;
    /* 不理想条纹 */
    background: linear-gradient(45deg, #fafafa 50%, #fff 0);
    background-size: 30px 30px;

    // background-color: blue;

    .about-header-avater {
      width: 250px;

      .avatar-uploader {
        .el-upload {
          border-radius: 50%;
          margin-top: 40px;
          margin-left: 40px;
          position: relative;

          .avater {
            z-index: 10;
          }

          .avatar-uploader-icon {
            // z-index: 1;
            opacity: 0;
            position: absolute;
            top: 40px;
            left: 45px;
          }
        }

        .el-upload:hover {
          // z-index: 99;
          opacity: 0.4;
          filter: alpha(opacity=60);

          .avatar-uploader-icon {
            opacity: 1;
          }
        }
      }
    }

    // 个人介绍
    .about-header-description {
      .about-name {
        margin: 85px 0 20px 20px;
      }

      .description-container {
        display: flex;

        .description-item {
          margin-left: 20px;

          .count {
            text-align: center;
            width: auto;
            margin: 0 auto;
            display: block;
          }

          .description-tag {
            color: #7f757d;
          }
        }
      }
    }

    // 修改个人资料按钮
    .about-update {
      position: absolute;
      width: 200px;
      // 方案一
      height: 40px;

      // 方案二
      // min-height: 40px;

      right: 20px;
      bottom: 20px;
    }
  }

  // 主体——介绍
  .about-body {
    margin-top: 12px;
    background-color: #fff;
    border-radius: 10px;

    .about-body-left {
      .el-tabs {
        .el-tabs__content {
          // 方案一
          height: 350px;
          overflow-y: auto;

          // 方案二
          // min-height: 350px;
          // overflow-y: auto;
        }

        // 我的帖子分页
        .post-container {
          margin-top: 20px;
          border-radius: 10px;
          background-color: #fff;

          .post-title {
            padding: 10px 30px;
          }

          .post-item {
            padding: 10px 30px;
            display: flex;
            position: relative;
            // border: 1px solid red;

            // 帖子作者
            .post-author {
              position: relative;
              display: flex;
              padding-left: 10px;

              // margin: 10px 0 0 0px;
              // border-bottom: 1px solid #909399;
              // padding-bottom: 10px;

              .post-avater {
                .avater {
                  width: 40px;
                }
              }
            }

            // 评论详细
            .post-detail {
              .avater-description {
                margin-left: 10px;
                padding-right: 10px;
                // border-right: 1px solid #909399;
                display: flex;

                // 姓名
                .post-name {
                  border-right: 1px solid #909399;
                  padding-right: 10px;
                  font-size: 14px;
                }

                // 高校
                .post-college {
                  padding-left: 5px;
                  font-size: 8px;
                  margin-top: 2px;
                }
              }

              // 评论信息
              .post-info {
                margin-top: 20px;
                margin-left: 20px;

                span {
                  font-size: 12px;
                }
              }

              .post-action {
                display: flex;

                // 附件下载
                .post-download {
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
                .add-post-item {
                  margin: 20px 0px 0px 10px;
                  font-size: 12px;
                  cursor: pointer;
                  color: #909399;

                  // .icon-message {}

                  .add-action {
                    font-size: 10px;
                  }
                }

                .add-post-item:hover {
                  color: #029dff;
                }
              }
            }

            .post-send-name {
              position: absolute;
              top: 10px;
              right: 30px;

              span {
                font-size: 12px;
              }
            }
          }

          // 奇数选择器
          .post-item:nth-child(odd) {
            background-color: #fafafa;
          }

          // 无收藏
          .post-none {
            text-align: center;
            margin-top: 150px;
            color: #7f757d;
          }
        }

        // 评论分页
        .comment-container {
          margin: 0 auto;

          .comment-link {
            .comment-item {
              position: relative;
              padding-bottom: 10px;
              margin-right: 50px;
              margin-left: 50px;
              // width: 100%;
              // text-align: start;
              // padding: 0px 0px 10px 50px;

              // 评论详细
              .comment-detail {
                // 用户描述
                .avater-description {
                  margin-bottom: 5px;

                  // .comment-name {
                  // }
                }

                // 帖子信息
                .comment-info {
                  font-size: 10px;
                  color: #7f757d;
                  padding-left: 10px;
                  padding-bottom: 5px;
                }

                // 回复
                .comment-send-name {
                  font-size: 10px;
                  color: #7f757d;
                  position: absolute;
                  right: 5px;
                  bottom: 10px;
                }
              }
            }

            .comment-item:hover {
              background-color: #fafafa;
            }
          }

          // 奇数选择器 斑马纹
          .comment-link:nth-child(odd) {
            .comment-item {
              background-color: #fafafa;
            }
          }

          // 无评论
          .comment-none {
            text-align: center;
            margin-top: 150px;
            color: #7f757d;
          }
        }

        // 收藏分页
        .collect-container {
          margin-top: 20px;
          border-radius: 10px;
          background-color: #fff;

          .collect-title {
            padding: 10px 30px;
          }

          .collect-item {
            padding: 10px 30px;
            display: flex;
            position: relative;
            // border: 1px solid red;

            // 帖子作者
            .collect-author {
              position: relative;
              display: flex;
              padding-left: 10px;

              // margin: 10px 0 0 0px;
              // border-bottom: 1px solid #909399;
              // padding-bottom: 10px;

              .collect-avater {
                .avater {
                  width: 40px;
                }
              }
            }

            // 评论详细
            .collect-detail {
              .avater-description {
                margin-left: 10px;
                padding-right: 10px;
                // border-right: 1px solid #909399;
                display: flex;

                // 姓名
                .collect-name {
                  border-right: 1px solid #909399;
                  padding-right: 10px;
                  font-size: 14px;
                }

                // 高校
                .collect-college {
                  padding-left: 5px;
                  font-size: 8px;
                  margin-top: 2px;
                }
              }

              // 评论信息
              .collect-info {
                margin-top: 20px;
                margin-left: 20px;

                span {
                  font-size: 12px;
                }
              }

              .collect-action {
                display: flex;

                // 附件下载
                .collect-download {
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
                .add-collect-item {
                  margin: 20px 0px 0px 10px;
                  font-size: 12px;
                  cursor: pointer;
                  color: #909399;

                  // .icon-message {}

                  .add-action {
                    font-size: 10px;
                  }
                }

                .add-collect-item:hover {
                  color: #029dff;
                }
              }
            }

            .collect-send-name {
              position: absolute;
              top: 10px;
              right: 30px;

              span {
                font-size: 12px;
              }
            }
          }

          // 奇数选择器
          .collect-item:nth-child(odd) {
            background-color: #fafafa;
          }

          // 无收藏
          .collect-none {
            text-align: center;
            margin-top: 150px;
            color: #7f757d;
          }
        }

        // 用户信息
        .el-form {
          .el-form-item {
            display: flex;
            justify-content: center;

            .el-form-item__content {
              display: flex;
              justify-content: center;
              margin-left: 0px !important;

              .el-input {
                height: 40px;
                margin-left: 20px;
                margin-right: 20px;

                .el-input__wrapper {
                  width: 200px;
                  .el-input__inner {
                    width: 200px;
                  }
                }
              }
            }

            .form-item-action {
              color: #7f757d;
              width: 80px;
              display: inline-block;
              text-align: center;
              cursor: pointer;
            }

            .form-item-action:hover {
              color: #029dff;
            }
          }
        }

        .el-tabs__header {
          width: 100%;
          text-align: center;
          // margin-left: 33%;
          // margin-right: 33%;
        }

        .el-tabs__nav-scroll {
          text-align: center;
          display: flex;
          justify-content: center;

          .el-tabs__nav {
            display: flex;
          }
        }
      }
    }

    // .about-body-right {
    // }
  }
}
</style>
