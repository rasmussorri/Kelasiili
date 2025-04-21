package com.example.myapplication.utilities_plus_helpers;

public class XmlWeatherParser {

    // Tässä ilmatieteen laitokselta saatu vastaus xml-muodossa siivotaan, jotta saadaan halutut tiedot

    public static String parse(String xml, String tagName) {
        try {
            String nameTag = "<BsWfs:ParameterName>" + tagName + "</BsWfs:ParameterName>";
            // ottaa viimeisen tunnin sään
            int tagStart = xml.lastIndexOf(nameTag);
            if (tagStart == -1) return "-";

            String valueStartTag = "<BsWfs:ParameterValue>";
            String valueEndTag = "</BsWfs:ParameterValue>";

            int valueStart = xml.indexOf(valueStartTag, tagStart);
            int valueEnd = xml.indexOf(valueEndTag, valueStart);

            if (valueStart == -1 || valueEnd == -1) return "-";

            return xml.substring(valueStart + valueStartTag.length(), valueEnd).trim();
        } catch (Exception e) {
            return "-";
        }
    }
}
