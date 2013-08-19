package com.example.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.util.Log;
import android.util.Xml;
import com.example.entity.SmsEntity;
import com.example.entity.UserInfo;

public class XmlTool {
	private Context context;

	public XmlTool(Context context) {
		this.context = context;
	}

	public boolean writeUserInfoXML(List<UserInfo> users, String path) {
		boolean flag = false;
		String str = writeUserInfoToString(users);

		flag = writeToXml(context, str, path);

		return flag;
	}

	public boolean writeSmsEntityXML(List<SmsEntity> entitys, String path) {
		boolean flag = false;
		String str = writeSmsEntityToString(entitys);
		flag = writeToXml(context, str, path);
		return flag;
	}

	private String writeSmsEntityToString(List<SmsEntity> entitys) {
		// 实现xml信息序列号的一个对象
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			// xml数据经过序列化后保存到String中，然后将字串通过OutputStream保存为xml文件
			serializer.setOutput(writer);
			// 文档开始
			serializer.startDocument("utf-8", true);
			// 开始一个节点

			serializer.startTag("", "smsAll");
			serializer.attribute("", "type", "list");
			for (SmsEntity info : entitys) {
				serializer.startTag("", "sms");
				serializer.startTag("", "name");
				serializer.text(info.getName());
				serializer.endTag("", "name");
				serializer.startTag("", "phoneNumber");
				serializer.text(info.getPhoneNumber());
				serializer.endTag("", "phoneNumber");
				serializer.startTag("", "smsbody");
				serializer.text(info.getSmsbody());
				serializer.endTag("", "smsbody");
				serializer.startTag("", "date");
				serializer.text(info.getDate());
				serializer.endTag("", "date");
				serializer.startTag("", "type");
				serializer.text(info.getType() + "");
				serializer.endTag("", "type");
				serializer.endTag("", "sms");
			}
			serializer.endTag("", "smsAll");

			// 关闭文档
			serializer.endDocument();

		} catch (Exception e) {
			Log.i("info", e.getMessage());
		}
		return writer.toString();
	}

	public String writeVideoInfoToString(List<String> infos) {
		// 实现xml信息序列号的一个对象
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			// xml数据经过序列化后保存到String中，然后将字串通过OutputStream保存为xml文件
			serializer.setOutput(writer);
			// 文档开始
			serializer.startDocument("utf-8", true);
			// 开始一个节点

			serializer.startTag("", "videos");
			serializer.attribute("", "type", "list");
			for (String str : infos) {
				serializer.startTag("", "video");
				serializer.text(str);
				serializer.endTag("", "video");
			}
			serializer.endTag("", "videos");

			// 关闭文档
			serializer.endDocument();

		} catch (Exception e) {
			Log.i("info", e.getMessage());
		}
		return writer.toString();
	}

	public String writeUserInfoToString(List<UserInfo> users) {
		// 实现xml信息序列号的一个对象
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		try {
			// xml数据经过序列化后保存到String中，然后将字串通过OutputStream保存为xml文件
			serializer.setOutput(writer);
			// 文档开始
			serializer.startDocument("utf-8", true);
			// 开始一个节点

			serializer.startTag("", "users");
			serializer.attribute("", "type", "list");
			for (UserInfo info : users) {
				serializer.startTag("", "user");
				/**
				 * 加属性
				 */
				serializer.startTag("", "id");
				serializer.text(info.getId() + "");
				serializer.endTag("", "id");
				serializer.startTag("", "cityid");
				serializer.text(info.getCityId());
				serializer.endTag("", "cityid");
				serializer.startTag("", "phone");
				serializer.text(info.getPhone());
				serializer.endTag("", "phone");
				serializer.startTag("", "pwd");
				serializer.text(info.getPwd());
				serializer.endTag("", "pwd");
				serializer.startTag("", "imgName");
				serializer.text(info.getImgName());
				serializer.endTag("", "imgName");
				serializer.startTag("", "sign");
				serializer.text(info.getSign());
				serializer.endTag("", "sign");
				serializer.endTag("", "user");
			}
			serializer.endTag("", "users");

			// 关闭文档
			serializer.endDocument();

		} catch (Exception e) {
			Log.i("info", e.getMessage());
		}
		return writer.toString();
	}

	/**
	 * 将xml字符串写入xml文件
	 * 
	 * @param context
	 * @param str
	 * @return
	 */
	public boolean writeToXml(Context context, String str, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream os = new FileOutputStream(path);
			// OutputStream os = openFileOutput(path, MODE_PRIVATE);
			OutputStreamWriter osw = new OutputStreamWriter(os);
			osw.write(str);
			osw.close();
			os.close();
		} catch (FileNotFoundException e) {
			Log.i("info", "getMessage = " + e.getMessage());
			return false;
		} catch (IOException e) {
			Log.i("info", "getMessage = " + e.getMessage());
			return false;
		}
		return true;
	}

	public List<SmsEntity> xmlParserSmsEntity(String filePath) throws Exception {
		List<SmsEntity> reslist = null;
		SmsEntity call = null;
		try {
			File file = new File(filePath);
			FileInputStream fs = new FileInputStream(file);
			XmlPullParser parser = Xml.newPullParser();

			parser.setInput(fs, "UTF-8");
			int eventType = parser.getEventType();
			while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {

				/**
				 * private String name; private String phoneNumber; private
				 * String smsbody; private String date; private int type;
				 */
				switch (eventType) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					Log.i("info", "name：" + name);
					if (parser.getName().equals("smsAll")) {
						reslist = new ArrayList<SmsEntity>();
					} else if (name.equals("sms")) {
						call = new SmsEntity();
						// contact.setId(Integer.valueOf(parser.getAttributeValue(0)));
					} else if (name.equals("name")) {
						String name1 = parser.nextText();
						call.setName(name1);
					} else if (name.equals("phoneNumber")) {
						call.setPhoneNumber(parser.nextText());
					} else if (name.equals("smsbody")) {
						call.setSmsbody(parser.nextText());
					} else if (name.equals("date")) {
						call.setDate(parser.nextText());
					} else if (name.equals("type")) {
						call.setType(Integer.parseInt(parser.nextText()));
					}
					break;

				case XmlPullParser.END_TAG:
					String name1 = parser.getName();
					if (name1.equals("sms")) {
						reslist.add(call);
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reslist;
	}

	public List<UserInfo> xmlParserUserInfo(String filePath) throws Exception {

		List<UserInfo> reslist = null;
		UserInfo call = null;
		try {
			File file = new File(filePath);
			FileInputStream fs = new FileInputStream(file);
			XmlPullParser parser = Xml.newPullParser();

			parser.setInput(fs, "UTF-8");
			int eventType = parser.getEventType();
			while ((eventType = parser.next()) != XmlPullParser.END_DOCUMENT) {

				switch (eventType) {
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					Log.i("info", "name：" + name);
					if (parser.getName().equals("users")) {
						reslist = new ArrayList<UserInfo>();
					} else if (name.equals("user")) {
						call = new UserInfo();
						// contact.setId(Integer.valueOf(parser.getAttributeValue(0)));
					} else if (name.equals("id")) {
						String id = parser.nextText();
						Log.i("info", "id：" + id);
						call.setId(Integer.parseInt(id));
					} else if (name.equals("phone")) {
						call.setPhone(parser.nextText());
					} else if (name.equals("pwd")) {
						call.setPwd(parser.nextText());
					} else if (name.equals("imgName")) {
						call.setImgName(parser.nextText());
					} else if (name.equals("sign")) {
						call.setSign(parser.nextText());
					} else if (name.equals("cityid")) {
						call.setCityId(parser.nextText());
					}

					break;

				case XmlPullParser.END_TAG:
					String name1 = parser.getName();
					if (name1.equals("user")) {
						reslist.add(call);
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reslist;

	}
}
