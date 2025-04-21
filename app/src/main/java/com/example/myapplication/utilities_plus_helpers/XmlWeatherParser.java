package com.example.myapplication.utilities_plus_helpers;

public class XmlWeatherParser {

    // Tässä ilmatieteen laitokselta saatu vastaus xml-muodossa siivotaan, jotta saadaan halutut tiedot

    public static String parse(String xml, String tagName) {
        try {
            int tagStart = xml.indexOf("<BsWfs:ParameterName>" + tagName + "</BsWfs:ParameterName>");
            if (tagStart == -1) return "-";
            String openTag = "<BsWfs:ParameterValue>";
            int valueTagStart = xml.indexOf(openTag, tagStart);
            int valueTagEnd = xml.indexOf("</BsWfs:ParameterValue>", tagStart);
            return xml.substring(valueTagStart + openTag.length(), valueTagEnd).trim();
        } catch (Exception e) {
            return "-";
        }
    }
}
