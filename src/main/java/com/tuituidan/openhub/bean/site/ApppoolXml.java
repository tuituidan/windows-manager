package com.tuituidan.openhub.bean.site;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * AppResult.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/10/25
 */
@Getter
@Setter
@Accessors(chain = true)
public class ApppoolXml {

    @JSONField(name = "APPPOOL.NAME")
    private String apppoolName;

    private String state;

}
