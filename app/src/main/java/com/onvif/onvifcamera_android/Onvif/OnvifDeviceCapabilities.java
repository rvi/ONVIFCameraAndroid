package com.onvif.onvifcamera_android.Onvif;

/**
 * Created from https://www.onvif.org/ver10/device/wsdl/devicemgmt.wsdl
 *
 * GetCapabilities
 * Description:
 * Any endpoint can ask for the capabilities of a device using the capability exchange request
 * response operation. The device shall indicate all its ONVIF compliant capabilities through the
 * GetCapabilities command. The capability list includes references to the addresses (XAddr) of
 * the service implementing the interface operations in the category. Apart from the addresses,
 * the capabilities only reflect optional functions.
 *
 * Input:
 * [GetCapabilities]
 *  Category - optional, unbounded; [CapabilityCategory]
 *      List of categories to retrieve capability information on.
 *      - enum { 'All', 'Analytics', 'Device', 'Events', 'Imaging', 'Media', 'PTZ' }
 * Output:
 * [GetCapabilitiesResponse]
 *  Capabilities [Capabilities] - Capability information.
 *      Analytics - optional; [AnalyticsCapabilities] - Analytics capabilities
 *          XAddr [anyURI] - Analytics service URI.
 *          RuleSupport [boolean] - Indicates whether or not rules are supported.
 *          AnalyticsModuleSupport [boolean] - Indicates whether or not modules are supported.
 *      Device - optional; [DeviceCapabilities] - Device capabilities
 *          XAddr [anyURI] - Device service URI.
 *          Network - optional; [NetworkCapabilities] - Network capabilities.
 *              IPFilter - optional; [boolean] - Indicates whether or not IP filtering is supported.
 *              ZeroConfiguration - optional; [boolean] - Indicates whether or not zeroconf is supported.
 *              IPVersion6 - optional; [boolean] - indicates whether or not IPv6 is supported.
 *              DynDNS - optional; [boolean] - indicates whether or not is supported.
 *              Extension - optional; [NetworkCapabilitiesExtension]
 *                  Dot11Configuration - optional; [boolean]
 *                  Extension - optional; [NetworkCapabilitiesExtension2]
 *          System - optional; [SystemCapabilities] - System capabilities.
 *              DiscoveryResolve [boolean] - Indicates whether or not WS Discovery resolve requests are supported.
 *              DiscoveryBye [boolean] - Indicates whether or not WS-Discovery Bye is supported.
 *              RemoteDiscovery [boolean] - Indicates whether or not remote discovery is supported.
 *              SystemBackup [boolean] - Indicates whether or not system backup is supported.
 *              SystemLogging [boolean] - Indicates whether or not system logging is supported.
 *              FirmwareUpgrade [boolean] - Indicates whether or not firmware upgrade is supported.
 *              SupportedVersions - unbounded; [OnvifVersion] - Indicates supported ONVIF version(s).
 *                  Major [int] - Major version number.
 *                  Minor [int] - Two digit minor version number. If major version number is less than "16", X.0.1 maps to "01" and X.2.1 maps to "21" where X stands for Major version number. Otherwise, minor number is month of release, such as "06" for June.
 *          Extension - optional; [SystemCapabilitiesExtension]
 *              HttpFirmwareUpgrade - optional; [boolean]
 *              HttpSystemBackup - optional; [boolean]
 *              HttpSystemLogging - optional; [boolean]
 *              HttpSupportInformation - optional; [boolean]
 *              Extension - optional; [SystemCapabilitiesExtension2]
 *          IO - optional; [IOCapabilities] - I/O capabilities.
 *              InputConnectors - optional; [int] - Number of input connectors.
 *              RelayOutputs - optional; [int] - Number of relay outputs.
 *              Extension - optional; [IOCapabilitiesExtension]
 *                  Auxiliary - optional; [boolean]
 *                  AuxiliaryCommands - optional, unbounded; [AuxiliaryData]
 *                  Extension [IOCapabilitiesExtension2]
 *          Security - optional; [SecurityCapabilities] - Security capabilities.
 *              TLS1.1 [boolean] - Indicates whether or not TLS 1.1 is supported.
 *              TLS1.2 [boolean] - Indicates whether or not TLS 1.2 is supported.
 *              OnboardKeyGeneration [boolean] - Indicates whether or not onboard key generation is supported.
 *              AccessPolicyConfig [boolean] - Indicates whether or not access policy configuration is supported.
 *              X.509Token [boolean] - Indicates whether or not WS-Security X.509 token is supported.
 *              SAMLToken [boolean] - Indicates whether or not WS-Security SAML token is supported.
 *              KerberosToken [boolean] - Indicates whether or not WS-Security Kerberos token is supported.
 *              RELToken [boolean] - Indicates whether or not WS-Security REL token is supported.
 *              Extension - optional; [SecurityCapabilitiesExtension]
 *                  TLS1.0 [boolean]
 *                  Extension - optional; [SecurityCapabilitiesExtension2]
 *                      Dot1X [boolean]
 *                      SupportedEAPMethod - optional, unbounded; [int] - EAP Methods supported by the device. The int values refer to the IANA EAP Registry.
 *                      RemoteUserHandling [boolean]
 *          Extension - optional; [DeviceCapabilitiesExtension]
 *      Events - optional; [EventCapabilities] - Event capabilities
 *          XAddr [anyURI] - Event service URI.
 *          WSSubscriptionPolicySupport [boolean] - Indicates whether or not WS Subscription policy is supported.
 *          WSPullPointSupport [boolean] - Indicates whether or not WS Pull Point is supported.
 *          WSPausableSubscriptionManagerInterfaceSupport [boolean] - Indicates whether or not WS Pausable Subscription Manager Interface is supported
 *      Imaging - optional; [ImagingCapabilities] - Imaging capabilities
 *          XAddr [anyURI] - Imaging service URI.
 *      Media - optional; [MediaCapabilities] - Media capabilities
 *          XAddr [anyURI] - Media service URI.
 *          StreamingCapabilities [RealTimeStreamingCapabilities] - Streaming capabilities.
 *              RTPMulticast - optional; [boolean] - Indicates whether or not RTP multicast is supported.
 *              RTP_TCP - optional; [boolean] - Indicates whether or not RTP over TCP is supported.
 *              RTP_RTSP_TCP - optional; [boolean] - Indicates whether or not RTP/RTSP/TCP is supported.
 *              Extension - optional; [RealTimeStreamingCapabilitiesExtension]
 *          Extension - optional; [MediaCapabilitiesExtension]
 *              ProfileCapabilities [ProfileCapabilities]
 *                  MaximumNumberOfProfiles [int] - Maximum number of profiles.
 *      PTZ - optional; [PTZCapabilities] - PTZ capabilities
 *          XAddr [anyURI] - PTZ service URI.
 *      Extension - optional; [CapabilitiesExtension]
 *          DeviceIO - optional; [DeviceIOCapabilities]
 *              XAddr [anyURI]
 *              VideoSources [int]
 *              VideoOutputs [int]
 *              AudioSources [int]
 *              AudioOutputs [int]
 *              RelayOutputs [int]
 *          Display - optional; [DisplayCapabilities]
 *              XAddr [anyURI]
 *              FixedLayout [boolean] - Indication that the SetLayout command supports only predefined layouts.
 *          Recording - optional; [RecordingCapabilities]
 *              XAddr [anyURI]
 *              ReceiverSource [boolean]
 *              MediaProfileSource [boolean]
 *              DynamicRecordings [boolean]
 *              DynamicTracks [boolean]
 *              MaxStringLength [int]
 *          Search - optional; [SearchCapabilities]
 *              XAddr [anyURI]
 *              MetadataSearch [boolean]
 *          Replay - optional; [ReplayCapabilities]
 *              XAddr [anyURI] - The address of the replay service.
 *          Receiver - optional; [ReceiverCapabilities]
 *              XAddr [anyURI] - The address of the receiver service.
 *              RTP_Multicast [boolean] - Indicates whether the device can receive RTP multicast streams.
 *              RTP_TCP [boolean] - Indicates whether the device can receive RTP/TCP streams
 *              RTP_RTSP_TCP [boolean] - Indicates whether the device can receive RTP/RTSP/TCP streams.
 *              SupportedReceivers [int] - The maximum number of receivers supported by the device.
 *              MaximumRTSPURILength [int] - The maximum allowed length for RTSP URIs.
 *          AnalyticsDevice - optional; [AnalyticsDeviceCapabilities]
 *              XAddr [anyURI]
 *              RuleSupport - optional; [boolean] - Obsolete property.
 *              Extension - optional; [AnalyticsDeviceExtension]
 *          Extensions - optional; [CapabilitiesExtension2]
 */

public class OnvifDeviceCapabilities {
	private String device_Xaddr = "unknown";
	private boolean media_RTPMultiCast = false;
	private boolean media_RTP_TCP = false;
	private boolean media_RTP_RTSP_TCP = false;
	private boolean ptz_hasPtz = false;
	private int deviceio_videoSources = 0;
	private int deviceio_videoOutputs = 0;
	private int deviceio_audioSources = 0;
	private int deviceio_audioOutputs = 0;
	private int deviceio_relayOutputs = 0;

	@SuppressWarnings("SameReturnValue")
	public static String getCapabilitiesCommand() {
		return "<GetCapabilities xmlns=\"http://www.onvif.org/ver10/device/wsdl\"/>";
	}

	public static boolean parseCapabilitiesResponse(String response, OnvifDeviceCapabilities parsed) {
		try {
			OnvifResponseParser.lastIndex = 0; // Start from beginning of response
			parsed.device_Xaddr = OnvifResponseParser.parseOnvifString("XAddr>", "</tt", response);
			parsed.media_RTPMultiCast = OnvifResponseParser.parseOnvifBoolean("icast>", "</tt", response);
			parsed.media_RTP_TCP = OnvifResponseParser.parseOnvifBoolean("TP_TCP>", "</tt", response);
			parsed.media_RTP_RTSP_TCP = OnvifResponseParser.parseOnvifBoolean("SP_TCP>", "</tt", response);
			parsed.ptz_hasPtz = OnvifResponseParser.parseOnvifHasValue("t:PTZ>", response);
			parsed.deviceio_videoSources = OnvifResponseParser.parseOnvifInt("VideoSources>", "</tt", response);
			parsed.deviceio_videoOutputs = OnvifResponseParser.parseOnvifInt("VideoOutputs>", "</tt", response);
			parsed.deviceio_audioSources = OnvifResponseParser.parseOnvifInt("AudioSources>", "</tt", response);
			parsed.deviceio_audioOutputs = OnvifResponseParser.parseOnvifInt("AudioOutputs>", "</tt", response);
			parsed.deviceio_relayOutputs = OnvifResponseParser.parseOnvifInt("RelayOutputs>", "</tt", response);
			return true;
		} catch (StringIndexOutOfBoundsException ignore) {
			return false;
		}
	}

	public static String capabilitiesToString(OnvifDeviceCapabilities parsed) {
		String parsedResult = "Device capabilities:\n";
		parsedResult += "Address: " + parsed.device_Xaddr + "\n";
		if (parsed.media_RTP_RTSP_TCP) {
			parsedResult += "RTP_RTSP_TCP stream available\n";
		}
		if (parsed.media_RTP_TCP) {
			parsedResult += "RTP_TCP stream available\n";
		}
		if (parsed.media_RTPMultiCast) {
			parsedResult += "RTPMultiCast stream available\n";
		}
		if (parsed.ptz_hasPtz) {
			parsedResult += "PTZ available\n";
		}
		if (parsed.deviceio_videoSources > 0) {
			parsedResult += "Video sources: " + parsed.deviceio_videoSources + "\n";
		}
		if (parsed.deviceio_videoOutputs > 0) {
			parsedResult += "Video outputs: " + parsed.deviceio_videoOutputs + "\n";
		}
		if (parsed.deviceio_audioSources > 0) {
			parsedResult += "Audio sources: " + parsed.deviceio_audioSources + "\n";
		}
		if (parsed.deviceio_audioOutputs > 0) {
			parsedResult += "Audio outputs: " + parsed.deviceio_audioOutputs + "\n";
		}
		if (parsed.deviceio_relayOutputs > 0) {
			parsedResult += "Relay outputs: " + parsed.deviceio_relayOutputs + "\n";
		}
		return parsedResult;
	}
}
