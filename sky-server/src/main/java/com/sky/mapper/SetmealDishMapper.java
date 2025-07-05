package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐和菜品的关联关系
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除套餐和菜品的关联数据
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐id查询套餐和菜品种类数量
     */
    @Select("select count(1) from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getSetmealDishesBySetmealId(Long id);

    /**
     * 根据菜品id查询关联的套餐数量
     */
//    select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}
    @Select({
            "SELECT DISTINCT sd.setmeal_id FROM setmeal_dish sd JOIN setmeal s ON sd.setmeal_id = s.id " +
                    "WHERE sd.dish_id = #{dishId} AND s.status = #{status}"
    })
    List<Long> getSetmealIdsByDishIdAndStatus(Long dishId, Integer status);
}
