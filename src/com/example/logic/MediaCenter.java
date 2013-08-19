package com.example.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.constants.LogUtil;
import com.example.entity.AppListCategory;
import com.example.entity.FeelingContentEntity;
import com.example.entity.MoreAppEntity;
import com.example.entity.MyPicEntity;
import com.example.entity.NoteBarEntity;
import com.example.entity.NoteBookEntity;
import com.example.entity.PersonRecordEntity;
import com.example.entity.PersonalAppEntity;
import com.example.entity.ScoreContentEntity;

public class MediaCenter {
	private static MediaCenter ins;

	public static MediaCenter getIns() {
		if (ins == null) {
			ins = new MediaCenter();
		}
		return ins;
	}

	private Map<String, List<NoteBookEntity>> mapsBooks = new HashMap<String, List<NoteBookEntity>>();

	public void addMapBookEntity(String str, List<NoteBookEntity> entitys) {
		mapsBooks.put(str, entitys);

	}

	public Map<String, List<NoteBookEntity>> getMapsNote() {
		return mapsBooks;
	}

	public List<NoteBookEntity> getMapsNoteBookEntitys(String str) {
		return mapsBooks.get(str);
	}

	public int getMapsSize() {
		return mapsBooks.size();
	}

	public void clearMapNotes() {
		mapsBooks.clear();
		LogUtil.log("isEmpty = " + mapsBooks.isEmpty());
	}


	private List<NoteBarEntity> noteBars = new ArrayList<NoteBarEntity>();

	public void addNoteBars(NoteBarEntity str) {
		noteBars.add(str);
	}

	public List<NoteBarEntity> getNoteBars() {
		return noteBars;
	}

	public void clearNoteBars() {
		noteBars.clear();
	}

	private List<String> uploadPhotos = new ArrayList<String>();

	public void addPhoto(String str) {
		LogUtil.log("uploadPhotos = " + uploadPhotos.size());
		uploadPhotos.add(str);
	}

	public List<String> getPhotos() {
		return uploadPhotos;
	}

	public void clearPhotos() {
		uploadPhotos.clear();
	}

	private List<ScoreContentEntity> scores = new ArrayList<ScoreContentEntity>();

	public void addScoreContent(ScoreContentEntity entity) {
		scores.add(entity);
	}

	public List<ScoreContentEntity> getScores() {
		return scores;
	}

	public void clearScores() {
		scores.clear();
	}

	private List<FeelingContentEntity> feels = new ArrayList<FeelingContentEntity>();

	public void addFeelContent(FeelingContentEntity entity) {
		feels.add(entity);
	}

	public List<FeelingContentEntity> getFeels() {
		return feels;
	}

	public void clearFeels() {
		feels.clear();
	}

	private List<PersonalAppEntity> appMap = new ArrayList<PersonalAppEntity>();

	public void addAppMap(PersonalAppEntity entity) {
		appMap.add(entity);
	}

	public List<PersonalAppEntity> getApps() {
		return appMap;
	}

	public void clearApps() {
		appMap.clear();
	}

	private List<MoreAppEntity> moreApps = new ArrayList<MoreAppEntity>();

	public void addMoreApp(MoreAppEntity entity) {
		moreApps.add(entity);
	}

	public List<MoreAppEntity> getMoreApps() {
		return moreApps;
	}

	public void clearMoreApps() {
		moreApps.clear();
	}

	private List<PersonRecordEntity> records = new ArrayList<PersonRecordEntity>();

	public void addRecordEntity(PersonRecordEntity entity) {
		records.add(entity);
	}

	public List<PersonRecordEntity> getRecords() {
		return records;
	}

	public void clearRecords() {
		records.clear();
	}

	private List<String> myPics = new ArrayList<String>();

	public void addPic(String str) {
		myPics.add(str);
	}

	public List<String> getPics() {
		return myPics;
	}

	public void clearPics() {
		myPics.clear();
	}

	private List<AppListCategory> categorys = new ArrayList<AppListCategory>();

	public void addCategory(AppListCategory cate) {
		categorys.add(cate);
	}

	public List<AppListCategory> getCategorys() {
		return categorys;
	}

	public void clearCategorys() {
		categorys.clear();
	}

	private List<String> myPicUrls = new ArrayList<String>();

	public void addMyPic(String url) {
		myPicUrls.add(url);
	}

	public List<String> getMyPics() {
		return myPicUrls;
	}

	public void clearMyPics() {
		myPicUrls.clear();
	}

	private List<String> myPhotoUrls = new ArrayList<String>();

	public void addMyPhoto(String url) {
		myPhotoUrls.add(url);
	}

	public List<String> getMyPhoto() {
		return myPhotoUrls;
	}

	public void clearMyPhoto() {
		myPhotoUrls.clear();
	}
	
	private List<MyPicEntity> picEntitys = new ArrayList<MyPicEntity>();
	
	public void addPicEntity(MyPicEntity entity){
		picEntitys.add(entity);
	}
	
	public List<MyPicEntity> getPicEntitys(){
		return picEntitys;
	}
	
	public void clearPicEntity(){
		picEntitys.clear();
	}
	
}
