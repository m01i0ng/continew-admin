/*
 * Copyright (c) 2022-present Charles7c Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.charles7c.cnadmin.monitor.model.entity;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import top.charles7c.cnadmin.monitor.enums.LogLevelEnum;

/**
 * 操作日志实体
 *
 * @author Charles7c
 * @since 2022/12/25 9:11
 */
@Data
@TableName("sys_log")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId
    private Long logId;

    /**
     * 日志级别
     */
    private LogLevelEnum logLevel;

    /**
     * 日志描述
     */
    private String description;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 请求方式
     */
    private String requestMethod;

    /**
     * 请求头
     */
    private String requestHeader;

    /**
     * 请求体
     */
    private String requestBody;

    /**
     * 状态码
     */
    private Integer statusCode;

    /**
     * 响应头
     */
    private String responseHeader;

    /**
     * 响应体
     */
    private String responseBody;

    /**
     * 请求耗时（ms）
     */
    private Long elapsedTime;

    /**
     * 请求IP
     */
    private String requestIp;

    /**
     * 操作地址
     */
    private String location;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 异常
     */
    private String exception;

    /**
     * 操作人
     */
    private Long createUser;

    /**
     * 操作时间
     */
    private Date createTime;
}
