package com.onvif.onvifcamera_android.Onvif;

import android.util.Log;

import com.onvif.onvifcamera_android.BuildConfig;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 Device discovery class to list local accessible devices probed per UDP probe messages.
 @author th
 @date 2015-06-18
 @version 0.1
 */
public class DeviceDiscovery {
	private static final int WS_DISCOVERY_TIMEOUT = 4000;
	private static final int WS_DISCOVERY_PORT = 3702;
	private static final String WS_DISCOVERY_ADDRESS_IPv4 = "239.255.255.250";
	/**
	 * Not supported yet.
	 */
	private static final String WS_DISCOVERY_ADDRESS_IPv6 = "[FF02::C]";
	public static String WS_DISCOVERY_PROBE_MESSAGE = "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:wsa=\"http://schemas.xmlsoap.org/ws/2004/08/addressing\" xmlns:tns=\"http://schemas.xmlsoap.org/ws/2005/04/discovery\"><soap:Header><wsa:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</wsa:Action><wsa:MessageID>urn:uuid:c032cfdd-c3ca-49dc-820e-ee6696ad63e2</wsa:MessageID><wsa:To>urn:schemas-xmlsoap-org:ws:2005:04:discovery</wsa:To></soap:Header><soap:Body><tns:Probe/></soap:Body></soap:Envelope>";
	private static final Random random = new SecureRandom();

//	public static void main(String[] args) throws InterruptedException {
//		for (URL url : discoverWsDevicesAsUrls()) {
//			if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - Device discovered: " + url.toString());
//		}
//	}
//
	/**
	 Discover WS device on the local network and returns Urls
	 @return list of unique device urls
	 */
	public static Collection<URL> discoverWsDevicesAsUrls() {return discoverWsDevicesAsUrls("", "");}

	/**
	 Discover WS device on the local network with specified filter
	 @param regexpProtocol url protocol matching regexp like "^http$", might be empty ""
	 @param regexpPath url path matching regexp like "onvif", might be empty ""
	 @return list of unique device urls filtered
	 */
	@SuppressWarnings("SameParameterValue")
	private static Collection<URL> discoverWsDevicesAsUrls(String regexpProtocol, String regexpPath) {
		final Collection<URL> urls = new TreeSet<>(new Comparator<URL>() {
			public int compare(URL o1, URL o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		for (String key : discoverWsDevices()) {
			try {
				final URL url = new URL(key);
				boolean ok = true;
				if (regexpProtocol.length() > 0 && !url.getProtocol().matches(regexpProtocol))
					ok = false;
				if (regexpPath.length() > 0 && !url.getPath().matches(regexpPath))
					ok = false;
				if (ok) urls.add(url);
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - MalformedURLException: " + e.toString());
			}
		}
		return urls;
	}

	/**
	 Discover WS device on the local network
	 @return  list of unique devices access strings which might be URLs in most cases
	 */
	private static Collection<String> discoverWsDevices() {
		final Collection<String> addresses = new ConcurrentSkipListSet<>();
		final CountDownLatch serverStarted = new CountDownLatch(1);
		final CountDownLatch serverFinished = new CountDownLatch(1);
		final Collection<InetAddress> addressList = new ArrayList<>();
		try {
			final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			if(interfaces != null) {
				while (interfaces.hasMoreElements()) {
					NetworkInterface anInterface = interfaces.nextElement();
					if( ! anInterface.isLoopback() ) {
						final List<InterfaceAddress> interfaceAddresses = anInterface.getInterfaceAddresses();
						for (InterfaceAddress address : interfaceAddresses) {
							addressList.add(address.getAddress());
						}
					}
				}
			}
		} catch (SocketException e) {
			if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - SocketException: " + e.toString());
		}
		ExecutorService executorService = Executors.newCachedThreadPool();
		for (final InetAddress address : addressList) {
			Runnable runnable = new Runnable() {
				public void run() {
					try {
						final String uuid = UUID.randomUUID().toString();
						final String probe = WS_DISCOVERY_PROBE_MESSAGE.replaceAll("<wsa:MessageID>urn:uuid:.*</wsa:MessageID>", "<wsa:MessageID>urn:uuid:" + uuid + "</wsa:MessageID>");
						final int port = random.nextInt(20000) + 40000;
						@SuppressWarnings("SocketOpenedButNotSafelyClosed")
						final DatagramSocket server = new DatagramSocket(port, address);
						new Thread() {
							public void run() {
								try {
									final DatagramPacket packet = new DatagramPacket(new byte[4096], 4096);
									server.setSoTimeout(WS_DISCOVERY_TIMEOUT);
									long timerStarted = System.currentTimeMillis();
									while (System.currentTimeMillis() - timerStarted < (WS_DISCOVERY_TIMEOUT)) {
										serverStarted.countDown();
										server.receive(packet);
										final Collection<String> collection = parseSoapResponseForUrls(
												Arrays.copyOf(packet.getData(), packet.getLength()));
										for (String key : collection) {
											addresses.add(key);
										}
									}
								} catch (SocketTimeoutException ignored) {
								} catch (Exception e) {
									if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - Exception: " + e.toString());
								} finally {
									serverFinished.countDown();
									server.close();
								}
							}
						}.start();
						try {
							serverStarted.await(1000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e) {
							if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - InterruptedException: " + e.toString());
						}
						if (address instanceof Inet4Address) {
							server.send(new DatagramPacket(probe.getBytes(), probe.length(), InetAddress.getByName(WS_DISCOVERY_ADDRESS_IPv4), WS_DISCOVERY_PORT));
						} else {
							server.send(new DatagramPacket(probe.getBytes(), probe.length(), InetAddress.getByName(WS_DISCOVERY_ADDRESS_IPv6), WS_DISCOVERY_PORT));
						}
					} catch (Exception e) {
						if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - Exception: " + e.toString());
					}
					try {
						serverFinished.await((WS_DISCOVERY_TIMEOUT), TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - InterruptedException: " + e.toString());
					}
				}
			};
			executorService.submit(runnable);
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(WS_DISCOVERY_TIMEOUT + 2000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			if (BuildConfig.DEBUG) Log.d("OnvifTest", "DeviceDiscovery - InterruptedException: " + e.toString());
		}
		return addresses;
	}

	private static Collection<String> parseSoapResponseForUrls(byte[] data) {
		String searchString = new String(data);

		final Collection<String> urls = new ArrayList<>();

		// Get web service address
		int startIndex = searchString.indexOf("XAddrs>");
		int endIndex;
		if (startIndex != -1) {
			startIndex += 7;
			endIndex = searchString.indexOf("</",startIndex);
			if (endIndex != -1) {
				urls.add(searchString.substring(startIndex,endIndex));
			}
		}
		return urls;
	}
}
