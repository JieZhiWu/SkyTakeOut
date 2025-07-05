package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.sound.sampled.Line;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper; //注入菜品Mapper, 用于操作数据库

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDTO
     */
    @Transactional
    @Override   //可删除,但降低代码可读性
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();

        BeanUtils.copyProperties(dishDTO, dish);

        //插入1条菜品数据
        dishMapper.insert(dish);

        //获取insert语句生成的id
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishId));
            //插入n条口味数据
            dishFlavorMapper.insertBatch(flavors);
        }
/*
        if (!CollectionUtils.isEmpty(flavors))
*/
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     **/
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     **/
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {

        // 判断当前菜品是否能够删除--菜品是否在售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品在售, 不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否能够删除--当前菜品是否有关联的套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

/*
        // 删除菜品数据
        for (Long id: ids) {
            dishMapper.deleteById(id);
            // 删除菜品的关联的关联的口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
*/
        //根据菜品id集合批量删除菜品数据
        dishMapper.deleteByIds(ids);
        //根据菜品id集合批量删除菜品口味数据
        dishFlavorMapper.deleteByDishIds(ids);

    }

    /**
     * 根据id查询菜品和对应的口味数据
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);

        //根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        //将查询到的数据封装成 DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 根据id修改菜品基本信息和口味数据
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //修改dish表基本数据
        dishMapper.update(dish);

        //删除原有菜品口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //重新插入口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 根据分类id查询菜品
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 启售停售菜品管理
     */
    /**
     * 启售停售菜品管理
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 获取当前菜品对象
        Dish dish = dishMapper.getById(id);
        if (dish == null) {
            throw new DeletionNotAllowedException(MessageConstant.UNKNOWN_ERROR); // 或自定义菜品不存在异常
        }

        // 停售操作：检查是否被启用状态的套餐引用
        if (status == StatusConstant.DISABLE) {
            // 查询所有包含此菜品的启用状态的套餐ID
            List<Long> usingSetmealIds = setmealDishMapper.getSetmealIdsByDishIdAndStatus(id, StatusConstant.ENABLE);
            if (!CollectionUtils.isEmpty(usingSetmealIds)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_DISABLE_FAILED);
            }
        }

        dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        // 更新菜品状态
        dishMapper.update(dish);
    }

}
