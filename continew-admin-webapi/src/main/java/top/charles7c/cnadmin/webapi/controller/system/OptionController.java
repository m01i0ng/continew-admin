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

package top.charles7c.cnadmin.webapi.controller.system;

import java.util.List;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import top.charles7c.cnadmin.common.model.vo.R;
import top.charles7c.cnadmin.system.model.query.OptionQuery;
import top.charles7c.cnadmin.system.model.vo.OptionVO;
import top.charles7c.cnadmin.system.service.OptionService;

/**
 * 系统参数管理 API
 *
 * @author Bull-BCLS
 * @since 2023/8/26 19:38
 */
@Tag(name = "系统参数管理 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/option")
public class OptionController {

    private final OptionService optionService;

    @Operation(summary = "查询系统参数列表", description = "查询系统参数列表")
    @GetMapping
    public R<List<OptionVO>> list(@Validated OptionQuery query) {
        return R.ok(optionService.list(query));
    }
}