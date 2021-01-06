package com.rvirin.onvif.onvifcamera

/**
 * Created by remy on 09/03/2018.
 */
object OnvifXMLBuilder {

    /**
     * The header for SOAP 1.2 with digest authentication
     */
    val soapHeader: String
        get() {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<soap:Envelope " +
                    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                    "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                    "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" >" +
                    "<soap:Body>"
        }

    val envelopeEnd: String
        get() = "</soap:Body></soap:Envelope>"

}
