package com.example.constants;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

public class DataParseUtil {

	/**
	 * XML Pull½âÎö
	 * 
	 * @param result
	 * @return
	 */
	public static String xmlParse(String result) {
		String resu = "";

		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(
					result.getBytes()));

			XmlPullParser parser = Xml.newPullParser();

			parser.setInput(dis, "UTF-8");
			int nodeType = parser.getEventType();
			while (nodeType != XmlPullParser.END_DOCUMENT) {
				switch (nodeType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					String nodeName = parser.getName();
					if (nodeName.equalsIgnoreCase("string")) {
						resu = parser.nextText();

						LogUtil.log("xmlParse()  " + resu);
					} else if (nodeName.equalsIgnoreCase("int")) {
						resu = parser.nextText() + "";

						LogUtil.log("xmlParse()  " + resu);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				nodeType = parser.next();
			}
			dis.close();

		} catch (Exception e) {
			LogUtil.log("xmlParse() e " + e.toString());
		}

		return resu;

	}
}
