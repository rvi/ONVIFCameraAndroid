package com.onvif.onvifcamera_android.Onvif;

public class OnvifMediaProfiles {

	private String profileToken = "unknown";
	private String profileName = "unknown";

	public String videoSourceConfigToken =  "unknown";
	private String videoSourceConfigName = "unknown";
	private String videoSourceToken =  "unknown";
	public int videoSourceHeight = 0;
	public int videoSourceWidth = 0;
	private int videoSourceY = 0;
	private int videoSourceX = 0;

	private String audioSourceConfigToken =  "unknown";
	private String audioSourceConfigName = "unknown";
	private String audioSourceToken =  "unknown";

	private String videoEncoderConfigToken = "unknown";
	private String videoEncoderConfigName = "unknown";
	private String videoEncoderEndcoding = "unknown";
	public int videoEncoderWidth = 0;
	public int videoEncoderHeight = 0;
	private int videoEncoderQuality = 0;
	private int videoEncoderFrameRateLimit = 0;
	private int videoEncoderFrameRateInterval = 0;
	private int videoEncoderBitrateLimit = 0;
	private String videoEncoderMulticastAddr = "unknown";
	private int videoEncoderMulticastPort = 0;
	private int videoEncoderMulticastTTL = 0;
	private boolean videoEncoderMulticastAutoStart = false;
	private String videoEncoderSessionTimeout = "unknown";

	private String audioEncoderConfigToken = "unknown";
	private String audioEncoderConfigName = "unknown";
	private String audioEncoderEndcoding = "unknown";
	private int audioEncoderBitrate = 0;
	private int audioEncoderSampleRate = 0;
	private String audioEncoderMulticastAddr = "unknown";
	private int audioEncoderMulticastPort = 0;
	private int audioEncoderMulticastTTL = 0;
	private boolean audioEncoderMulticastAutoStart = false;
	private String audioEncoderSessionTimeout = "unknown";

	public String ptzConfigToken =  "unknown";
	private String ptzConfigName = "unknown";
	public String ptzNodeToken =  "unknown";

	@SuppressWarnings("SameReturnValue")
	public static String getProfilesCommand() {
		return "<GetProfiles xmlns=\"http://www.onvif.org/ver10/media/wsdl\"/>";
	}

	public static boolean parseProfilesResponse(String response, OnvifMediaProfiles parsed) {
		try {
			OnvifResponseParser.lastIndex = 0; // Start from beginning of response
			parsed.profileToken = OnvifResponseParser.parseOnvifString("Profiles token=\"", "\">", response);
			parsed.profileName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.videoSourceConfigToken = OnvifResponseParser.parseOnvifString("VideoSourceConfiguration token=\"", "\">", response);
			parsed.videoSourceConfigName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.videoSourceToken = OnvifResponseParser.parseOnvifString("SourceToken>", "</tt", response);
			parsed.videoSourceHeight = OnvifResponseParser.parseOnvifInt("height=\"", "\"", response);
			parsed.videoSourceWidth = OnvifResponseParser.parseOnvifInt("width=\"", "\"", response);
			parsed.videoSourceY = OnvifResponseParser.parseOnvifInt("y=\"", "\"", response);
			parsed.videoSourceX = OnvifResponseParser.parseOnvifInt("x=\"", "\"", response);
			parsed.audioSourceConfigToken = OnvifResponseParser.parseOnvifString("AudioSourceConfiguration token=\"", "\">", response);
			parsed.audioSourceConfigName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.audioSourceToken = OnvifResponseParser.parseOnvifString("SourceToken>", "</tt", response);
			parsed.videoEncoderConfigToken = OnvifResponseParser.parseOnvifString("VideoEncoderConfiguration token=\"", "\">", response);
			parsed.videoEncoderConfigName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.videoEncoderEndcoding = OnvifResponseParser.parseOnvifString("Encoding>", "</tt", response);
			parsed.videoEncoderWidth = OnvifResponseParser.parseOnvifInt("Width>", "</tt", response);
			parsed.videoEncoderHeight = OnvifResponseParser.parseOnvifInt("Height>", "</tt", response);
			parsed.videoEncoderQuality = OnvifResponseParser.parseOnvifInt("Quality>", "</tt", response);
			parsed.videoEncoderFrameRateLimit = OnvifResponseParser.parseOnvifInt("FrameRateLimit>", "</tt", response);
			parsed.videoEncoderFrameRateInterval = OnvifResponseParser.parseOnvifInt("EncodingInterval>", "</tt", response);
			parsed.videoEncoderBitrateLimit = OnvifResponseParser.parseOnvifInt("BitrateLimit>", "</tt", response);
			parsed.videoEncoderMulticastAddr = OnvifResponseParser.parseOnvifString("IPv4Address>", "</tt", response);
			parsed.videoEncoderMulticastPort = OnvifResponseParser.parseOnvifInt("Port>", "</tt", response);
			parsed.videoEncoderMulticastTTL = OnvifResponseParser.parseOnvifInt("TTL>", "</tt", response);
			parsed.videoEncoderMulticastAutoStart = OnvifResponseParser.parseOnvifBoolean("AutoStart", "</tt", response);
			parsed.videoEncoderSessionTimeout = OnvifResponseParser.parseOnvifString("SessionTimeout>", "</tt", response);
			parsed.audioEncoderConfigToken = OnvifResponseParser.parseOnvifString("AudioEncoderConfiguration token=\"", "\">", response);
			parsed.audioEncoderConfigName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.audioEncoderEndcoding = OnvifResponseParser.parseOnvifString("Encoding>", "</tt", response);
			parsed.audioEncoderBitrate = OnvifResponseParser.parseOnvifInt("Bitrate>", "</tt", response);
			parsed.audioEncoderSampleRate = OnvifResponseParser.parseOnvifInt("SampleRate>", "</tt", response);
			parsed.audioEncoderMulticastAddr = OnvifResponseParser.parseOnvifString("IPv4Address>", "</tt", response);
			parsed.audioEncoderMulticastPort = OnvifResponseParser.parseOnvifInt("Port>", "</tt", response);
			parsed.audioEncoderMulticastTTL = OnvifResponseParser.parseOnvifInt("TTL>", "</tt", response);
			parsed.audioEncoderMulticastAutoStart = OnvifResponseParser.parseOnvifBoolean("AutoStart", "</tt", response);
			parsed.audioEncoderSessionTimeout = OnvifResponseParser.parseOnvifString("SessionTimeout>", "</tt", response);
			parsed.ptzConfigToken = OnvifResponseParser.parseOnvifString("AudioEncoderConfiguration token=\"", "\">", response);
			parsed.ptzConfigName = OnvifResponseParser.parseOnvifString("Name>", "</tt", response);
			parsed.ptzNodeToken = OnvifResponseParser.parseOnvifString("NodeToken>", "</tt", response);
			return true;
		} catch (StringIndexOutOfBoundsException ignore) {
			return false;
		}
	}

	public static String profilesToString(OnvifMediaProfiles parsed) {
		String parsedResult = "Profiles:\n";
		parsedResult += "Token: " + parsed.profileToken + " ";
		parsedResult += "Name: " + parsed.profileName + "\n";
		parsedResult += "Video source:\n";
		parsedResult += "Config token: " + parsed.videoSourceConfigToken + " ";
		parsedResult += "Name: " + parsed.videoSourceConfigName + "\n";
		parsedResult += "Token: " + parsed.videoSourceToken + "\n";
		parsedResult += "Height: " + parsed.videoSourceHeight + " ";
		parsedResult += "Width: " + parsed.videoSourceWidth + " ";
		parsedResult += "Ypos: " + parsed.videoSourceY + " ";
		parsedResult += "Xpos: " + parsed.videoSourceX + "\n";
		parsedResult += "Audio source:\n";
		parsedResult += "Config token: " + parsed.audioSourceConfigToken + " ";
		parsedResult += "Name: " + parsed.audioSourceConfigName + "\n";
		parsedResult += "Token: " + parsed.audioSourceToken + "\n";
		parsedResult += "Video encoder:\n";
		parsedResult += "Config token: " + parsed.videoEncoderConfigToken + "\n";
		parsedResult += "Name: " + parsed.videoEncoderConfigName + "\n";
		parsedResult += "Encoding: " + parsed.videoEncoderEndcoding + " ";
		parsedResult += "Width: " + parsed.videoEncoderWidth + " ";
		parsedResult += "Height: " + parsed.videoEncoderHeight + " ";
		parsedResult += "Quality: " + parsed.videoEncoderQuality + "\n";
		parsedResult += "FrameRate: " + parsed.videoEncoderFrameRateLimit + " ";
		parsedResult += "Interval: " + parsed.videoEncoderFrameRateInterval + " ";
		parsedResult += "Bitrate: " + parsed.videoEncoderBitrateLimit + "\n";
		parsedResult += "Multicast: " + parsed.videoEncoderMulticastAddr + ":";
		parsedResult += parsed.videoEncoderMulticastPort + " ";
		parsedResult += "TTL: " + parsed.videoEncoderMulticastTTL + "\n";
		parsedResult += "Autostart: " + parsed.videoEncoderMulticastAutoStart + " ";
		parsedResult += "Timeout: " + parsed.videoEncoderSessionTimeout + "\n";
		parsedResult += "Audio encoder:\n";
		parsedResult += "Config token: " + parsed.audioEncoderConfigToken + " ";
		parsedResult += "Name: " + parsed.audioEncoderConfigName + "\n";
		parsedResult += "Encoding: " + parsed.audioEncoderEndcoding + " ";
		parsedResult += "Bitrate: " + parsed.audioEncoderBitrate + " ";
		parsedResult += "Samplerate: " + parsed.audioEncoderSampleRate + "\n";
		parsedResult += "Multicast: " + parsed.audioEncoderMulticastAddr + ":";
		parsedResult += parsed.audioEncoderMulticastPort + " ";
		parsedResult += "TTL: " + parsed.audioEncoderMulticastTTL + "\n";
		parsedResult += "Autostart: " + parsed.audioEncoderMulticastAutoStart + " ";
		parsedResult += "Timeout: " + parsed.audioEncoderSessionTimeout + "\n";
		parsedResult += "PTZ:\n";
		parsedResult += "Config token: " + parsed.ptzConfigToken + "\n";
		parsedResult += "Name: " + parsed.ptzConfigName + "\n";
		parsedResult += "Node: " + parsed.ptzNodeToken + "\n";
		return parsedResult;
	}
}
