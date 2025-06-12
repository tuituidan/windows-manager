package com.tuituidan.openhub.bean.file;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * FilePage.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2025/6/12
 */
@Getter
@Setter
@Accessors(chain = true)
public class FilePage {

    private String data;

    private String position;

    private Boolean hasMore;

}
