package com.sinaev.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods or classes for logging.
 * <p>
 * This annotation can be applied to methods or types (classes and interfaces).
 * When applied, it indicates that the annotated method or class should have logging
 * applied, typically for logging method entry, exit, and exceptions.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Loggable {
}
