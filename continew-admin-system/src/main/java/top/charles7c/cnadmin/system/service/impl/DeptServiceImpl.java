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

package top.charles7c.cnadmin.system.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;

import top.charles7c.cnadmin.common.base.BaseServiceImpl;
import top.charles7c.cnadmin.common.enums.DisEnableStatusEnum;
import top.charles7c.cnadmin.common.util.ExceptionUtils;
import top.charles7c.cnadmin.common.util.TreeUtils;
import top.charles7c.cnadmin.common.util.validate.CheckUtils;
import top.charles7c.cnadmin.system.mapper.DeptMapper;
import top.charles7c.cnadmin.system.model.entity.DeptDO;
import top.charles7c.cnadmin.system.model.query.DeptQuery;
import top.charles7c.cnadmin.system.model.request.DeptRequest;
import top.charles7c.cnadmin.system.model.vo.DeptDetailVO;
import top.charles7c.cnadmin.system.model.vo.DeptVO;
import top.charles7c.cnadmin.system.service.DeptService;
import top.charles7c.cnadmin.system.service.UserService;

/**
 * 部门业务实现类
 *
 * @author Charles7c
 * @since 2023/1/22 17:55
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl extends BaseServiceImpl<DeptMapper, DeptDO, DeptVO, DeptDetailVO, DeptQuery, DeptRequest>
    implements DeptService {

    @Resource
    private UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long add(DeptRequest request) {
        String deptName = request.getDeptName();
        boolean isExists = this.checkNameExists(deptName, request.getParentId(), request.getDeptId());
        CheckUtils.throwIf(() -> isExists, String.format("新增失败，'%s'已存在", deptName));

        // 保存信息
        request.setStatus(DisEnableStatusEnum.ENABLE);
        return super.add(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DeptRequest request) {
        String deptName = request.getDeptName();
        boolean isExists = this.checkNameExists(deptName, request.getParentId(), request.getDeptId());
        CheckUtils.throwIf(() -> isExists, String.format("修改失败，'%s'已存在", deptName));

        super.update(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Long> ids) {
        CheckUtils.throwIf(() -> userService.countByDeptIds(ids) > 0, "所选部门存在用户关联，请解除关联后重试");
        super.delete(ids);
        super.lambdaUpdate().in(DeptDO::getParentId, ids).remove();
    }

    @Override
    public List<DeptVO> buildListTree(List<DeptVO> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }

        // 去除重复子部门列表
        List<DeptVO> deDuplicationList = deDuplication(list);
        return deDuplicationList.stream().map(d -> d.setChildren(this.getChildren(d, list)))
            .collect(Collectors.toList());
    }

    /**
     * 数据去重（去除重复子部门列表）
     *
     * @param list
     *            部门列表
     * @return 去重后部门列表
     */
    private List<DeptVO> deDuplication(List<DeptVO> list) {
        List<DeptVO> deDuplicationList = new ArrayList<>();
        for (DeptVO outer : list) {
            boolean flag = true;
            for (DeptVO inner : list) {
                // 忽略重复子列表
                if (Objects.equals(inner.getDeptId(), outer.getParentId())) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                deDuplicationList.add(outer);
            }
        }
        return deDuplicationList;
    }

    /**
     * 获取指定部门的子部门列表
     *
     * @param deptVO
     *            指定部门
     * @param list
     *            部门列表
     * @return 子部门列表
     */
    private List<DeptVO> getChildren(DeptVO deptVO, List<DeptVO> list) {
        return list.stream().filter(d -> Objects.equals(d.getParentId(), deptVO.getDeptId()))
            .map(d -> d.setChildren(this.getChildren(d, list))).collect(Collectors.toList());
    }

    @Override
    public List<Tree<Long>> buildTree(List<DeptVO> list) {
        return TreeUtils.build(list, (d, tree) -> {
            tree.setId(d.getDeptId());
            tree.setName(d.getDeptName());
            tree.setParentId(d.getParentId());
            tree.setWeight(d.getDeptSort());
        });
    }

    /**
     * 检查名称是否存在
     *
     * @param name
     *            名称
     * @param parentId
     *            上级 ID
     * @param id
     *            ID
     * @return 是否存在
     */
    private boolean checkNameExists(String name, Long parentId, Long id) {
        return super.lambdaQuery().eq(DeptDO::getDeptName, name).eq(DeptDO::getParentId, parentId)
            .ne(id != null, DeptDO::getDeptId, id).exists();
    }

    @Override
    public void fillDetail(Object detailObj) {
        super.fillDetail(detailObj);
        if (detailObj instanceof DeptDetailVO) {
            DeptDetailVO detailVO = (DeptDetailVO)detailObj;
            detailVO.setParentName(ExceptionUtils.exToNull(() -> this.get(detailVO.getParentId()).getDeptName()));
        }
    }
}
