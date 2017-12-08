package com.coremobile.coreyhealth.messaging;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Nitij Katiyar
 *
 */
public class CMN_JSONUtils {
	Context activity;

	public CMN_JSONUtils(Context activity) {
		this.activity = activity;
	}

	public String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.getMessage();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.getMessage();
			}
		}
		return sb.toString();
	}
}
