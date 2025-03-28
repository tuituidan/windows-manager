package com.tuituidan.openhub.service;

import com.alibaba.fastjson2.JSONObject;
import com.tuituidan.openhub.config.AppPropertiesConfig;
import com.tuituidan.openhub.util.StringExtUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * WindowsService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-02-08
 */
@Service
@Slf4j
public class WindowsService {

    @Resource
    private AppPropertiesConfig appPropertiesConfig;

    /**
     * getNames
     *
     * @return List
     */
    public List<String> getNames() {
        try {
            File file = getConfigFile();
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
        String[] lines = getWinService(names);
        List<String> tmpList = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isNotBlank(line)) {
                tmpList.add(line);
                continue;
            }
            if (CollectionUtils.isNotEmpty(tmpList)) {
                list.add(buildData(tmpList));
                tmpList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(tmpList)) {
            list.add(buildData(tmpList));
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

    /**
     * add
     *
     * @param names names
     * @throws IOException ex
     */
    public void add(List<String> names) throws IOException {
        FileUtils.writeLines(getConfigFile(), names);
    }

    /**
     * 修改状态
     *
     * @param name name
     * @param state 状态
     */
    public void changeState(String name, String state) {
        String cmd = StringExtUtils.format(appPropertiesConfig.getStartWinService(), state, name);
        try {
            Process process = new ProcessBuilder("cmd", "/c", cmd)
                    .redirectErrorStream(true).start();
            process.waitFor();
        } catch (InterruptedException | IOException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalArgumentException("操作异常", ex);
        }
    }

    private String[] getWinService(List<String> names) {
        String where = "";
        if (CollectionUtils.isNotEmpty(names)) {
            where = StringExtUtils.format(appPropertiesConfig.getListWinServiceWhere(),
                    names.stream().map(name -> "Name='" + name + "'")
                            .collect(Collectors.joining(" OR ")));
        }
        String cmd = StringExtUtils.format(appPropertiesConfig.getListWinService(), where);
        try {
            Process process = new ProcessBuilder("cmd", "/c", cmd)
                    .redirectErrorStream(true).start();
            String result = IOUtils.toString(process.getInputStream(), "GBK");
            // 有些描述自带换行符，先把两个替换成一个换行，再以换行分割
            return result.replace("\r\r\n\r\r\n", "\r\r\n").split("\r\r\n");
        } catch (Exception ex) {
            log.error("", ex);
        }
        return ArrayUtils.EMPTY_STRING_ARRAY;
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

    private File getConfigFile() {
        return new File(System.getProperty("user.dir") + "/win-service.ini");
    }

}
