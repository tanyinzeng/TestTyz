package com.example.personalapp;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.adapter.ExpandableListViewaAdapter;
import com.example.adapter.ExpandableListViewaAdapter1;
import com.example.adapter.TestAdapter;
import com.example.constants.Constants;
import com.example.constants.DeviceUtil;
import com.example.constants.FTPUtil;
import com.example.constants.LogUtil;
import com.example.constants.SharedPreferencemanager;
import com.example.constants.StringUtil;
import com.example.entity.CostSpendEntity;
import com.example.entity.ProjectEntity;
import com.example.view.SLCustomListView;
import com.example.view.SLCustomListView.OnRefreshListener;

public class CostSpendingActivity extends Activity implements OnClickListener,
		OnRefreshListener {
	private List<CostSpendEntity> groupArray;// 组列表
	private List<List<CostSpendEntity>> childArray;// 子列表
	private List<ProjectEntity> proGroup;
	private List<List<ProjectEntity>> proChild;
	private ExpandableListView expandableListView_one;
	private ExpandableListViewaAdapter expandaleAdapter;
	private ExpandableListViewaAdapter1 expandaleAdapter1;
	private Button btnBack, btnWrite, btnSure;
	private RelativeLayout firstLayout, secondLayout, costTotalLayout,
			proSecondLayout, timeLayout;
	private TextView tvTitle, tvTotalMoney;
	private String flag;
	private boolean fromCost = false;
	private boolean isGroup = false;
	private int groupPos = 0;
	private String phone, localMainPath, localCost, remotePath, localPro;
	private Context context;
	private LinearLayout progressLayout;
	private SLCustomListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.cost_spending_layout);
		context = this;
		listView = (SLCustomListView) findViewById(R.id.my_pic_list);
		List<String> items = new ArrayList<String>();
		items.add("aa");
		listView.setAdapter(new TestAdapter(this, items));
		listView.setonRefreshListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		phone = SharedPreferencemanager.getPhone(this);
		localMainPath = Environment.getExternalStorageDirectory()
				+ Constants.FTP_STATUS.WORKSPACE_PERSONAL_INFO + phone;
		Intent intent = getIntent();
		flag = intent.getStringExtra(Constants.USER_FLAG.MAIN_TO_FLAG);
		LogUtil.log("flag = " + flag);
		tvTitle = (TextView) findViewById(R.id.title);
		costTotalLayout = (RelativeLayout) findViewById(R.id.cost_total_layout);
		progressLayout = (LinearLayout) findViewById(R.id.progress_layout);
		if (flag.equals(Constants.USER_FLAG.MAIN_TO_COST)) {
			fromCost = true;
			remotePath = "/" + phone + Constants.FTP_STATUS.WORKSPACE_COST_INFO;
			groupArray = new ArrayList<CostSpendEntity>();
			childArray = new ArrayList<List<CostSpendEntity>>();
			tvTitle.setText(R.string.cost_spending_title);
			costTotalLayout.setVisibility(View.VISIBLE);
			localCost = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_COST_INFO;
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = new File(localCost);
					if (!file.exists()) {
						file.mkdirs();
					}
					File[] fileList = file.listFiles();
					if (fileList.length > 0) {
						parserFileList(fileList);
					} else {
						if (FTPUtil.ftpDownload(remotePath, localCost,
								Constants.FTP_STATUS.COST_TXT_NAME)) {
							FTPUtil.ftpDownload(remotePath, localCost,
									Constants.FTP_STATUS.COST_CHILD_TXT_NAME);
							file = new File(localCost);
							if (!file.exists()) {
								file.mkdirs();
							}
							fileList = file.listFiles();
							parserFileList(fileList);
						} else {
							runOnUiThread(new Runnable() {
								public void run() {
									expandaleAdapter = new ExpandableListViewaAdapter(
											context, groupArray, childArray);
									expandableListView_one
											.setAdapter(expandaleAdapter);
									int totalMoney = 0;
									for (int i = 0; i < groupArray.size(); i++) {
										totalMoney += Integer
												.parseInt(groupArray.get(i)
														.getMoney());
									}
									tvTotalMoney.setText("￥" + totalMoney + "");
									progressLayout.setVisibility(View.GONE);
								}
							});
						}
					}
				}
			}).start();
			expandableListView_one = (ExpandableListView) findViewById(R.id.expandableListView);
			tvTotalMoney = (TextView) findViewById(R.id.cost_spending_total);
			expandaleAdapter = new ExpandableListViewaAdapter(context,
					groupArray, childArray);
			expandableListView_one.setAdapter(expandaleAdapter);
		} else {
			fromCost = false;
			remotePath = "/" + phone
					+ Constants.FTP_STATUS.WORKSPACE_ARRAGEMENT_INFO;
			tvTitle.setText(R.string.thing_schedle_title);
			costTotalLayout.setVisibility(View.GONE);
			proGroup = new ArrayList<ProjectEntity>();
			proChild = new ArrayList<List<ProjectEntity>>();
			localPro = localMainPath
					+ Constants.FTP_STATUS.WORKSPACE_ARRAGEMENT_INFO;
			new Thread(new Runnable() {
				@Override
				public void run() {
					File file = new File(localPro);
					if (!file.exists()) {
						file.mkdirs();
					}
					File[] fileList = file.listFiles();
					if (fileList.length > 0) {
						parserProFileList(fileList);
					} else {
						if (FTPUtil.ftpDownload(remotePath, localPro,
								Constants.FTP_STATUS.ARRAGMENG_TXT_NAME)) {
							FTPUtil.ftpDownload(
									remotePath,
									localPro,
									Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME);
							file = new File(localPro);
							if (!file.exists()) {
								file.mkdirs();
							}
							fileList = file.listFiles();
							parserProFileList(fileList);
						} else {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									expandaleAdapter1 = new ExpandableListViewaAdapter1(
											context, proGroup, proChild);
									expandableListView_one
											.setAdapter(expandaleAdapter1);
									progressLayout.setVisibility(View.GONE);
								}
							});
						}
					}
				}
			}).start();
			expandableListView_one = (ExpandableListView) findViewById(R.id.expandableListView);
			expandaleAdapter1 = new ExpandableListViewaAdapter1(context,
					proGroup, proChild);
			expandableListView_one.setAdapter(expandaleAdapter1);
		}
		initView();
	}

	private void parserProFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.ARRAGMENG_TXT_NAME)) {
				proGroup = SharedPreferencemanager
						.pullProjectEntityGroupFromFile(this, localPro
								+ Constants.FTP_STATUS.ARRAGMENG_TXT_NAME);
			} else if (str
					.contains(Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME)) {
				proChild = SharedPreferencemanager
						.pullProjectEntityChildFromFile(this, localPro
								+ Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME);
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				expandaleAdapter1 = new ExpandableListViewaAdapter1(context,
						proGroup, proChild);
				expandableListView_one.setAdapter(expandaleAdapter1);
				progressLayout.setVisibility(View.GONE);
			}
		});
	}

	private void parserFileList(File[] fileList) {
		for (int i = 0; i < fileList.length; i++) {
			String str = fileList[i].toString();
			if (str.contains(Constants.FTP_STATUS.COST_TXT_NAME)) {
				groupArray = SharedPreferencemanager
						.pullCostEntityGroupFromFile(this, localCost
								+ Constants.FTP_STATUS.COST_TXT_NAME);
			} else if (str.contains(Constants.FTP_STATUS.COST_CHILD_TXT_NAME)) {
				childArray = SharedPreferencemanager
						.pullCostEntityChildFromFile(this, localCost
								+ Constants.FTP_STATUS.COST_CHILD_TXT_NAME);
			}
		}
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				expandaleAdapter = new ExpandableListViewaAdapter(context,
						groupArray, childArray);
				expandableListView_one.setAdapter(expandaleAdapter);
				int totalMoney = 0;
				for (int i = 0; i < groupArray.size(); i++) {
					totalMoney += Integer
							.parseInt(groupArray.get(i).getMoney());
				}
				tvTotalMoney.setText("￥" + totalMoney + "");
				progressLayout.setVisibility(View.GONE);
			}
		});
	}

	private CostSpendEntity costEntity;
	private boolean isCost = false, isPro = false;
	private ProjectEntity proEntity;

	public void initView() {
		expandableListView_one
				.setOnGroupClickListener(new OnGroupClickListener() {
					@Override
					public boolean onGroupClick(ExpandableListView parent,
							View v, int groupPosition, long id) {
						isGroup = true;
						groupPos = groupPosition;
						String title = "";
						if (fromCost) {
							title = groupArray.get(groupPosition).getTitle();
						} else {
							title = proGroup.get(groupPosition).getProName();
						}
						if (title.length() > 5) {
							title = title.substring(0, 5) + "...";
						}
						tvTitle.setText(title);
						return false;
					}
				});
		expandableListView_one
				.setOnChildClickListener(new OnChildClickListener() {
					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						if (fromCost) {
							firstLayout.setVisibility(View.GONE);
							secondLayout.setVisibility(View.VISIBLE);
							costEntity = childArray.get(groupPosition).get(
									childPosition);
							etCostTitle.setText(costEntity.getTitle());
							etCostMoney.setText(costEntity.getMoney() + "");
							etCostTime.setText(costEntity.getTime());
							etCostMemo.setText(costEntity.getMemo());
							tvTitle.setText("编辑费用开支");
							isCost = true;
						} else {
							proEntity = proChild.get(groupPosition).get(
									childPosition);
							tvTitle.setText("编辑事情安排");
							firstLayout.setVisibility(View.GONE);
							btnWrite.setVisibility(View.GONE);
							btnSure.setVisibility(View.VISIBLE);
							proSecondLayout.setVisibility(View.VISIBLE);
							etProEndTime.setText(proEntity.getEndTime());
							etProImport.setText(proEntity.getProImport());
							etProMemo.setText(proEntity.getProMemo());
							etProName.setText(proEntity.getProName());
							etProStartTime.setText(proEntity.getStartTime());
							etProType.setText(proEntity.getProType());
							isPro = true;
						}
						return false;
					}
				});
		btnBack = (Button) findViewById(R.id.back);
		btnBack.setOnClickListener(this);
		btnWrite = (Button) findViewById(R.id.write);
		btnWrite.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.sure);
		btnSure.setOnClickListener(this);
		firstLayout = (RelativeLayout) findViewById(R.id.first_layout);
		initCostSecondView();
		proSecondLayout = (RelativeLayout) findViewById(R.id.project_second_layout);
		initProSecondView();
	}

	private EditText etCostTitle, etCostMoney, etCostMemo;
	private TextView etCostTime;

	private void initCostSecondView() {
		timeLayout = (RelativeLayout) findViewById(R.id.time_layout);
		timeLayout.setOnClickListener(this);
		secondLayout = (RelativeLayout) findViewById(R.id.second_layout);
		etCostTitle = (EditText) findViewById(R.id.cost_title_et);
		etCostMoney = (EditText) findViewById(R.id.cost_money_et);
		etCostTime = (TextView) findViewById(R.id.cost_time_et);
		etCostTime.setOnClickListener(this);
		etCostMemo = (EditText) findViewById(R.id.cost_memo_et);
	}

	private TextView etProStartTime, etProEndTime, etProType;
	private EditText etProName, etProImport, etProMemo;

	private void initProSecondView() {
		etProStartTime = (TextView) findViewById(R.id.pro_start_time_et);
		etProStartTime.setOnClickListener(this);
		etProEndTime = (TextView) findViewById(R.id.pro_end_time_et);
		etProEndTime.setOnClickListener(this);
		etProType = (TextView) findViewById(R.id.pro_select_category_et);
		etProType.setOnClickListener(this);
		etProName = (EditText) findViewById(R.id.pro_name_et);
		etProImport = (EditText) findViewById(R.id.pro_person_name_et);
		etProMemo = (EditText) findViewById(R.id.pro_memo_et);
	}

	DatePickerDialog.OnDateSetListener onDateSetListener1 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			etProStartTime
					.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		}
	};
	DatePickerDialog.OnDateSetListener onDateSetListener2 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			etProEndTime
					.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		}
	};
	DatePickerDialog.OnDateSetListener onDateSetListener3 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			etCostTime
					.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		}
	};
	DatePickerDialog.OnDateSetListener onDateSetListener4 = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			etCostTime
					.setText((year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
		}
	};

	public void hideInputMethod() {
	}

	@Override
	public void onBackPressed() {
		if (secondLayout.getVisibility() == View.VISIBLE) {
			tvTitle.setText("费用开支");
			isCost = false;
			hideInputMethod();
			secondLayout.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.VISIBLE);
		} else if (proSecondLayout.getVisibility() == View.VISIBLE) {
			proSecondLayout.setVisibility(View.GONE);
			btnSure.setVisibility(View.GONE);
			firstLayout.setVisibility(View.VISIBLE);
			btnWrite.setVisibility(View.VISIBLE);
			hideInputMethod();
			tvTitle.setText("事情安排");
		} else {
			finish();
		}
	}
	private String[] items;
	private int whichItem = 0;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.time_layout:
			new DatePickerDialog(this, onDateSetListener4, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.cost_time_et:
			new DatePickerDialog(this, onDateSetListener3, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.pro_start_time_et:
			new DatePickerDialog(this, onDateSetListener1, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.pro_end_time_et:
			new DatePickerDialog(this, onDateSetListener2, Calendar
					.getInstance().get(Calendar.YEAR), Calendar.getInstance()
					.get(Calendar.MONTH), Calendar.getInstance().get(
					Calendar.DAY_OF_MONTH)).show();
			break;
		case R.id.pro_select_category_et:
			items = new String[proGroup.size()+1];
			items[0] = "不限";
			for(int i = 0;i<proGroup.size();i++){
				items[i+1] = proGroup.get(i).getProName();
			}
			new AlertDialog.Builder(this).setTitle("列表框")
					.setItems(items, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							etProType.setText(items[which]);
							whichItem = which;
						}
					}).show();
			break;
		case R.id.back:
			if (secondLayout.getVisibility() == View.VISIBLE) {
				isCost = false;
				tvTitle.setText("费用开支");
				hideInputMethod();
				secondLayout.setVisibility(View.GONE);
				btnSure.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
			} else if (proSecondLayout.getVisibility() == View.VISIBLE) {
				proSecondLayout.setVisibility(View.GONE);
				btnSure.setVisibility(View.GONE);
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
				hideInputMethod();
				tvTitle.setText("事情安排");
				isPro = false;
			} else {
				finish();
			}
			break;
		case R.id.write:
			if (flag.equals(Constants.USER_FLAG.MAIN_TO_COST)) {
				tvTitle.setText(R.string.cost_spending_title);
				firstLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.GONE);
				secondLayout.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.VISIBLE);
				tvTitle.setText("添加费用开支");
				etCostMemo.setText("");
				etCostMoney.setText("");
				etCostTime.setText("");
				etCostTitle.setText("");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						InputMethodManager m = (InputMethodManager) etCostMemo
								.getContext().getSystemService(
										Context.INPUT_METHOD_SERVICE);
						m.showSoftInput(etCostMemo, 0);
					}
				}, 100);
			} else {
				tvTitle.setText(R.string.thing_schedle_title);
				firstLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.GONE);
				btnSure.setVisibility(View.VISIBLE);
				proSecondLayout.setVisibility(View.VISIBLE);
				tvTitle.setText("添加事情安排");
				etProEndTime.setText("");
				etProImport.setText("");
				etProMemo.setText("");
				etProName.setText("");
				etProStartTime.setText("");
				etProType.setText("");
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					public void run() {
						InputMethodManager m = (InputMethodManager) etProEndTime
								.getContext().getSystemService(
										Context.INPUT_METHOD_SERVICE);
						m.showSoftInput(etProEndTime, 0);
					}
				}, 100);
			}
			break;
		case R.id.sure:
			if (flag.equals(Constants.USER_FLAG.MAIN_TO_COST)) {
				if (StringUtil.isEmpty(etCostMemo.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etCostMemo.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etCostMoney.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etCostMoney.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etCostTime.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etCostTime.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etCostTitle.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etCostTitle.requestFocus();
					return;
				}
				if (isCost) {
					costEntity.setMemo(etCostMemo.getText().toString());
					costEntity.setMoney(etCostMoney.getText().toString());
					costEntity.setTime(etCostTime.getText().toString());
					costEntity.setTitle(etCostTitle.getText().toString());
				} else {
					if (expandableListView_one.isGroupExpanded(groupPos)) {
						CostSpendEntity entity = new CostSpendEntity();
						entity.setMemo(etCostMemo.getText().toString());
						entity.setMoney(etCostMoney.getText().toString());
						entity.setTime(etCostTime.getText().toString());
						entity.setTitle(etCostTitle.getText().toString());
						if (childArray.size() > 0
								&& childArray.size() > groupPos) {
							List<CostSpendEntity> entss = childArray
									.get(groupPos);
							entss.add(entity);
							int totalMoney = Integer
									.parseInt(entity.getMoney())
									+ Integer.parseInt(groupArray.get(groupPos)
											.getMoney());
							groupArray.get(groupPos).setMoney(totalMoney + "");
							tvTotalMoney.setText(totalMoney + "");

						} else {
							List<CostSpendEntity> entss = new ArrayList<CostSpendEntity>();
							entss.add(entity);
							CostSpendEntity entity1 = groupArray.get(groupPos);
							int totalMoney = Integer.parseInt(entity1
									.getMoney())
									+ Integer.parseInt(entity.getMoney());
							groupArray.get(groupPos).setMoney(totalMoney + "");
							childArray.add(0, entss);
						}
					} else {
						CostSpendEntity entity = new CostSpendEntity();
						entity.setMemo(etCostMemo.getText().toString());
						entity.setMoney(etCostMoney.getText().toString());
						entity.setTime(etCostTime.getText().toString());
						entity.setTitle(etCostTitle.getText().toString());
						groupArray.add(entity);

					}
				}
				int totalMoney = 0;
				for (int i = 0; i < groupArray.size(); i++) {
					totalMoney += Integer
							.parseInt(groupArray.get(i).getMoney());
				}
				tvTotalMoney.setText("￥" + totalMoney + "");
				expandaleAdapter.notifyDataSetChanged();
				firstLayout.setVisibility(View.VISIBLE);
				secondLayout.setVisibility(View.GONE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				SharedPreferencemanager.pushCostEntityChildToFile(childArray,
						context, localCost
								+ Constants.FTP_STATUS.COST_CHILD_TXT_NAME);
				SharedPreferencemanager
						.pushCostEntityGroupToFile(groupArray, context,
								localCost + Constants.FTP_STATUS.COST_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localCost,
								Constants.FTP_STATUS.COST_CHILD_TXT_NAME);

						FTPUtil.ftpUpload(remotePath, localCost,
								Constants.FTP_STATUS.COST_TXT_NAME);
					}
				}).start();
				isCost = false;
				tvTitle.setText("费用开支");
			} else {
				if (StringUtil.isEmpty(etProStartTime.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProStartTime.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etProEndTime.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProEndTime.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etProType.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProType.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etProName.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProName.requestFocus();
					return;
				}
				if (StringUtil.isEmpty(etProMemo.getText().toString())) {
					Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show();
					etProMemo.requestFocus();
					return;
				}
				if (isPro) {
					proEntity.setStartTime(etProStartTime.getText().toString());
					proEntity.setEndTime(etProEndTime.getText().toString());
					proEntity.setProType(etProType.getText().toString());
					proEntity.setProName(etProName.getText().toString());
					proEntity.setProImport(etProImport.getText().toString());
					proEntity.setProMemo(etProMemo.getText().toString());
				} else {
					if(whichItem != 0) {
						ProjectEntity entity = new ProjectEntity();
						entity.setStartTime(etProStartTime.getText().toString());
						entity.setEndTime(etProEndTime.getText().toString());
						entity.setProType(etProType.getText().toString());
						entity.setProName(etProName.getText().toString());
						entity.setProImport(etProImport.getText().toString());
						entity.setProMemo(etProMemo.getText().toString());
						if (proChild.size() > 0 && whichItem <proChild.size()) {
							List<ProjectEntity> entss = proChild.get(whichItem);
							entss.add(entity);
						} else {
							List<ProjectEntity> entss = new ArrayList<ProjectEntity>();
							for(int j = 0;j<whichItem-1;j++){
								proChild.add(entss);
							}
							entss = new ArrayList<ProjectEntity>();
							entss.add(entity);
							proChild.add(whichItem-1, entss);
						}

					} else{
						ProjectEntity entity = new ProjectEntity();
						entity.setStartTime(etProStartTime.getText().toString());
						entity.setEndTime(etProEndTime.getText().toString());
						entity.setProType(etProType.getText().toString());
						entity.setProName(etProName.getText().toString());
						entity.setProImport(etProImport.getText().toString());
						entity.setProMemo(etProMemo.getText().toString());
						proGroup.add(entity);

					}
				}
				
				SharedPreferencemanager.pushProjectEntityGroupToFile(proGroup,
						this, localPro
								+ Constants.FTP_STATUS.ARRAGMENG_TXT_NAME);
				SharedPreferencemanager
				.pushProjectEntityChildToFile(proChild, this, localPro
						+ Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME);
				new Thread(new Runnable() {
					@Override
					public void run() {
						FTPUtil.ftpUpload(remotePath, localPro,
								Constants.FTP_STATUS.ARRAGMENG_TXT_NAME);
						FTPUtil.ftpUpload(remotePath, localPro,
								Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME);
					}
				}).start();
				expandaleAdapter1.notifyDataSetChanged();
				firstLayout.setVisibility(View.VISIBLE);
				btnWrite.setVisibility(View.VISIBLE);
				btnSure.setVisibility(View.GONE);
				proSecondLayout.setVisibility(View.GONE);
				tvTitle.setText("事情安排");
			}
			hideInputMethod();
			break;
		}
	}

	@Override
	public void onRefresh() {
		if (flag.equals(Constants.USER_FLAG.MAIN_TO_COST)) {
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						if (FTPUtil.ftpDownload(remotePath, localCost,
								Constants.FTP_STATUS.COST_TXT_NAME)) {
							FTPUtil.ftpDownload(remotePath, localCost,
									Constants.FTP_STATUS.COST_CHILD_TXT_NAME);
							File file = new File(localCost);
							if (!file.exists()) {
								file.mkdirs();
							}
							File[] fileList = file.listFiles();
							parserFileList(fileList);
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								listView.onRefreshComplete();
							}
						});
					}
				}).start();
			}
		} else {
			if (DeviceUtil.is3GorWifi()) {
				new Thread(new Runnable() {

					@Override
					public void run() {
						if (FTPUtil.ftpDownload(remotePath, localPro,
								Constants.FTP_STATUS.ARRAGMENG_CHILD_TXT_NAME)) {
							FTPUtil.ftpDownload(remotePath, localPro,
									Constants.FTP_STATUS.ARRAGMENG_TXT_NAME);
							File file = new File(localPro);
							if (!file.exists()) {
								file.mkdirs();
							}
							File[] fileList = file.listFiles();
							parserProFileList(fileList);
						}
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								listView.onRefreshComplete();
							}
						});
					}
				}).start();
			}
		}
		listView.onRefreshComplete();
	}

}
