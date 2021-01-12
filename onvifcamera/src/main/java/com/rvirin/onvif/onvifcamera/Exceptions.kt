package com.rvirin.onvif.onvifcamera

class OnvifUnauthorized(message: String) : Exception(message)

class OnvifInvalidResponse(message: String) : Exception(message)

class OnvifForbidden(message: String) : Exception(message)