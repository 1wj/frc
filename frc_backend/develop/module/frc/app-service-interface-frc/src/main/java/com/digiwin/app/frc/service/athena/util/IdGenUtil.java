/**
 * 
 */
package com.digiwin.app.frc.service.athena.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
* @Title: IdGen.java
* @Description: 32位OID
* @author libin
* @date 2021年4月8日
* @version V1.0
*/

/**
 * 封装各种生成唯一性ID算法的工具类.
 */
public class IdGenUtil {

	private static SecureRandom random = new SecureRandom();
	
	/**
	 * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
	 */
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	/**
	 * 使用SecureRandom随机生成Long
	 */
	public static long randomLong() {
		return Math.abs(random.nextLong());
	}


	public static void main(String[] args) {
		System.out.println(IdGenUtil.uuid());
		System.out.println(IdGenUtil.uuid().length());
	}
}
