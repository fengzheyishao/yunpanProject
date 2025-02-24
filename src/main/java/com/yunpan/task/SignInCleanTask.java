package com.yunpan.task;

import com.yunpan.component.RedisComponent;
import com.yunpan.entity.constants.Constants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

@Component
public class SignInCleanTask {
    @Resource
    private RedisComponent redisComponent;

    @Scheduled(cron = "0 0 0 1 1 *") // 每年1月1日的0点执行
    public void cleanUpOldSignInData() {
        // 清理去年的签到数据
        String bitmapKey = Constants.REDIS_KEY_USER_SIGN_IN + "*"; // 使用通配符匹配去年的所有签到数据
        redisComponent.deleteKey(bitmapKey);
    }
}
