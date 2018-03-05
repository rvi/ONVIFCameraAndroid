package com.onvif.onvifcamera_android.Onvif;

class OnvifResponseParser {

	static int lastIndex;
	private static String testValue;
	private static String partParsed;

	/**
	 * static int parseOnvifInt
	 * Return Integer from ONVIF element
	 * @param start
	 *          Start parsing from <String>start</String>
	 * @param end
	 *          End parsing from <String>end</String>
	 * @param search
	 *          String to search
	 * @return
	 *          Found value as string
	 */
	static int parseOnvifInt(String start, String end, String search) {
		// search for 'start'
		// until 'end'
		// in 'search'
		if (search.contains(start)) {
			int startIndex = search.indexOf(start, lastIndex)+ start.length();
			partParsed = search.substring(startIndex);
			int endIndex = partParsed.indexOf(end);
			lastIndex = startIndex + endIndex;
			testValue = partParsed.substring(0,endIndex);
			try {
				return Integer.valueOf(testValue);
			} catch (NumberFormatException ignore) {}
		}
		return -1;
	}

	static String parseOnvifString(String start, String end, String search) {
		// search for 'start'
		// until 'end'
		// in 'search'
		if (search.contains(start)) {
			int startIndex = search.indexOf(start, lastIndex)+ start.length();
			partParsed = search.substring(startIndex);
			int endIndex = partParsed.indexOf(end);
			lastIndex = startIndex + endIndex;
			testValue = partParsed.substring(0,endIndex);
			return testValue;
		} else {
			return "n/a";
		}
	}

	@SuppressWarnings("SameParameterValue")
	static boolean parseOnvifBoolean(String start, String end, String search) {
		// search for 'start'
		// until 'end'
		// in 'search'
		if (search.contains(start)) {
			int startIndex = search.indexOf(start, lastIndex)+ start.length();
			partParsed = search.substring(startIndex);
			int endIndex = partParsed.indexOf(end);
			lastIndex = startIndex + endIndex;
			testValue = partParsed.substring(0,endIndex);
			if (testValue.equalsIgnoreCase("true")) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("SameParameterValue")
	static boolean parseOnvifHasValue(String value, String search) {
		return search.contains(value);
	}

	static int timeOutToMS(String timeoutString) {
		String testValue;
		int valStart;
		int valEnd;
		int timeoutHours = 0;
		int timeoutMinutes = 0;
		float timeoutSeconds = 0;

		if (!timeoutString.contains("PT")) { // No valid timeout value found
			return -1;
		}
		valStart = timeoutString.indexOf("PT") + 2;
		if (timeoutString.contains("H")) {
			valEnd = timeoutString.indexOf("H");
			testValue = timeoutString.substring(valStart, valEnd);
			try {
				timeoutHours = Integer.valueOf(testValue);
			} catch (NumberFormatException ignore) {}
			valStart = valEnd + 1;
		}

		if (timeoutString.contains("M")) {
			valEnd = timeoutString.indexOf("M");
			testValue = timeoutString.substring(valStart, valEnd);
			try {
				timeoutMinutes = Integer.valueOf(testValue);
			} catch (NumberFormatException ignore) {}
			valStart = valEnd + 1;
		}

		if (timeoutString.contains("S")) {
			valEnd = timeoutString.indexOf("S");
			testValue = timeoutString.substring(valStart, valEnd);
			try {
				timeoutSeconds = Float.valueOf(testValue);
			} catch (NumberFormatException ignore) {}
		}

		float timeoutMS = ((timeoutHours * 3600) + (timeoutMinutes * 60) + timeoutSeconds) * 1000;
		return (int) timeoutMS;
	}
}
