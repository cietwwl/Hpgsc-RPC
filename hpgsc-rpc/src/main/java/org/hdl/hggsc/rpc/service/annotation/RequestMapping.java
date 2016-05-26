package org.hdl.hggsc.rpc.service.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hdl.hggsc.rpc.service.ServiceFilter;
import org.hdl.hpgsc.common.io.Record;
/**
 * Request Mapping 
 * @author qiuhd
 * @since  2014年9月19日
 * @version V1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
	
	long id();
	
	Class<? extends Record>[] param() default {};
	
	Class<? extends ServiceFilter>[] filter()  default {};
}
