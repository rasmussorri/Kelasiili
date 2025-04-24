package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlWeatherParser {
    // simple holder for the timestamp + value
    public static class ParameterResult {
        public final String time;
        public final String value;
        public ParameterResult(String time, String value) {
            this.time  = time;
            this.value = value;
        }
    }

    /**
     * Returns the last non‐NaN <ParameterValue> for tagName,
     * together with its preceding <Time>.
     */
    public static ParameterResult parseWithTimestamp(String xml, String tagName) {
        final String nameTag       = "<BsWfs:ParameterName>"  + tagName + "</BsWfs:ParameterName>";
        final String valueStartTag = "<BsWfs:ParameterValue>";
        final String valueEndTag   = "</BsWfs:ParameterValue>";
        final String timeStartTag  = "<BsWfs:Time>";
        final String timeEndTag    = "</BsWfs:Time>";

        int searchPos = xml.length();
        while (true) {
            // find the next‐to‐last occurrence
            int tagStart = xml.lastIndexOf(nameTag, searchPos);
            if (tagStart < 0) break;

            // extract the value immediately after it
            int vs = xml.indexOf(valueStartTag, tagStart);
            int ve = xml.indexOf(valueEndTag, vs);
            if (vs < 0 || ve < 0) break;

            String val = xml.substring(vs + valueStartTag.length(), ve).trim();
            // if it’s a real number, grab its timestamp and return
            if (!"NaN".equals(val) && !val.isEmpty() && !"-".equals(val)) {
                // find the closest <Time> before this <ParameterName>
                int ts = xml.lastIndexOf(timeStartTag, tagStart);
                int te = (ts >= 0 ? xml.indexOf(timeEndTag, ts) : -1);
                String time = "-";
                if (ts >= 0 && te >= 0) {
                    time = xml.substring(ts + timeStartTag.length(), te).trim();
                }

                String rawTime = xml.substring(ts + timeStartTag.length(), te).trim();

                // parse & convert to Helsinki time
                Instant instant = Instant.parse(rawTime);
                ZonedDateTime helsinki = instant.atZone(ZoneId.of("Europe/Helsinki"));

                // format as "DD Klo HH:MM", e.g. "23 Klo 11:00"
                DateTimeFormatter fmt = DateTimeFormatter
                        .ofPattern("dd.MM. 'Klo' HH:mm", Locale.forLanguageTag("fi"));
                String prettyTime = helsinki.format(fmt);

                Log.d("XmlWeatherParser", "Found value: " + val + " at time: " + prettyTime);
                return new ParameterResult(prettyTime, val);
            }

            // else move window back and try the next‐earlier occurrence
            searchPos = tagStart - 1;
        }

        // nothing found
        return new ParameterResult("-", "-");
    }

    public static ParameterResult parseTimeValuePair(String xml, String paramKey) {
        // 1) Extract the single observation chunk
        String obsRx =
                "(?s)<omso:PointTimeSeriesObservation.*?"
                        +   "param=" + paramKey + ".*?"
                        +   "</omso:PointTimeSeriesObservation>";
        Matcher mObs = Pattern.compile(obsRx).matcher(xml);
        if (!mObs.find()) return new ParameterResult("-", "-");
        String block = mObs.group();

        // 2) Find all <MeasurementTVP>…</MeasurementTVP> and pick the last
        Pattern tvp = Pattern.compile(
                "(?s)<wml2:MeasurementTVP>.*?"
                        +   "<wml2:time>([^<]+)</wml2:time>.*?"
                        +   "<wml2:value>([^<]+)</wml2:value>.*?"
                        +   "</wml2:MeasurementTVP>"
        );
        Matcher mTvp = tvp.matcher(block);
        String lastTime = "-", lastVal = "-";
        while (mTvp.find()) {
            lastTime = mTvp.group(1).trim();
            lastVal  = mTvp.group(2).trim();
        }

        // 3) Convert the ISO-time → Helsinki locale
        try {
            Instant inst = Instant.parse(lastTime);
            ZonedDateTime hel = inst.atZone(ZoneId.of("Europe/Helsinki"));
            lastTime = hel.format(
                    DateTimeFormatter.ofPattern("dd.MM. 'Klo' HH:mm", Locale.forLanguageTag("fi"))
            );
        } catch (Exception ignored) {}

        return new ParameterResult(lastTime, lastVal);
    }
}
