package com.example.constants;

public class Constants {
	public interface USER_STATUS {
		public static final int SCALE = 3;// 照片缩小比例
		public static final int PHOTO_TO_MAIN = 0001;
		public static final int PHOTO_TO_VIDEO = 0002;
		public static final int APPLIST_TO_MAIN = 0003;
		public static final int APPLIST_TO_CATEGORY = 0004;
		public static final int CATEGORY_TO_APPLIST = 0005;
		public static final int CAMERA_WITH_DATA = 1101;
		public static final int PHOTO_PICKED_WITH_DATA = 1102;
		public static final int SMS_TO_CONTACT = 1103;
		public static final int VIDEO_TO_PHOTO = 1104;

		// 网络不可用
		public static final int NOT_NETWORK = 2000;
		// 发送请求
		public static final int MSG_SEND_REQUEST = 2001;
		// 发送请求成功
		public static final int MSG_SEND_SUCCESS = 2002;
		// 请求超时
		public static final int MSG_REQUEST_TIMEOUT = 2003;
		// 请求失败
		public static final int MSG_SEND_FALIURE = 2004;
		public static final int LOGIN_SUCCESS = 2005;

		public static final int FEED_BACK_STATUS = 2006;
		public static final int ABOUT_URI_STATUS = 2007;
		public static final int WARNING_URI_STATUS = 2008;
		public static final int MORE_APP_URI_STATUS = 2009;
		public static final int SEND_FRIEND_URI_STATUS = 2010;

		public static final int SMS_ALL_STATUS = 2011;
		public static final int SMS_DELETE_STATUS = 2012;

		public static final int PHOTO_STATUS = 2011;
		
		public static final int MYPIC_ADAPTER_TO_PIC = 2012;

		public static final int LIST_APP_CATEGORY_STATUS = 2012;

		public static final int PERSONAL_LIST_APP_STATUS = 2013;
		
		public static final int BACKGROUND_FROM_LOCAL = 2014;
		
		public static final int BACKGROUND_FROM_NET = 2015;

	}

	public interface FTP_STATUS {
		public static final String FTP_HOST = "42.96.140.41";
		public static final String FTP_USERNAME = "huqin135";
		public static final String FTP_PWD = "hh654654";
		/*
		 * 1.person(个人资料目录)；2.album(相册)；3.video（视频）；
		 * 4.message(信息)；5.note（记事本）；6.project（项目安排）；
		 * 7.track（个人纪录）；8.chengji（考核，成绩）；9.cost（费用开支）；
		 * 10.arrangement(事情安排)；11.zuopin（个人作品）；12.mypic（个人图片）
		 */
		public static final String WORKSPACE_PERSONAL_INFO = "/person/";
		public static final String WORKSPACE_ALBUM_INFO = "/album/";
		public static final String WORKSAPCE_VIDEO_INFO = "/video/";
		public static final String WORKSPACE_MESSAGE_INFO = "/message/";
		public static final String WORKSPACE_NOTE_INFO = "/note/";
		public static final String WORKSPACE_PROJECT_INFO = "/project/";
		public static final String WORKSPACE_TRACK_INFO = "/track/";
		public static final String WORKSPACE_CHENGJI_INFO = "/chengji/";
		public static final String WORKSPACE_COST_INFO = "/cost/";
		public static final String WORKSPACE_ARRAGEMENT_INFO = "/arrangement/";
		public static final String WORKSPACE_MYPIC_INFO = "/mypic/";
		public static final String ZUO_PIN = "/zuopin/";

		public static final String PERSON_TXT_NAME = "personInfo.xml";
		public static final String ALBUM_TXT_NAME = "album.xml";
		public static final String VIDEO_TXT_NAME = "video.xml";
		public static final String MESSAGE_TXT_NAME = "message.xml";
		public static final String NOTE_BAR_TXT_NAME = "noteBar.xml";
		public static final String NOTE_TXT_NAME = "note.xml";
		public static final String PROJECT_TXT_NAME = "project.xml";
		public static final String PROJECT_BAR_TXT_NAME = "proBar.xml";
		public static final String TRACK_TXT_NAME = "track.xml";
		public static final String CHENGJI_TXT_NAME = "chengji.xml";
		public static final String FEELING_TXT_NAME = "feeling.xml";
		public static final String COST_TXT_NAME = "cost.xml";
		public static final String COST_CHILD_TXT_NAME = "costChild.xml";
		public static final String ARRAGMENG_TXT_NAME = "arrangement.xml";
		public static final String ARRAGMENG_CHILD_TXT_NAME = "arrChild.xml";
		public static final String MYPIC_TXT_NAME = "mypic.xml";

	}

	public interface USER_FLAG {
		public static final String MAIN_TO_FLAG = "flag";
		public static final String MAIN_TO_COST = "cost";
		public static final String MAIN_TO_THING = "thing";
		public static final String MAIN_TO_NOTEBOOK = "noteBook";
		public static final String MAIN_TO_PROJECT = "project";
	}

	public interface GET_QUEST_URI {
		// http://42.96.140.41:7878/WDWebService.asmx/
		public static final String ROOT_IMG = "http://42.96.140.41:7878";
		public static final String ROOT = "http://42.96.140.41:7878/WDWebService.asmx/";
		public static final String LOGIN_URI = ROOT + "P2_OverUserByName?";
		public static final String FEED_BACK_URI = ROOT
				+ "P4_SetByConsultationADD?";
		public static final String ABOUT_URI = ROOT
				+ "P3_GetByCommodityStudio?";
		public static final String WARNING_URI = ROOT + "P7_GetByServeByState";
		public static final String MORE_APP_URI = ROOT + "P6_GetByApply?";
		public static final String SEND_FRIEND_URI = ROOT
				+ "P5_GetByServeByNote?";

		public static final String PERSONAL_LIST_APP_URI = ROOT
				+ "GetByCommodity?";
		public static final String PERSONAL_APP_DETAIL_URI = ROOT
				+ "GetByCommodityString?";

		public static final String SMS_ALL_URI = ROOT + "P9_GetByBlessing?";
		public static final String SMS_DELETE_URI = ROOT
				+ "P9_SetByByBlessingDel?";

		public static final String GET_PICTURE_URI = "http://42.96.140.41:7878/Users/";
		public static final String GET_ALL_DATA_URI = "http://42.96.140.41:7878/WDService.asmx/FileNodeXMLPlist?";
	}
}
