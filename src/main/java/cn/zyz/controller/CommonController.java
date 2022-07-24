package cn.zyz.controller;

import cn.zyz.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.UUID;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-21 15:26
 **/


@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    //文件上传路径
    @Value("${reggie.path}")
    private String basePath;


    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {  //参数名必须和前端传过来的图片的name相同
        //file是一个临时文件,需要转存到指定位置，否则本次请求完成后会删除

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //原始文件的后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，保证文件名不重复
        String filename = UUID.randomUUID().toString() + suffix;
        try {
            //进行文件上传，上传路径+文件名
            file.transferTo(new File(basePath + filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将文件名称返回
        return R.success(filename);
    }


    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        //输入流，通过输入流读取文件
        FileInputStream inputStream = null;
        //输出流，通过输出流将文件写回浏览器，在浏览器展示图片
        ServletOutputStream outputStream = null;
        //设置响应的格式为图片类型
        response.setContentType("image/jpeg");
        try {
            inputStream = new FileInputStream(basePath+name);
            //获取response输出流，将图片回写给浏览器
            outputStream = response.getOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭资源
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



