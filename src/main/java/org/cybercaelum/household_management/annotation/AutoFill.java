package org.cybercaelum.household_management.annotation;

import io.swagger.v3.oas.annotations.Operation;
import org.cybercaelum.household_management.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//自定义注解，填充创建时间和修改时间
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {

    OperationType value();
}
