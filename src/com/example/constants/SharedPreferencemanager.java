package com.example.constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.example.entity.CostSpendEntity;
import com.example.entity.FeelingContentEntity;
import com.example.entity.MyPicEntity;
import com.example.entity.NoteBarEntity;
import com.example.entity.NoteBookEntity;
import com.example.entity.PersonRecordEntity;
import com.example.entity.ProjectEntity;
import com.example.entity.ScoreContentEntity;
import com.example.entity.UserInfo;

public class SharedPreferencemanager {
	private static final String PREFERENCEFILENAME = "preferencefilename";

	public static void setIsLogin(boolean isLogin, Context context) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putBoolean("isLogin", isLogin).commit();
	}

	public static boolean getIsLogin(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getBoolean("isLogin", false);
	}

	public static void setUserPhone(String phone, Context context) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putString("phone", phone).commit();
	}

	public static String getPhone(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getString("phone", "");
	}

	public static void setUserPwd(String pwd, Context context) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putString("pwd", pwd).commit();
	}

	public static String getPwd(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getString("pwd", "");
	}

	public static void setUserId(int id, Context context) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putInt("userId", id).commit();
	}

	public static int getUserId(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getInt("userId", 0);
	}

	public static void setCityId(String id, Context context) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putString("cityId", id).commit();
	}

	public static String getCityId(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getString("cityId", "");
	}

	public static List<CostSpendEntity> pullCostEntityGroupFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<CostSpendEntity> products = (ArrayList<CostSpendEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}


	public static List<PersonRecordEntity> pullPersonRecordFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<PersonRecordEntity> products = (ArrayList<PersonRecordEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static List<ProjectEntity> pullProjectEntityGroupFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<ProjectEntity> products = (ArrayList<ProjectEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static List<List<ProjectEntity>> pullProjectEntityChildFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<List<ProjectEntity>> products = (ArrayList<List<ProjectEntity>>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static List<List<CostSpendEntity>> pullCostEntityChildFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<List<CostSpendEntity>> products = (ArrayList<List<CostSpendEntity>>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushPersonalRecordToFile(
			List<PersonRecordEntity> productArray, Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void pushMyPicToFile(List<MyPicEntity> productArray,
			Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}
	
	public static List<MyPicEntity> pullMyPicFromFile(Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<MyPicEntity> products = (ArrayList<MyPicEntity>) ois.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushCostEntityGroupToFile(
			List<CostSpendEntity> productArray, Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void pushProjectEntityGroupToFile(
			List<ProjectEntity> productArray, Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void pushProjectEntityChildToFile(
			List<List<ProjectEntity>> productArray, Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void pushCostEntityChildToFile(
			List<List<CostSpendEntity>> productArray, Context context,
			String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(productArray);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void pushNoteBookEntityToFile(
			Map<String, List<NoteBookEntity>> maps, Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(maps);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static void setPhotoOnline(Context context, boolean photoOnline) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putBoolean("photoOnline", photoOnline).commit();
	}

	public static boolean getPhotoOnline(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getBoolean("photoOnline", false);
	}

	public static void setVideoOnline(Context context, boolean videoOnline) {
		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
				.edit().putBoolean("videoOnline", videoOnline).commit();
	}

	public static boolean getVideoOnline(Context context) {
		return context.getSharedPreferences(PREFERENCEFILENAME,
				Context.MODE_PRIVATE).getBoolean("videoOnline", false);
	}

//	public static void setRecordOnline(Context context, boolean recordOnline) {
//		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
//				.edit().putBoolean("recordOnline", recordOnline).commit();
//	}
//
//	public static boolean getRecordOnline(Context context) {
//		return context.getSharedPreferences(PREFERENCEFILENAME,
//				Context.MODE_PRIVATE).getBoolean("recordOnline", false);
//	}

//	public static void setScoreOnline(Context context, boolean scoreOnline) {
//		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
//				.edit().putBoolean("scoreOnline", scoreOnline).commit();
//	}
//
//	public static boolean getScoreOnline(Context context) {
//		return context.getSharedPreferences(PREFERENCEFILENAME,
//				Context.MODE_PRIVATE).getBoolean("scoreOnline", false);
//	}

//	public static void setMyPicOnline(Context context, boolean myPic) {
//		context.getSharedPreferences(PREFERENCEFILENAME, Context.MODE_PRIVATE)
//				.edit().putBoolean("myPic", myPic).commit();
//	}
//
//	public static boolean getMyPicOnline(Context context) {
//		return context.getSharedPreferences(PREFERENCEFILENAME,
//				Context.MODE_PRIVATE).getBoolean("myPic", false);
//	}

	public static Map<String, List<NoteBookEntity>> pullNoteEntityFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			Map<String, List<NoteBookEntity>> products = (Map<String, List<NoteBookEntity>>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushUserInfoToFile(UserInfo info, Context context,
			String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(info);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static UserInfo pullUserInfoFromFile(Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			UserInfo userInfo = (UserInfo) ois.readObject();
			ois.close();
			return userInfo;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushNoteBarsToFile(List<NoteBarEntity> noteBars,
			Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(noteBars);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static List<NoteBarEntity> pullNoteBarsFromFile(Context context,
			String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<NoteBarEntity> products = (List<NoteBarEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushStringPhotoToFile(List<String> strs,
			Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(strs);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static List<String> pullStringPhotoFromFile(Context context,
			String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<String> products = (List<String>) ois.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushScoreContentToFile(List<ScoreContentEntity> strs,
			Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(strs);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static List<ScoreContentEntity> pullScoreContentFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<ScoreContentEntity> products = (List<ScoreContentEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

	public static void pushFeelContentToFile(List<FeelingContentEntity> strs,
			Context context, String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream oos = new FileOutputStream(path);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(oos);
			objectOutputStream.writeObject(strs);
			objectOutputStream.flush();
			objectOutputStream.close();
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
		}
	}

	public static List<FeelingContentEntity> pullFeelContentFromFile(
			Context context, String path) {
		try {
			InputStream is = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(is);
			List<FeelingContentEntity> products = (List<FeelingContentEntity>) ois
					.readObject();
			ois.close();
			return products;
		} catch (Exception e) {
			LogUtil.log("getMessage = " + e.getMessage());
			return null;
		}
	}

}
