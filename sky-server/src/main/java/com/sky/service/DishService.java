package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品，同时插入菜品对应的数据，需要操作两张表：dish、dish_flavor
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品查询
     * @param dishPageQueryDTO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品和对应的口味数据
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 更新菜品及对应的口味数据
     */
    void updateWithFlavor(DishDTO dishDTO);
}
