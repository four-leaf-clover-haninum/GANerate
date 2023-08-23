package com.example.GANerate.config.timer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

//AOP로 사용할 클래스라는 것을 명시
@Aspect
@Slf4j
//클래스를 스프링 빈으로 등록
@Component
public class TimerAop {

    @Pointcut("@annotation(com.example.GANerate.config.timer.Timer)")//Timer 어노테이션이 붙은 메서드에만 적용
    private void enableTimer(){}

    @Around("enableTimer()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{ //메서드 실행시 걸린시간 측정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object result = joinPoint.proceed(); // 조인포인트의 메서드 실행

        stopWatch.stop();

        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        log.info("실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis);

        return result; // 조인포인트의 결과를 반환
    }
}