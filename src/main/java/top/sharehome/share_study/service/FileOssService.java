package top.sharehome.share_study.service;

import org.springframework.web.multipart.MultipartFile;


/**
 * 文件Service
 *
 * @author AntonyCheng
 */
public interface FileOssService {
    /**
     * 文件上传
     *
     * @param file     用户待上传的文件
     * @param rootPath 上传的根路径
     * @return 文件存储的url
     */
    String upload(MultipartFile file, String rootPath);

    /**
     * 文件删除
     *
     * @param url 文件所在地址
     */
    void delete(String url);
}
