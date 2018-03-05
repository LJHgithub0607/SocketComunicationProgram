package com.example.exmsocket.util;

import android.annotation.SuppressLint;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class DateUtil {

	public static String getTimeId() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
		return sdf.format(date);
	}

	public static String getNowTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(date);
	}
	
	public static String formatTime(String src) {
		String dest = src;
		SimpleDateFormat old_sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		SimpleDateFormat new_sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date = old_sdf.parse(src);
			dest = new_sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dest;
	}

}
