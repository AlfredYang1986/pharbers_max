package com.pharbers.aqll.calc.util.export;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Goofy
 * Excel注解，用以生成Excel表格文件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})
public @interface Excel {
	
	//列名
	String name() default "";
	
	//宽度
	int width() default 20;

	//忽略该字段
	boolean skip() default false;
	
	//日期格式化
	String dateFormat() default "yyyy-MM-dd HH:mm:ss";
	
	//浮点数的精度
	int precision() default -1;
	
	//四舍五入
	boolean round() default true;
	
}
