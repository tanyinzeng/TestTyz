package com.example.constants;

public class StringUtil {
	/**
	 * ÅĞ¶ÏÄÚÈİÊÇ·ñÎª¿Õ
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(Object str) {
		if (null == str || "".equals(str.toString().trim())) {
			return true;
		}

		return false;
	}

}
