package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    @ApiOperation("修改店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("修改店铺营业状态:{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(KEY,String.valueOf(status));
        return Result.success();
    }

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus() {
        Object value = redisTemplate.opsForValue().get(KEY);
        if (value == null) {
            log.warn("Redis中未找到KEY={}, 默认返回打烊中（0）", KEY);
            // 你可以根据业务需要返回默认值，比如0代表打烊中，也可以返回错误提示
            return Result.success(0);
        }
        int status;
        try {
            status = Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.error("Redis中KEY={}的值无法转换为整数: {}", KEY, value);
            // 你可以根据业务需要决定如何处理异常，比如返回默认值或抛出错误
            return Result.success(0);
        }
        log.info("获取到店铺营业状态为:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

}
