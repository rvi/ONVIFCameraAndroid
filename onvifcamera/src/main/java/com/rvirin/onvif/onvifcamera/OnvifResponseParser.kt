package com.rvirin.onvif.onvifcamera

internal object OnvifResponseParser {

    var lastIndex: Int = 0
    private var testValue: String? = null
    private var partParsed: String? = null

    /**
     * fun parseOnvifString
     * Return String from ONVIF element
     * @param start
     * Start parsing from <String>start</String>
     * @param end
     * End parsing from <String>end</String>
     * @param search
     * String to search
     * @return
     * Found value as string
     */
    fun parseOnvifString(start: String, end: String, search: String): String {
        // search for 'start'
        // until 'end'
        // in 'search'
        if (search.contains(start)) {
            val startIndex = search.indexOf(start, lastIndex) + start.length
            partParsed = search.substring(startIndex)
            val endIndex = partParsed!!.indexOf(end)
            lastIndex = startIndex + endIndex
            testValue = partParsed!!.substring(0, endIndex)
            return testValue as String
        } else {
            return "n/a"
        }
    }
}
