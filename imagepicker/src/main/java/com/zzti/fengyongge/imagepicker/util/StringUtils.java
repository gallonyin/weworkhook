/*
 * Copyright (c) 2016, ZCJ All Rights Reserved.
 * Project Name: zcj_android-V0.21
 */
package com.zzti.fengyongge.imagepicker.util;


public class StringUtils {

	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}


	/**
	 * 判断字符串是否为空
	 * @param text
	 * @return true null false !null
	 */
	public static boolean isNull(CharSequence text) {
		if (text == null || "".equals(text.toString().trim()) || "null".equals(text))
			return true;
		return false;
	}
}
