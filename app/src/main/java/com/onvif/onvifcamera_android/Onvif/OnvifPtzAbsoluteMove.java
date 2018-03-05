package com.onvif.onvifcamera_android.Onvif;

public class OnvifPtzAbsoluteMove {

	@SuppressWarnings("SameParameterValue")
	public static String getAbsoluteMoveCommand(int xMove, int yMove, int zoomVal, String profileToken) {
		String contMoveCmd = "<ContinuousMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\">";
		contMoveCmd += "<ProfileToken>" + profileToken + "</ProfileToken>";
		contMoveCmd += "<Position >";
		contMoveCmd += "<PanTilt x=\"";
		contMoveCmd += Integer.toString(xMove);
		contMoveCmd += "\" y=\"";
		contMoveCmd += Integer.toString(yMove);
		contMoveCmd += "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>";
		contMoveCmd += "<Zoom x=\"";
		contMoveCmd += Integer.toString(zoomVal);
		contMoveCmd += "\" xmlns=\"http://www.onvif.org/ver10/schema\"/>";
		contMoveCmd += "</Position>";
		contMoveCmd += "</ContinuousMove>";
		return contMoveCmd;
	}
}
