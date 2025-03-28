package com.tuituidan.openhub.service;

import com.alibaba.fastjson2.JSONObject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * WindowsService.
 *
 * @author zhujunhan
 * @version 1.0
 * @date 2025-02-08
 */
@Service
@Slf4j
public class WindowsService {

    /**
     * getNames
     *
     * @return List
     */
    public List<String> getNames() {
        try {
            File file = new File(System.getProperty("user.dir") + "/win-service.ini");
            if (file.exists()) {
                return FileUtils.readLines(file, StandardCharsets.UTF_8);
            }
        } catch (IOException ex) {
            log.error("", ex);
        }
        return Collections.emptyList();
    }

    /**
     * selectList
     *
     * @param names names
     * @return List
     */
    public List<JSONObject> selectList(List<String> names) {
        List<JSONObject> list = new ArrayList<>();
        String where = "";
        if (CollectionUtils.isNotEmpty(names)) {
            where = "where \"" + names.stream().map(name -> "Name='" + name + "'")
                    .collect(Collectors.joining(" OR ")) + "\"";
        }
        String cols = "Name,DisplayName,Description,PathName,StartMode,State,StartName";
        String cmd = "wmic service " + where + " get " + cols + " /format:list";
        try {
            Process process = new ProcessBuilder("cmd", "/c", cmd)
                    .redirectErrorStream(true).start();
            String result = IOUtils.toString(process.getInputStream(), "GBK");
            // 有些描述自带换行符，先把两个替换成一个换行，再以换行分割
            String[] lines = result.replace("\r\r\n\r\r\n", "\r\r\n").split("\r\r\n");
            List<String> tmpList = new ArrayList<>();
            for (String line : lines) {
                if (StringUtils.isNotBlank(line)) {
                    tmpList.add(line);
                    continue;
                }
                if (CollectionUtils.isEmpty(tmpList)) {
                    continue;
                }
                list.add(buildData(tmpList));
                tmpList.clear();
            }
            if (CollectionUtils.isNotEmpty(tmpList)) {
                list.add(buildData(tmpList));
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
        if (CollectionUtils.isEmpty(names)) {
            // 表示查所有，就是要新增服务，所以要过滤掉已存在的
            List<String> strs = getNames();
            if (CollectionUtils.isEmpty(strs)) {
                return list;
            }
            for (JSONObject item : list) {
                if (strs.contains(item.getString("Name"))) {
                    item.put("selected", true);
                }
            }
        }
        return list;
    }

    private JSONObject buildData(List<String> tmpList) {
        JSONObject data = new JSONObject();
        for (String str : tmpList) {
            String[] split = str.split("=");
            if (split.length == 2) {
                String value = split[1];
                if (StringUtils.startsWith(value, "\"")) {
                    value = StringUtils.strip(value, "\"");
                }
                data.put(split[0], value);
            }
        }
        return data;
    }

    /**
     * add
     *
     * @param names names
     * @throws IOException ex
     */
    public void add(List<String> names) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/win-service.ini");
        FileUtils.writeLines(file, names);
    }

    /**
     * 修改状态
     *
     * @param name name
     * @param state 状态
     */
    public void changeState(String name, String state) {
        try {
            Process process = new ProcessBuilder("cmd", "/c", "net " + state + " \"" + name + "\"")
                    .redirectErrorStream(true).start();
            process.waitFor();
        } catch (Exception ex) {
            throw new IllegalArgumentException("操作异常", ex);
        }
    }

}
