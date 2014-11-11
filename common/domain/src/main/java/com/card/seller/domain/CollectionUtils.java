/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.card.seller.domain;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


/**
 * 泛型工具类
 * 
 * @author vincent
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class CollectionUtils extends org.apache.commons.collections.CollectionUtils{
	
	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成Map.
	 * 
	 * @param collection 来源集合.
	 * @param keyPropertyName 要提取为Map中的Key值的属性名.
	 * @param valuePropertyName 要提取为Map中的Value值的属性名.
	 */
	public static Map extractToMap(Collection collection, String keyPropertyName, String valuePropertyName) {
		Map map = new HashMap();

		try {
			for (Object obj : collection) {
				map.put(PropertyUtils.getProperty(obj, keyPropertyName),PropertyUtils.getProperty(obj, valuePropertyName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成List.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * 
	 * @return List
	 */
	public static <T> List<T> extractToList(Collection collection, String propertyName) {
		
		return extractToList(collection,propertyName,false);
	}
	
	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成List.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param ignoreEmptyValue 是否过滤null值和""值
	 * 
	 * @return List
	 */
	public static <T> List<T> extractToList(Collection collection, String propertyName,boolean ignoreEmptyValue) {
		if (collection == null) {
			return null;
		}
		List list = new ArrayList();
		
		try {
			for (Object obj : collection) {
                if(obj != null) {
                    T value = (T) PropertyUtils.getProperty(obj, propertyName);
                    if (ignoreEmptyValue && value == null || value.toString().equals("")) {
                        continue;
                    }
                    list.add(PropertyUtils.getProperty(obj, propertyName));
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 提取集合中的对象的属性(通过Getter函数), 组合成由分割符分隔的字符串.
	 * 
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param separator 分隔符.
	 */
	public static String extractToString(Collection collection, String propertyName, String separator) {
		List list = extractToList(collection, propertyName);
		return StringUtils.join(list, separator);
	}

	/**
	 * @desc:提取集合中的对象的属性(通过Getter函数),去重后组合成List.
	 * @author:zhongzh
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 *
	 * @return List
	 */
	public static <T> List<T> extractToListDistinct(Collection collection, String propertyName) {

		return extractToListDistinct(collection,propertyName,false);
	}

	/**
	 * @desc:提取集合中的对象的属性(通过Getter函数), 去重后组合成List.
	 * @author:zhongzh
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param ignoreEmptyValue 是否过滤null值和""值
	 *
	 * @return List
	 */
	public static <T> List<T> extractToListDistinct(Collection collection, String propertyName,boolean ignoreEmptyValue) {
		if (collection == null) {
			return null;
		}

		List list = new ArrayList();

		try {
			for (Object obj : collection) {
				T value = (T) PropertyUtils.getProperty(obj, propertyName);
				if (ignoreEmptyValue && value == null || value.toString().equals("")) {
					continue;
				}
                if(!exists(list,value)) {
                   list.add(value);
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * @desc:提取集合中的对象的属性(通过Getter函数), 去重后组合成由分割符分隔的字符串.
	 * @author:zhongzh
	 * @param collection 来源集合.
	 * @param propertyName 要提取的属性名.
	 * @param separator 分隔符.
	 */
	public static String extractToStringDistinct(Collection collection, String propertyName, boolean ignoreEmptyValue,String separator) {
		List list = extractToListDistinct(collection, propertyName,ignoreEmptyValue);
		return StringUtils.join(list, separator);
	}

    /**
     * @desc: 依据value的string值判断collection中是否存在该value,。
     * @author:zhongzh
     * @param collection
     * @param value
     * @return
     */
    public static boolean exists(Collection collection, Object value) {
        if (collection==null || collection.size()==0){
            return false;
        }
        boolean flag = false;
         for(Object o:collection) {
            if(value.toString().trim().equals(o.toString().trim())) {
                flag = true;
                break;
            }
         }
        return flag;
    }
}
