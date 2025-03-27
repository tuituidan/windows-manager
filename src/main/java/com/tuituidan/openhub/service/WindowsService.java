package com.tuituidan.openhub.service;

import com.alibaba.fastjson2.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
            String[] lines = result.split("\r\r\n");
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
        return list;
    }

    private JSONObject buildData(List<String> tmpList) {
        JSONObject data = new JSONObject();
        for (String str : tmpList) {
            String[] split = str.split("=");
            if (split.length == 2) {
                data.put(split[0], split[1]);
            }
        }
        return data;
    }

}
