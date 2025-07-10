package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 添加购物车数据
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据Id 修改购物车数量
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     */
    @Insert("insert into shopping_cart (name, user_id, image, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{name}, #{userId}, #{image}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);
}
