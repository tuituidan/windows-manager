package com.tuituidan.openhub.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * FileNameValidator.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-06-04
 */
@UtilityClass
public class FileNameValidator {

    private static final Map<String, Pattern> PATTERN_CACHE = new HashMap<>();

    /**
     * clearCache
     */
    public static void clearCache() {
        PATTERN_CACHE.clear();
    }

    /**
     * 校验文件名 String[] patterns = {"*.txt", "lic*", "*.js"};
     *
     * @param fileName fileName
     * @param patterns patterns
     * @return boolean
     */
    public static boolean validate(String fileName, String patterns) {
        if (StringUtils.isBlank(patterns)) {
            return false;
        }
        Pattern pattern;
        if (PATTERN_CACHE.containsKey(patterns)) {
            pattern = PATTERN_CACHE.get(patterns);
        } else {
            pattern = buildPattern(StringUtils.split(patterns, ","));
            PATTERN_CACHE.put(patterns, pattern);
        }
        return pattern.matcher(fileName.toLowerCase()).matches();
    }

    /**
     * 将通配符模式转换为正则表达式
     *
     * @param pattern pattern
     * @return String
     */
    private static String convertToRegex(String pattern) {
        return pattern
                .replace(".", "\\.")
                .replace("*", ".*")
                .replace("?", ".");
    }

    /**
     * 构建完整的正则表达式
     *
     * @param patterns patterns
     * @return Pattern
     */
    private static Pattern buildPattern(String[] patterns) {
        StringBuilder regex = new StringBuilder();
        for (int i = 0; i < patterns.length; i++) {
            regex.append("(").append(convertToRegex(patterns[i])).append(")");
            if (i < patterns.length - 1) {
                regex.append("|");
            }
        }
        return Pattern.compile(regex.toString());
    }

}

