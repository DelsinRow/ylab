package com.sinaev.aspects;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
    @Pointcut("execution(* com.sinaev.services.*.*(..))")
    public void auditMethods() {
    }

    @Pointcut("within(@com.sinaev.annotations.Loggable *) && execution(* * (..))")
    public void annotatedByLoggable() {
    }
}
