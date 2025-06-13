package com.tuituidan.openhub;

import com.tuituidan.openhub.util.AppCmdUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/**
 * WindowsManagerApplication.
 *
 * @author tuituidan
 */
@SpringBootApplication
public class WindowsManagerApplication {

    /**
     * 入口方法
     *
     * @param args args
     */
    public static void main(String[] args) throws IOException {
        Assert.isTrue(new File(AppCmdUtils.PREV_CMD).exists(), "请在Windows环境且安装II7以上版本下运行");
        String checkCmd = StreamUtils.copyToString(AppCmdUtils.execCmd("list site").getInputStream(),
                Charset.forName("GBK"));
        // 执行命令失败就不启动了，输入失败原因，一般是因为权限不足导致
        Assert.isTrue(!checkCmd.startsWith("ERROR"), checkCmd);
        SpringApplication.run(WindowsManagerApplication.class, args);
    }

}
