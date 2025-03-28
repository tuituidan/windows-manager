package com.tuituidan.openhub.controller;

import com.alibaba.fastjson2.JSONObject;
import com.tuituidan.openhub.service.WindowsService;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * WindowsController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-02-08
 */
@RestController
@RequestMapping("/api/v1/win-service")
public class WindowsController {

    @Resource
    private WindowsService windowsService;

    /**
     * selectList
     *
     * @return List
     */
    @GetMapping("/all")
    public List<JSONObject> allList() {
        return windowsService.selectList(null);
    }

    /**
     * selectList
     *
     * @return List
     */
    @GetMapping
    public List<JSONObject> selectList() throws IOException {
        List<String> names = windowsService.getNames();
        if (CollectionUtils.isEmpty(names)) {
            return Collections.emptyList();
        }
        return windowsService.selectList(windowsService.getNames());
    }

    /**
     * add
     *
     * @param names names
     * @throws IOException ex
     */
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void add(@RequestBody List<String> names) throws IOException {
        windowsService.add(names);
    }

    /**
     * 修改状态
     *
     * @param name name
     * @param state 状态
     */
    @PatchMapping("/{name}/actions/{state:start|stop}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeState(@PathVariable String name, @PathVariable String state) {
        windowsService.changeState(new String(Base64Utils.decodeFromString(name)), state);
    }

}
