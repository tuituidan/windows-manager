package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.bean.file.FileData;
import com.tuituidan.openhub.bean.file.FilePage;
import com.tuituidan.openhub.service.SettingService;
import com.tuituidan.openhub.util.FileNameValidator;
import com.tuituidan.openhub.util.ResponseUtils;
import com.tuituidan.openhub.util.StringExtUtils;
import com.tuituidan.openhub.util.ZipUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * FileController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2024/2/18
 */
@RestController
@RequestMapping("/api/v1")
public class FileController {

    @Resource
    private SettingService settingService;

    /**
     * fileUpload
     *
     * @param file file
     * @param uploadPath uploadPath
     * @param zip zip
     * @throws IOException Exception
     */
    @PostMapping("/file/actions/upload")
    public void fileUpload(MultipartFile file, String uploadPath, Boolean zip) throws IOException {
        if (BooleanUtils.isTrue(zip)) {
            ZipUtils.unzip(file.getInputStream(), uploadPath);
        } else {
            FileUtils.copyInputStreamToFile(file.getInputStream(),
                    new File(uploadPath + File.separator + file.getOriginalFilename()));
        }
    }

    /**
     * fileUnzip
     *
     * @param fileData fileData
     */
    @PostMapping("/file/actions/unzip")
    public void fileUnzip(@RequestBody FileData fileData) {
        ZipUtils.unzip(fileData.getPath());
    }

    /**
     * deleteFiles
     *
     * @param paths paths
     * @throws IOException IOException
     */
    @PostMapping("/file/delete")
    public void deleteFiles(@RequestBody String[] paths) throws IOException {
        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        }
    }

    /**
     * downloadFile
     *
     * @param path path
     * @throws IOException ex
     */
    @GetMapping("/file/action/preview")
    public void previewFile(String path) throws IOException {
        File file = new File(path);
        Assert.isTrue(file.exists(), "路径错误，文件不存在");
        Assert.isTrue(file.isFile(), "这不是一个文件");
        Assert.isTrue(file.length() > 0, "这是一个空文件");
        ResponseUtils.preview(file.getName(), new FileInputStream(path));
    }

    /**
     * downloadFile
     *
     * @param path path
     * @throws IOException ex
     */
    @GetMapping("/file/action/download")
    public void downloadFile(String path) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            Assert.isTrue(file.length() > 0, "这是一个空文件");
            ResponseUtils.download(file.getName(), new FileInputStream(path));
            return;
        }
        File[] files = file.listFiles();
        Assert.isTrue(files != null && files.length > 0, "这是一个空文件夹");
        String zipPath = new File(path).getParentFile().getPath() + File.separator + StringExtUtils.getUuid() + ".zip";
        ZipUtils.zip(zipPath, Arrays.stream(files).map(File::getPath).collect(Collectors.toList()));
        ResponseUtils.download(FilenameUtils.getName(path) + ".zip", new FileInputStream(zipPath));
        FileUtils.forceDelete(new File(zipPath));
    }

    /**
     * showFileContent
     *
     * @param path path
     * @throws IOException IOException
     */
    @GetMapping("/file/action/show")
    public String showFileContent(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }

    /**
     * showFileContent
     *
     * @param position position
     * @param size size
     * @param path path
     */
    @GetMapping("/file/{position}/{size}/action/page")
    public FilePage showFileContent(@PathVariable String position, @PathVariable Integer size, String path) {
        try (RandomAccessFile raf = new RandomAccessFile(path, "r")) {
            raf.seek(Long.parseLong(position));
            int index = 0;
            StringBuilder sb = new StringBuilder();
            for (; index < size; index++) {
                String line = raf.readLine();
                if (line == null) {
                    break;
                }
                sb.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)).append("\n");
            }
            return new FilePage()
                    .setData(sb.toString())
                    .setPosition(String.valueOf(raf.getFilePointer()))
                    .setHasMore(index == size);
        } catch (Exception ex) {
            throw new IllegalArgumentException("读取错误", ex);
        }
    }

    /**
     * loadFileData
     *
     * @param path path
     * @return List
     */
    @GetMapping("/site/files")
    public List<FileData> loadFileData(String rootPath, String path, Boolean showHiddenFile) {
        File[] files = new File(StringUtils.isNotBlank(path) ? path : rootPath).listFiles();
        if (files == null) {
            return Collections.emptyList();
        }
        Properties properties = settingService.getSetting();
        List<Pair<String, String>> fileExtList = new ArrayList<>();
        for (Entry<Object, Object> entry : properties.entrySet()) {
            String key = entry.getKey().toString();
            if (StringUtils.startsWith(key, "file-ext")) {
                fileExtList.add(Pair.of(entry.getValue().toString(), StringUtils.remove(key, "file-ext-")));
            }
        }
        return Arrays.stream(files).filter(it -> (BooleanUtils.isTrue(showHiddenFile)
                        || !it.isHidden())).map(file -> new FileData()
                        .setLabel(file.getName())
                        .setPath(file.getPath())
                        .setType(getFileType(file, fileExtList))
                        .setFileSize(FileUtils.byteCountToDisplaySize(file.length()))
                        .setLastModifyTime(LocalDateTime
                                .ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                        .setLeaf(file.isFile()))
                .sorted(Comparator.comparing(FileData::getLeaf)
                        .thenComparing(FileData::getLabel, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private String getFileType(File file, List<Pair<String, String>> fileExtList) {
        if (file.isDirectory()) {
            return "folder";
        }
        if (StringUtils.endsWithIgnoreCase(file.getName(), ".zip")) {
            return "zip";
        }
        for (Pair<String, String> pair : fileExtList) {
            if (FileNameValidator.validate(file.getName(), pair.getKey())) {
                return pair.getValue();
            }
        }
        return "file";
    }

}
