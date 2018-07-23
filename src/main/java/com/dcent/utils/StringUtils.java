package com.dcent.utils;

/**
 *
 * @author kel
 */
public final class StringUtils {
	public static String parseYoutubeUrl(String path) {
		String url = path;
		if (path.contains("v=")) {
			url = path.substring(path.lastIndexOf("v=") + 2);
		} else if (path.contains("embed")) {
			url = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf("/") + 12);
		} else if (path.contains(".be/")) {
			url = path.substring(path.lastIndexOf("/") + 1);
        } else if(path.contains("&")) {
            if(path.contains("v=")) {
            	url = path.substring(path.lastIndexOf("v=") + 2, path.lastIndexOf("&") + 1);
            } else {
            	url = path.substring(0, path.lastIndexOf("&") + 1);
            }
        }

		return url;
	}
}

