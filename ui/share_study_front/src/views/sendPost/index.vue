<script lang="ts">
// 这是一个基于 TypeScript 的 Vue 组件
import { defineComponent, onMounted, onBeforeUnmount, ref, inject } from "vue";
import { useRouter, useRoute } from "vue-router";
import $bus from "@/libs/eventBus";
import { PostListMember } from "@/views/home/index.vue";
import { UserInfoMember } from "@/store";
import { getTokenData } from "@/utils/index";
import { getToken } from "@/utils/auth";
import { WangEditorFormMember } from "@/components/WangEditor/index.vue";

import { ElMessageBox } from "element-plus";
import type {
  FormInstance,
  FormRules,
  UploadProps,
  UploadUserFile,
} from "element-plus";
import { delOssFile, ossFileUpload } from "@/api/oss";
import { addPost, getPostInfo, updatePost as updatePostInfo } from "@/api/post";
import Loading from "@/components/Loading/index.vue";
import WangEditor from "@/components/WangEditor/index.vue";

export interface FormMember {
  name: string;
  info: string;
  belong: string;
  url: string;
}

export interface UpdateFormMember {
  name: string;
  info: string;
  id: string;
  url: string;
}

export default defineComponent({
  components: {
    Loading,
    WangEditor,
  },
  setup(props, context) {
    // 在这里声明数据，或者编写函数并在这里执行它
    // 在使用 setup 的情况下，请牢记一点：不能再用 this 来获取 Vue 实例
    const $message: any = inject("$message");
    const router = useRouter();
    const route = useRoute();

    const postId = ref<string>("");
    const postUrl = ref<string>("");
    const propTitle = ref<string>("");
    const propInfo = ref<string>("");
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
    const form = ref<FormMember>({
      name: "",
      info: "",
      belong: "",
      url: "",
    });

    const formRef = ref<FormInstance>();

    const formRules = ref<FormRules>({
      name: [{ required: true, trigger: "blur" }],
      info: [{ required: true, trigger: "blur" }],
      belong: [{ required: true, trigger: "blur" }],
      url: [{ required: false, trigger: "blur" }],
    });
    const updateForm = ref<UpdateFormMember>({
      name: "",
      info: "",
      id: "",
      url: "",
    });
    const submitLoading = ref<boolean>(false);
    const action = ref<string>("/api/file/oss_file_upload");
    // const fileList = ref<UploadUserFile[]>(); // 上传文件列表
    const fileList = ref<File[]>([]); // 上传文件列表
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
    const fileLimit = ref<number>(1);
    const headers = ref<object>({ "Content-Type": "multipart/form-data" });
    const fileUrl = ref<string>("");

    const resetFormFromHeaderEvent = (val: any) => {
      resetAddPost(formRef.value);
    };

    const tranHtmlFromEditorEvent = (WangEditorForm) => {
      console.log("info=>", WangEditorForm.html);
      console.log("title=>", WangEditorForm.postTitle);

      form.value.name = WangEditorForm.postTitle;
      form.value.info = WangEditorForm.html;
      // 发布接口
      onSubmit(formRef.value);
      // this.$refs.test.innerHTML += info;
      // console.log(this.$refs.test);
    };

    const updateHtmlFromEditorEvent = (WangEditorForm) => {
      console.log("info=>", WangEditorForm.html);
      console.log("title=>", WangEditorForm.postTitle);

      updateForm.value.name = WangEditorForm.postTitle;
      updateForm.value.info = WangEditorForm.html;
      // 发布接口
      updateSubmit(formRef.value);
      // this.$refs.test.innerHTML += info;
      // console.log(this.$refs.test);
    };

    // 生命周期钩子
    onMounted(() => {
      const token = getTokenData();
      if (token != null || token != undefined) {
        userInfo.value = token;
      }
      getParams(); // 获取路由中的参数
      updatePost(postId.value);
      noToken(); // 未登录

      $bus.on("resetFormFromHeader", resetFormFromHeaderEvent);
      // 提交 发布
      $bus.on("tranHtmlFromEditor", tranHtmlFromEditorEvent);
      // 修改
      $bus.on("updateHtmlFromEditor", updateHtmlFromEditorEvent);
    });

    onBeforeUnmount(() => {
      $bus.off("resetFormFromHeader", resetFormFromHeaderEvent);
      // 提交 发布
      $bus.off("tranHtmlFromEditor", tranHtmlFromEditorEvent);
      // 修改
      $bus.off("updateHtmlFromEditor", updateHtmlFromEditorEvent);
    });

    // 方法 methods
    // 方法 methods
    const noToken = () => {
      // console.log("getTokenData=>", this.getTokenData());
      const token = getTokenData();

      if (token === null || token === undefined) {
        // router.push(`/login`);
        router.push(`/intro`);
      }
    };
    // 获取路由中的参数
    const getParams = () => {
      postId.value = route.params.id as string;
      // console.log(this.postId);
    };
    // 更新当前信息
    const updatePost = async (id: string) => {
      if (id === "1") {
        // 发帖
        return id;
      } else {
        // 修改
        const { data } = await getPostInfo(id);
        // this.postDetail = data;

        // 转换
        form.value.name = data.name;
        form.value.info = data.info;
        form.value.belong = data.id;
        form.value.url = data.url;

        postUrl.value = data.url;

        propTitle.value = data.name;
        propInfo.value = data.info;
        // console.log("data=>", data);
        // this.$bus.$emit("updateHtmlFromSendPost", data.name, data.info);

        // updateForm
        updateForm.value.id = data.id;
      }
    };
    // 发布帖子
    const onSubmit = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      formEl.validate((valid) => {
        if (valid) {
          submitLoading.value = true;
          $message.info("正在发布中，请稍后");
          // 上传接口
          // 判断是否存在需要上传文件
          if (fileList.value != undefined && fileList.value[0] != undefined) {
            let FormDatas = new window.FormData();
            FormDatas.append("file", fileList.value[0]);
            // console.log("FormDatas=>", FormDatas);

            // this.$axios({
            //   // url: `http://localhost:8080/api/file/oss_file_upload`,
            //   url: `/api/file/oss_file_upload`,
            //   method: "post",
            //   data: FormDatas,
            //   timeout: 3 * 60 * 1000, // 上传设置180秒
            //   headers: {
            //     "Conten-Type": "multipart/form-data",
            //     // 'Content-Type': 'application/x-www-form-urlencoded'
            //   },
            // })
            ossFileUpload(FormDatas)
              .then((res) => {
                const { data } = res;
                // console.log(data);
                $message.success("附件上传成功");
                fileUrl.value = data;
                form.value.url = data; // form的url
                submitLoading.value = false; //

                // 调用发布接口
                form.value.belong = userInfo.value.id; // 配置id

                addPost(form.value)
                  .then((res) => {
                    $message.success("发表成功，审核后正式发布，正在跳转首页");
                    submitLoading.value = false;
                    resetAddPost(formRef.value);
                    router.push({ path: "/" });
                  })
                  .catch((error) => {
                    resetAddPost(formRef.value);
                    $message.info("帖子发布失败，请重试");
                    // 上传成功，但发布失败情况
                    if (fileUrl.value != "") {
                      // 删除oss文件
                      delOssFile(fileUrl.value);
                      fileList.value = [];
                      fileUrl.value = "";
                    }

                    submitLoading.value = false;
                  });
              })
              .catch((error) => {
                // console.log(error);
                $message.info("附件上传失败");
                submitLoading.value = false; //
              });
          } else {
            // 无文件
            // 调用发布接口
            form.value.belong = userInfo.value.id; // 配置id

            addPost(form.value)
              .then((res) => {
                $message.success("发布成功,正在跳转首页");
                submitLoading.value = false;
                resetAddPost(formRef.value);
                router.push({ path: "/" });
              })
              .catch((error) => {
                resetAddPost(formRef.value);
                $message.info("帖子发布失败，请重试");
                // 上传成功，但发布失败情况
                if (fileUrl.value) {
                  // 删除oss文件
                  delOssFile(fileUrl.value);
                }

                submitLoading.value = false;
              });
          }
        }
      });
    };
    // 修改帖子
    const updateSubmit = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      // let updateInfo
      formEl.validate((valid) => {
        if (valid) {
          submitLoading.value = true;
          $message.info("正在修改中，请稍后");
          // 上传接口
          // 如果上传文件
          if (fileList.value != undefined && fileList.value[0] != undefined) {
            let FormDatas = new window.FormData();
            FormDatas.append("file", fileList.value[0]);
            // console.log("FormDatas=>", FormDatas);

            //
            // this.$axios({
            //   url: `/ossapi/file/oss_file_upload`,
            //   method: "post",
            //   data: FormDatas,
            //   timeout: 3 * 60 * 1000, // 上传设置180秒
            //   headers: {
            //     "Conten-Type": "multipart/form-data",
            //     // 'Content-Type': 'application/x-www-form-urlencoded'
            //   },
            // })
            ossFileUpload(FormDatas)
              .then((res) => {
                const { data } = res;
                // console.log(data);
                $message.success("附件上传成功");
                fileUrl.value = data;
                submitLoading.value = false; //

                updateForm.value.url = data; // updateForm的url
                // updateForm.value.name = form.value.name; // 标题
                // updateForm.value.info = form.value.info; // 描述
                // 调用修改接口
                updateForm.value.id = postId.value; // 配置id

                updatePostInfo(updateForm.value)
                  .then((res) => {
                    $message.success("修改成功,正在跳转中");
                    submitLoading.value = false;
                    resetAddPost(formRef.value);
                    router.push({ path: "/" });
                  })
                  .catch((error) => {
                    resetAddPost(formRef.value);
                    $message.info("帖子发布失败，请重试");
                    // 上传成功，但发布失败情况
                    if ((fileUrl.value = "")) {
                      // 删除oss文件
                      delOssFile(fileUrl.value);
                      fileList.value = [];
                      fileUrl.value = "";
                    }

                    submitLoading.value = false;
                  });
              })
              .catch((error) => {
                // console.log(error);
                $message.info("附件上传失败");
                submitLoading.value = false; //
              });
          } else {
            // 如果不上传文件
            // 调用发布接口
            updateForm.value.id = postId.value; // 配置id
            // updateForm.value.name = form.value.name; // 标题
            // updateForm.value.info = form.value.info; // 描述

            updatePostInfo(updateForm.value)
              .then((res) => {
                $message.success("修改成功,正在跳转");
                submitLoading.value = false;
                resetAddPost(formRef.value);
                router.push({ path: "/" });
              })
              .catch((error) => {
                resetAddPost(formRef.value);
                $message.info("帖子发布失败，请重试");
                // 上传成功，但发布失败情况
                if (fileUrl.value != "") {
                  // 删除oss文件
                  delOssFile(fileUrl.value);
                }

                submitLoading.value = false;
              });
          }
        }
      });
    };
    // 登录表单重置
    const resetAddPost = (formEl: FormInstance | undefined) => {
      if (!formEl) return;
      form.value = {
        name: "",
        info: "",
        belong: "",
        url: "",
      };
      formEl.resetFields();
    };
    const handleCancel = () => {
      console.log("取消");
      if (fileUrl.value) {
        // 删除oss连接接口
        delOssFile(fileUrl.value);
      }
      // 跳转到首页
      router.push({ path: "/" });
    };
    // 上传图片相关
    const handleRemove: UploadProps["onRemove"] = (file, fileList) => {
      console.log(file, fileList);
    };
    const handlePreview = (file: any) => {
      console.log(file);
    };
    const handleExceed: UploadProps["onExceed"] = (files, fileList) => {
      $message.warning(
        `当前限制选择 ${fileLimit.value} 个文件，本次选择了 ${
          files.length
        } 个文件，共选择了 ${files.length + fileList.length} 个文件`
      );
    };
    const beforeRemove: UploadProps["beforeRemove"] = (file, fileList) => {
      // console.log(fileList);
      return ElMessageBox.confirm(`确定移除 ${file.name}？`, "Warning", {
        confirmButtonText: "OK",
        cancelButtonText: "Cancel",
        type: "warning",
      })
        .then(() => {
          $message({
            type: "success",
            message: "删除成功",
          });

          return true;
        })
        .catch(() => {
          $message({
            type: "info",
            message: "删除失败",
          });
          return false;
        });
    };
    // 上传之前的回调
    const beforeUpload: UploadProps["beforeUpload"] = (file) => {
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
    //上传文件的事件
    const uploadFile: UploadProps["httpRequest"] = (item) => {
      $message.info("文件已上传至浏览器，点击发布上传至服务器");
      //上传文件的需要formdata类型;所以要转
      // console.log(item.file);
      console.log(item);
      fileList.value.push(item.file);
      console.log(fileList.value);
      return new Promise((resolve) => resolve(true));
      // let FormDatas = new window.FormData();
      // FormDatas.append("file", item.file);
      // console.log("FormDatas=>", FormDatas);

      // console.log("FormDatas=>", FormDatas);
      // console.log(data);

      // 调用接口
      // ossFileUpload(FormDatas).then((res) => {
      //   const data = res.data;
      //   this.fileUrl = data;
      // });
    };

    // 计算方法 computed

    // 监听 watch

    return {
      // 需要给 `<template />` 用的数据或函数，在这里 `return` 出去
      postId,
      postDetail,
      userInfo,
      form,
      formRef,
      formRules,
      updateForm,
      submitLoading,
      action,
      fileList,
      fileType,
      fileLimit,
      headers,
      fileUrl,
      propTitle,
      propInfo,

      // 方法
      onSubmit,
      updateSubmit,
      handleCancel,
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
  <div class="add-post-container">
    <Loading v-show="submitLoading"></Loading>
    <el-upload
      class="upload-file"
      :action="action"
      :http-request="uploadFile"
      :on-preview="handlePreview"
      :on-remove="handleRemove"
      :before-remove="beforeRemove"
      :before-upload="beforeUpload"
      multiple
      :limit="fileLimit"
      :on-exceed="handleExceed"
      :file-list="fileList"
    >
      <el-button size="small" type="primary"
        ><span class="iconfont icon-fujian"></span> 附件上传</el-button
      >
      <div slot="tip" class="el-upload__tip"></div>
    </el-upload>
    <el-form
      class="form"
      ref="formRef"
      :model="form"
      :rules="formRules"
      label-width="40px"
    >
      <!-- WangEditor -->
      <wang-editor
        v-if="propTitle !== '' || postId === '1'"
        :postId="postId"
        :title="propTitle"
        :info="propInfo"
        class="wang-editor"
      ></wang-editor>

      <!--
        <el-upload
          class="upload-demo"
          action  //必要属性，上传文件的地址，可以不给，但必须要有，不给就i调接口上传
          :http-request="uploadFile"//这个是就上传文件的方法，把上传的接口写在这个方法里
          ref="upload"
          :limit="fileLimit"//上传文件个数的限制
          :on-remove="handleRemove"//上传之后，移除的事件
          :file-list="fileList"//上传了那些文件的列表
          :on-exceed="handleExceed"//超出上传文件个数的错误回调
          :before-upload="beforeUpload"//文件通过接口上传之前，一般用来判断规则，
          //比如文件大小，文件类型
          :show-file-list="false"//是否用默认文件列表显示
          :headers="headers"//上传文件的请求头
          >
       -->
      <!-- <div class="test" ref="test">测试<br /></div> -->
    </el-form>
  </div>
</template>

<style lang="scss">
/*大型屏幕pc 超大屏*/
@media screen and (min-width: 1200px) {
  .form {
    .upload-file {
      width: 600px;
    }
  }
}
/*1200>=pc>=992 大屏，字体红色，背景黑色*/
@media screen and (min-width: 992px) and (max-width: 1199px) {
  .form {
    .upload-file {
      width: 600px;
    }
  }
}
/*768<=pad<992 中屏，字体黄色，背景红色*/
@media screen and (min-width: 768px) and (max-width: 991px) {
  .form {
    .upload-file {
      width: 600px;
    }
  }
}
/*phone<768  小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 767px) and (min-width: 480px) {
  .form {
    .upload-file {
      width: 480px;
    }
  }
}
/* 超小屏，字体黑色，背景蓝色*/
@media screen and (max-width: 480px) {
}

.add-post-container {
  padding-top: 62px;
  text-align: center;
  margin: 0 auto;

  h2 {
    margin: 20px 0;
  }

  .form {
    .upload-file {
      // width: 600px;
      margin: 0 auto;
      margin-bottom: 20px;
    }
  }

  .upload-file {
    .el-upload {
      width: 100%;
      button {
        height: 50px;
        width: 100%;
        padding: 8px 12px;
      }
    }

    .el-upload-list {
      margin-top: 0;
    }

    .el-upload__tip {
      margin-top: 0;
    }
  }

  .wang-editor {
    text-align: start;
  }
}
</style>
