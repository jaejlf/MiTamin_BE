package great.job.mytamin.global.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class LogConfig {

    @Around("execution(* great.job.mytamin.domain.*.controller..*(..))")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startAt = System.currentTimeMillis();
        log.info("[REQUEST] {}", joinPoint.getSignature().toShortString());
        Object result = joinPoint.proceed();
        long endAt = System.currentTimeMillis();
        log.info("[RESPONSE] {} = {} ({}ms)", joinPoint.getSignature().toShortString(), result, endAt - startAt);

        return result;
    }

}