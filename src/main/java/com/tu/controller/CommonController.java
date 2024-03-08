package com.tu.controller;

import com.tu.common.R;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.UUID;

/**
 * @author JiFeiYe
 * @since 2024/3/6
 */
@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * 上传图片
     *
     * @param file 文件
     * @return R
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        // file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会被删除
        log.info("获取文件：{}", file.toString());
        // 获取原有文件名
        String originalFilename = file.getOriginalFilename();
        // 获取后缀
        String suffix = null;
        if (originalFilename != null) {
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 拼出新文件名（UUID），防止文件名重复造成覆盖
        String filename = UUID.randomUUID().toString() + suffix;
        // 创建一个目录对象
        File dir = new File(basePath);
        // 若文件夹不存在则创建文件夹
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            log.info("图片转存：{}", (basePath + filename));
            // 转存文件到指定位置
            file.transferTo(new File(basePath + filename));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return R.success(filename);
    }

    /**
     * 将图片写入浏览器
     *
     * @param response
     * @param name
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response, String name) {
        log.info("图片写入浏览器：{}", name);
        try {
            // 文件输入流（从服务器读取）
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
            // 文件输出流（写入浏览器）
            ServletOutputStream outputStream = response.getOutputStream();
            // 管道（连接上述两者）
            byte[] bytes = new byte[1024];
            int len = 0;
            // 持续地动态地流动（while）
            while ((len = fileInputStream.read(bytes)) != -1) {
                // 传输
                outputStream.write(bytes, 0, len);
                // 刷新输出流
                outputStream.flush();
            }
            outputStream.close();
            fileInputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
