package com.tuituidan.openhub.controller;

import com.alibaba.fastjson2.JSONObject;
import com.tuituidan.openhub.service.WindowsService;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * WindowsController.
 *
 * @author zhujunhan
 * @version 1.0
 * @date 2025-02-08
 */
@RestController
@RequestMapping("/api/v1/win-service")
public class WindowsController {

    @Resource
    private WindowsService windowsService;

    @GetMapping("/all")
    public List<JSONObject> allList() {
        return windowsService.selectList(null);
    }

    @GetMapping()
    public List<JSONObject> selectList() throws IOException {
        String path = System.getProperty("user.dir") + "/win-service.ini";
        return windowsService.selectList(FileUtils.readLines(new File(path), StandardCharsets.UTF_8));
    }

}
