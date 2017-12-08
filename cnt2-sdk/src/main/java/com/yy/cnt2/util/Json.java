package com.yy.cnt2.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 封装了jackson常用的API，默认 disable了FAIL_ON_UNKNOWN_PROPERTIES参数，忽略不存在的属性
 *
 */
public class Json {

	private final static ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); // 忽略不存在的属性
	}

	/**
	 * 通过JSON字符串生成对象
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param type
	 *            返回值的类型
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return T 指定对象
	 */
	public static <T> T strToObj(String jsonStr, Class<T> type) {
		try {
			return mapper.readValue(jsonStr, type);
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}

	/**
	 * 生成对象对应的JSON字符串.
	 * 
	 * @param obj
	 *            对象实例
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return 返回生成的字符串
	 */
	public static String ObjToStr(Object obj) {
		if (obj == null) {
			throw new RuntimeException("Failed to map object, which is null");
		}
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			String msg = String.format("Failed to map object {}", obj);
			throw new RuntimeException(msg, e);
		}
	}
	
	/**
	 * 通过JSON字符串生成指定类型的引用对象
	 * @param jsonStr json串
	 * @param type 引用类型  T
	 * @return
	 * 		T 引用类型的对象实例
	 */
	public static <T> T strToObj(String jsonStr, TypeReference<T> type){
		try {
			return  mapper.readValue(jsonStr, type);
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	private static JsonNode toJsonNode(String jsonStr ,String fieldName) throws JsonProcessingException, IOException{
		JsonNode jsonNode = mapper.readTree(jsonStr);
		if (jsonNode == null) {
            return null;
        }
		JsonNode nameNode = jsonNode.get(fieldName);		
		return nameNode;
	}
	
	/**
	 * 取json传里面的某个属性值。
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param fieldName
	 *            熟悉名称
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return Integer 该属性名称所对应的值
	 */
	public static Integer getIntField(String jsonStr, String fieldName) {
		try {
			JsonNode nameNode = toJsonNode(jsonStr,fieldName);
			if (nameNode != null) {
				Integer value = nameNode.asInt();
	            return value;
	        }
	        return null;
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	
	/**
	 * 取json传里面的某个属性值。
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param fieldName
	 *            属性名称
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return String 该属性名称所对应的值
	 */
	public static String  getStringField(String jsonStr, String fieldName) {
		try {
			JsonNode nameNode = toJsonNode(jsonStr,fieldName);
			if (nameNode != null) {
	            String value = nameNode.asText();
	            return value;
	        }
	        return null;
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	/**
	 * 取json传里面的某个属性值。
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param fieldName
	 *            属性名称
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return Long 该属性名称所对应的值
	 */
	public static Long getLongField(String jsonStr, String fieldName) {
		try {
			JsonNode nameNode = toJsonNode(jsonStr,fieldName);
			if (nameNode != null) {
				Long value = nameNode.asLong();
	            return value;
	        }
	        return null;
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	/**
	 * 取json传里面的某个属性值。
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param fieldName
	 *            属性名称
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return Boolean 该属性名称所对应的值
	 */
	public static Boolean getBooleanField(String jsonStr, String fieldName) {
		try {
			JsonNode nameNode = toJsonNode(jsonStr,fieldName);
			if (nameNode != null) {
				Boolean value = nameNode.asBoolean();
	            return value;
	        }
	        return null;
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	/**
	 * 取json传里面的某个属性值。
	 * 
	 * @param jsonStr
	 *            JSON字符串
	 * @param fieldName
	 *            属性名称
	 * @exception e
	 *                对象为空时，底层抛出异常时，均会封装成RuntimeException抛出
	 * @return Double 该属性名称所对应的值
	 */
	public static Double getDoubleField(String jsonStr, String fieldName) {
		try {
			JsonNode nameNode = toJsonNode(jsonStr,fieldName);
			if (nameNode != null) {
				Double value = nameNode.asDouble();
	            return value;
	        }
	        return null;
		} catch (Exception e) {
			String msg = String.format("Failed to parse json %s", jsonStr);
			throw new RuntimeException(msg, e);
		}
	}
	
	
	
	/**
	 * 获取jackon 的原生接口
	 * @return ObjectMapper
	 */
	public static ObjectMapper getObjectMapper(){
		return mapper;
	}
}
