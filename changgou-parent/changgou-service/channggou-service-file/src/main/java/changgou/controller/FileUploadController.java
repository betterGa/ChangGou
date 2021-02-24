package changgou.controller;

import changgou.file.FastDFSFile;
import changgou.util.FastDFSUtil;
import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import org.csource.common.MyException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController("/upload")
//@RequestMapping
@CrossOrigin
public class FileUploadController {

    @PostMapping
    /* 文件上传 */
    public Result upload(@RequestParam(value = "file") MultipartFile file) throws IOException, MyException {
        FastDFSFile file1 = new FastDFSFile(
                // 文件名
                file.getName(),
                // 文件字节数组
                file.getBytes(),
                // 文件扩展名
                StringUtils.getFilenameExtension(file.getOriginalFilename())
        );

        // 调用文件上传工具类 将文件传入到 FastDFS 中
         String[] filename=FastDFSUtil.upload(file1);

        // 拼接访问地址
        String url="http://192.168.211.132:8080/"+filename[0]+"/"+filename[1];
        return new Result(true, StatusCode.OK, "上传成功",url);
    }
}
