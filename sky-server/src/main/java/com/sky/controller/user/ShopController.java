package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){
        Object value = redisTemplate.opsForValue().get(KEY);
        if (value == null) {
            log.warn("Redis中未找到KEY={}, 默认返回打烊中（0）", KEY);
            // 也可以根据业务实际返回Result.error(...)
            return Result.success(0);
        }
        int status;
        try {
            status = Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.error("Redis中KEY={}的值无法转换为整数: {}", KEY, value);
            // 也可以根据业务实际返回Result.error(...)
            return Result.success(0);
        }
        log.info("获取到店铺营业状态为:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
