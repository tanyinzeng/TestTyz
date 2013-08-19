package com.example.constants;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {
	public static boolean ftpDownload(String remoteFile, String localFile,
			String textName) {
		LogUtil.log("remoteFile = " + remoteFile + " , localFile = "
				+ localFile);
		FTPClient ObjFtpCon = new FTPClient();
		try {
			ObjFtpCon.connect(Constants.FTP_STATUS.FTP_HOST, 50);
			boolean loginResult = ObjFtpCon.login(
					Constants.FTP_STATUS.FTP_USERNAME,
					Constants.FTP_STATUS.FTP_PWD);
			if (loginResult) {
				ObjFtpCon.enterLocalPassiveMode(); // important!
				ObjFtpCon.cwd(remoteFile);
				String[] strArrQuesFiles = ObjFtpCon.listNames();
				int intcnt = 0;
				boolean blnresult = false;
				// "/test1/"
				File objfile = new File(localFile);
				if (!objfile.exists())
					objfile.mkdirs();
				objfile = null;
				for (intcnt = 0; intcnt < strArrQuesFiles.length; intcnt++) {
					LogUtil.log("strArrQuesFiles = " + strArrQuesFiles[intcnt]);
					if (strArrQuesFiles[intcnt].equals(textName)) {
						objfile = new File(localFile + strArrQuesFiles[intcnt]);
						objfile.createNewFile();
						FileOutputStream objFos = new FileOutputStream(objfile);
						blnresult = ObjFtpCon.retrieveFile(
								strArrQuesFiles[intcnt], objFos);
						objFos.close();
					}
				}
				return blnresult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ObjFtpCon.logout();
			ObjFtpCon.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean ftpDownload(String remoteFile, String localFile) {
		LogUtil.log("remoteFile = " + remoteFile + " , localFile = "
				+ localFile);
		FTPClient ObjFtpCon = new FTPClient();
		try {
			ObjFtpCon.connect(Constants.FTP_STATUS.FTP_HOST, 50);
			boolean loginResult = ObjFtpCon.login(
					Constants.FTP_STATUS.FTP_USERNAME,
					Constants.FTP_STATUS.FTP_PWD);
			if (loginResult) {
				ObjFtpCon.enterLocalPassiveMode(); // important!
				ObjFtpCon.cwd(remoteFile);
				String[] strArrQuesFiles = ObjFtpCon.listNames();
				int intcnt = 0;
				boolean blnresult = false;
				// "/test1/"
				File objfile = new File(localFile);
				if (!objfile.exists())
					objfile.mkdirs();
				objfile = null;
				for (intcnt = 0; intcnt < strArrQuesFiles.length; intcnt++) {
					LogUtil.log("strArrQuesFiles = " + strArrQuesFiles[intcnt]);
					objfile = new File(localFile + strArrQuesFiles[intcnt]);
					objfile.createNewFile();
					FileOutputStream objFos = new FileOutputStream(objfile);
					blnresult = ObjFtpCon.retrieveFile(strArrQuesFiles[intcnt],
							objFos);
					objFos.close();
				}
				return blnresult;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			ObjFtpCon.logout();
			ObjFtpCon.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String ftpUpload(String remotePath, String fileNamePath,
			String fileName) {
		LogUtil.log("remotePath = " + remotePath+" filePath = "+fileNamePath+fileName);
		FTPClient ftpClient = new FTPClient();
		FileInputStream fis = null;
		String returnMessage = "0";
		try {
			ftpClient.connect(Constants.FTP_STATUS.FTP_HOST, 50);
			boolean loginResult = ftpClient.login(
					Constants.FTP_STATUS.FTP_USERNAME,
					Constants.FTP_STATUS.FTP_PWD);
			int returnCode = ftpClient.getReplyCode();
			if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
				ftpClient.makeDirectory(remotePath);
				// 设置上传目录
				ftpClient.changeWorkingDirectory(remotePath);
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				ftpClient.enterLocalPassiveMode();
				fis = new FileInputStream(fileNamePath + fileName);
				BufferedInputStream buffIn = null;
				buffIn = new BufferedInputStream(fis);
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				ftpClient.storeFile(fileName, buffIn);
				returnMessage = "1"; // 上传成功
				buffIn.close();
			} else {// 如果登录失败
				returnMessage = "0";
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("FTP客户端出错！", e);
		} finally {
			try {
				ftpClient.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("关闭FTP连接发生异常！", e);
			}
		}
		return returnMessage;
	}
}
