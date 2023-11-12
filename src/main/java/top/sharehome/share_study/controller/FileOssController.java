package top.sharehome.share_study.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.service.FileOssService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * OSS功能相关接口
 *
 * @author AntonyCheng
 */
@RestController
@Api(tags = "OSS文件上传/下载功能相关接口")
@RequestMapping("/file")
@CrossOrigin
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FileOssController {
    @Resource
    private FileOssService fileOssService;
    /**
     * 头像可以通过的文件格式
     */
    private static final List<String> AVATAR_FORMATS = new ArrayList<>(Arrays.asList("png", "jpg", "jpeg"));
    /**
     * 其他文件可以通过的文件格式
     */
    private static final List<String> FILE_FORMATS = new ArrayList<>(Arrays.asList(
            "png", "jpg", "jpeg", "gif", "pdf",
            "xlsx", "xls", "doc", "docx",
            "ppt", "pptx", "mp3", "mp4", "mpeg",
            "zip", "rar", "7z",
            "py", "java", "c", "cpp", "go", "html", "js", "ts", "sql", "css"));

    /**
     * 头像文件上传（无需权限）
     *
     * @param file 用户头像上传的文件
     * @return 返回上传信息
     */
    @ApiOperation("头像文件上传")
    @PostMapping("/oss_avatar_upload")
    public R<String> avatarUpload(MultipartFile file) {

        if (file == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        String[] split = originalFilename.split("\\.");
        String suffix = split[split.length - 1];
        if (!AVATAR_FORMATS.contains(suffix.toLowerCase())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_FILE_TYPE_MISMATCH));
        }
        long size = file.getSize();
        if (size / 1024 >= 500) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_IMAGE_IS_TOO_LARGE));
        }
        String url = fileOssService.upload(file, "avatar");
        return R.success(url, "上传文件成功");
    }

    /**
     * 其他文件上传（s/a/u）
     *
     * @param file 用户上传的文件
     * @return 返回上传信息
     */
    @ApiOperation("其他文件上传")
    @PostMapping("/oss_file_upload")
    public R<String> fileUpload(MultipartFile file) {
        if (file == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        String[] split = originalFilename.split("\\.");
        String suffix = split[split.length - 1].toLowerCase();
        if (!FILE_FORMATS.contains(suffix)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_FILE_TYPE_MISMATCH));
        }
        long size = file.getSize();
        if (Arrays.asList("py", "java", "c", "cpp", "go", "html", "js", "ts", "sql", "css").contains(suffix)) {
            if (size / 1024 >= 100) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_CODE_IS_TOO_LARGE));
            }
        }
        if (Arrays.asList("png", "jpg", "jpeg", "gif").contains(suffix)) {
            if (size / 1024 / 1024 >= 5) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_IMAGE_IS_TOO_LARGE));
            }
        }
        if (Arrays.asList("pdf", "xlsx", "xls", "doc", "docx", "ppt", "pptx").contains(suffix)) {
            if (size / 1024 / 1024 >= 20) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_FILE_IS_TOO_LARGE));
            }
        }
        if (Arrays.asList("mp3", "mp4").contains(suffix)) {
            if (size / 1024 / 1024 >= 100) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_VIDEO_IS_TOO_LARGE));
            }
        }
        if (Arrays.asList("rar", "7z", "zip").contains(suffix)) {
            if (size / 1024 / 1024 >= 100) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.USER_UPLOADED_VIDEO_IS_TOO_LARGE));
            }
        }
        String url = fileOssService.upload(file, "file/" + suffix);
        return R.success(url, "上传文件成功");
    }

    /**
     * OSS文件删除（无需权限）
     *
     * @param ossUrl oss链接
     * @return 返回删除结果
     */
    @ApiOperation("OSS文件删除")
    @DeleteMapping("/oss_file_delete")
    public R<String> fileDelete(String ossUrl) {
        if (ossUrl == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.REQUEST_REQUIRED_PARAMETER_IS_EMPTY));
        }
        if (!ossUrl.contains("myqcloud.com")) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
        }
        fileOssService.delete(ossUrl);
        return R.success("OSS文件删除成功");
    }
}
