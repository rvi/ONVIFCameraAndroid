package com.onvif.onvifcamera_android.Onvif;

class OnvifPtzContinuousMove {

	@SuppressWarnings({"SameParameterValue", "unused"})
	public static String getContinuousMoveCommand(int xMove, int yMove, int zoomVal, String profileToken) {
		String contMoveCmd = "<ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">";
		contMoveCmd += "<ProfileToken>" + profileToken + "</ProfileToken>";
		contMoveCmd += "<Velocity>";
		contMoveCmd += "<PanTilt x=\"";
		contMoveCmd += Integer.toString(xMove);
		contMoveCmd += "\" y=\"";
		contMoveCmd += Integer.toString(yMove);
		contMoveCmd += "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>";
		contMoveCmd += "<Zoom x=\"";
		contMoveCmd += Integer.toString(zoomVal);
		contMoveCmd += "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>";
		contMoveCmd += "</Velocity>";
		contMoveCmd += "</ContinuousMove>";
		return contMoveCmd;
	}
}
