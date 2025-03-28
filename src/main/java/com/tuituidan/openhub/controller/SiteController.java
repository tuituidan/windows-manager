package com.tuituidan.openhub.controller;

import com.tuituidan.openhub.bean.site.Site;
import com.tuituidan.openhub.service.SiteService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * SiteController.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025-02-08
 */
@RestController
@RequestMapping("/api/v1")
public class SiteController {

    @Resource
    private SiteService siteService;

    /**
     * 查询网站列表
     *
     * @return List
     */
    @GetMapping("/site")
    public List<Site> listSite() {
        return siteService.listSite();
    }

    /**
     * 修改网站状态
     *
     * @param id id
     * @param state 状态
     */
    @PatchMapping("/site/{id}/actions/{state:start|stop|restart}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void siteState(@PathVariable String id, @PathVariable String state) {
        siteService.siteState(id, state);
    }

    /**
     * 修改应用程序池状态
     *
     * @param id id
     * @param state 状态
     */
    @PatchMapping("/apppool/{id}/actions/{state:start|stop|recycle}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apppoolState(@PathVariable String id, @PathVariable String state) {
        siteService.apppoolState(id, state);
    }

}
