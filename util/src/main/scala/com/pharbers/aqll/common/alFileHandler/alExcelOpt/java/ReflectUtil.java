package com.pharbers.aqll.common.alFileHandler.alExcelOpt.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * 反射工具类
 * 
 * @author liujiduo
 * 
 */
public class ReflectUtil {

	/**
	 * 反射调用指定构造方法创建对象
	 *
	 * @param clazz
	 *            对象类型
	 * @param argTypes
	 *            参数类型
	 * @param args
	 *            构造参数
	 * @return 返回构造后的对象
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 *
	 */
	public static <T> T invokeConstructor(Class<T> clazz, Class<?>[] argTypes,
	                                      Object[] args) throws NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		Constructor<T> constructor = clazz.getConstructor(argTypes);
		return constructor.newInstance(args);
	}

	/**
	 * 反射调用指定对象属性的getter方法
	 *
	 * @param <T>
	 *            泛型
	 * @param target
	 *            指定对象
	 * @param fieldName
	 *            属性名
	 * @return 返回调用后的值
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *
	 */
	public static <T> Object invokeGetter(T target, String fieldName)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		// 如果属性名为xxx，则方法名为getXxx
		String methodName = "get" + StringUtil.firstCharUpperCase(fieldName);
		Method method = target.getClass().getMethod(methodName);
		return method.invoke(target);
	}

	/**
	 * 反射调用指定对象属性的setter方法
	 *
	 * @param <T>
	 *            泛型
	 * @param target
	 *            指定对象
	 * @param fieldName
	 *            属性名
	 * @param args
	 *            参数列表
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *
	 */
	public static <T> void invokeSetter(T target, String fieldName, Object args)
			throws NoSuchFieldException, SecurityException,
			NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// 如果属性名为xxx，则方法名为setXxx
		String methodName = "set" + StringUtil.firstCharUpperCase(fieldName);
		Class<?> clazz = target.getClass();
		Field field = clazz.getDeclaredField(fieldName);
		Method method = clazz.getMethod(methodName, field.getType());
		args = convert(field,args);
		method.invoke(target, args);
	}

	/**
	 * 获取所有的成员变量,包括父类
	 *
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public static <T> Field[] getClassFieldsAndSuperClassFields(Class<T> clazz) throws Exception {

		Field[] fields = clazz.getDeclaredFields();

		if (clazz.getSuperclass() == null) {
			throw new Exception(clazz.getName() + "没有父类");
		}

		Field[] superFields = clazz.getSuperclass().getDeclaredFields();

		Field[] allFields = new Field[fields.length + superFields.length];

		for (int i = 0; i < fields.length; i++) {
			allFields[i] = fields[i];
		}
		for (int i = 0; i < superFields.length; i++) {
			allFields[fields.length + i] = superFields[i];
		}

		return allFields;
	}

	/**
	 * 反射调用指定对象的父级属性的setter方法
	 *
	 * @param <T>
	 *            泛型
	 * @param target
	 *            指定对象
	 * @param fieldName
	 *            属性名
	 * @param args
	 *            参数列表
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 *
	 */
	public static <T> void invokeSuperSetter(T target, String fieldName, Object args)
			throws NoSuchFieldException, SecurityException,
			NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		// 如果属性名为xxx，则方法名为setXxx
		String methodName = "set" + StringUtil.firstCharUpperCase(fieldName);
		Class<?> clazz = target.getClass();
		Field field = clazz.getSuperclass().getDeclaredField(fieldName);
		Method method = clazz.getMethod(methodName, field.getType());
		method.invoke(target, args);
	}

	public static Object convert(Field obj1,Object obj2){
		if(obj1.getType().getSimpleName().equals("String")){
			return obj2;
		}else if(obj1.getType().getSimpleName().equals("Integer")){
			return Integer.parseInt("".equals(obj2.toString().replaceAll("\\s*", "")) || obj2.toString().replaceAll("\\s*", "") == null ? "0" : obj2.toString());
		}else if(obj1.getType().getSimpleName().equals("Long")){
			return Long.parseLong("".equals(obj2.toString().replaceAll("\\s*", "")) || obj2.toString().replaceAll("\\s*", "") == null ? "0" : obj2.toString());
		}else if(obj1.getType().getSimpleName().equals("Double")){
			return Double.parseDouble("".equals(obj2.toString().replaceAll("\\s*", "")) || obj2.toString().replaceAll("\\s*", "") == null ? "0" : obj2.toString());
		}
		return obj2;
	}
}
