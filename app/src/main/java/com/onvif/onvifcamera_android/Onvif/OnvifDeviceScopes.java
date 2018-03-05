package com.onvif.onvifcamera_android.Onvif;

/**
 * Created from https://www.onvif.org/ver10/device/wsdl/devicemgmt.wsdl
 *
 * GetScopes
 * Description:
 * This operation requests the scope parameters of a device. The scope parameters are used in the
 * device discovery to match a probe message, see Section 7. The Scope parameters are of two
 * different types:
 *      Fixed
 *      Configurable
 *  Fixed scope parameters are permanent device characteristics and cannot be removed through the
 *  device management interface. The scope type is indicated in the scope list returned in the get
 *  scope parameters response. A device shall support retrieval of discovery scope parameters
 *  through the GetScopes command. As some scope parameters are mandatory, the device shall
 *  return a non-empty scope list in the response.
 *
 *  [OnvifDeviceScopes]
 *  Scopes - unbounded; [Scope]
 *      Contains a list of URI defining the device scopes. Scope parameters can be of two types:
 *      fixed and configurable. Fixed parameters can not be altered.
 *          ScopeDef [ScopeDefinition]
 *              Indicates if the scope is fixed or configurable.
 *              - enum { 'Fixed', 'Configurable' }
 *          ScopeItem [anyURI]
 *          Scope item URI.
 */

public class OnvifDeviceScopes {
	private String scopeType = "unknown";
	public String scopeName = "unknown";
	private String scopeLocation = "unknown";

	@SuppressWarnings("SameReturnValue")
	public static String getScopesCommand() {
		return "<GetScopes xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}

	public static boolean parseScopesResponse(String response, OnvifDeviceScopes parsed) {

		try {
			OnvifResponseParser.lastIndex = 0; // Start from beginning of response
			parsed.scopeType = OnvifResponseParser.parseOnvifString("/type/", "</tt", response);
			parsed.scopeName = OnvifResponseParser.parseOnvifString("/name/", "</tt", response);
			parsed.scopeLocation = OnvifResponseParser.parseOnvifString("/Country/", "</tt", response);
			return true;
		} catch (StringIndexOutOfBoundsException ignore) {
			return false;
		}
	}

	public static String scopesToString(OnvifDeviceScopes parsed) {
		return "Scopes:\n"
				+ parsed.scopeType + "\n"
				+ parsed.scopeName + "\n"
				+ parsed.scopeLocation + "\n";
	}
}
