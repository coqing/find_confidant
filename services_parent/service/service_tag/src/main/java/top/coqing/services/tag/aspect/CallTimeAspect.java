package top.coqing.services.tag.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CallTimeAspect {
  @Pointcut("@annotation(CallTime) && execution(public * top.coqing.services.tag..*.*(..))")
  public void pointCutMethod() {

  }

  @Around("pointCutMethod()")
  public Object callTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object proceed = joinPoint.proceed();
    long end = System.currentTimeMillis();
    Class<?> aClass = joinPoint.getTarget().getClass();
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    System.out.println(String.format("%s.%s takes %dms", aClass.getName(), methodSignature.getName(), end - start));
    return proceed;
  }
}