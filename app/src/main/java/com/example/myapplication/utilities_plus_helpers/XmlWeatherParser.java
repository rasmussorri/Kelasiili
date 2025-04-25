package com.example.myapplication.utilities_plus_helpers;

import android.util.Log;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
            int tagStart = xml.lastIndexOf(nameTag, searchPos);
            if (tagStart < 0) break;

            int vs = xml.indexOf(valueStartTag, tagStart);
            int ve = xml.indexOf(valueEndTag, vs);
            if (vs < 0 || ve < 0) break;

            String val = xml.substring(vs + valueStartTag.length(), ve).trim();
            if (!"NaN".equals(val) && !val.isEmpty() && !"-".equals(val)) {
                int ts = xml.lastIndexOf(timeStartTag, tagStart);
                int te = (ts >= 0 ? xml.indexOf(timeEndTag, ts) : -1);
                String rawTime = (ts >= 0 && te >= 0)
                        ? xml.substring(ts + timeStartTag.length(), te).trim()
                        : null;

                String prettyTime = "-";
                if (rawTime != null) {
                    try {
                        Instant instant = Instant.parse(rawTime);
                        ZonedDateTime helsinki = instant.atZone(ZoneId.of("Europe/Helsinki"));
                        prettyTime = helsinki.format(
                                DateTimeFormatter.ofPattern("dd.MM. 'Klo' HH:mm", Locale.forLanguageTag("fi"))
                        );
                    } catch (Exception ex) { /* ignore */ }
                }

                Log.d("XmlWeatherParser", "Found value: " + val + " at time: " + prettyTime);
                return new ParameterResult(prettyTime, val);
            }

            searchPos = tagStart - 1;
        }
        return new ParameterResult("-", "-");
    }

    /**
     * Returns the most recent non‐NaN value/time pair for the given
     * time‐value‐pair query.  If the very latest TVP is NaN, it
     * falls back to the next‐newest, and so on.
     */
    public static ParameterResult parseTimeValuePair(String xml, String paramKey) {
        // 1) Extract the block for this parameter
        String obsRx = "(?s)<omso:PointTimeSeriesObservation.*?"
                + "param=" + paramKey + ".*?"
                + "</omso:PointTimeSeriesObservation>";
        Matcher mObs = Pattern.compile(obsRx).matcher(xml);
        if (!mObs.find()) {
            return new ParameterResult("-", "-");
        }
        String block = mObs.group();

        // 2) Find all <MeasurementTVP>…</MeasurementTVP>
        Pattern tvp = Pattern.compile(
                "(?s)<wml2:MeasurementTVP>.*?"
                        +   "<wml2:time>([^<]+)</wml2:time>.*?"
                        +   "<wml2:value>([^<]+)</wml2:value>.*?"
                        + "</wml2:MeasurementTVP>"
        );
        Matcher mTvp = tvp.matcher(block);

        // 3) Collect into list
        List<ParameterResult> readings = new ArrayList<>();
        while (mTvp.find()) {
            String isoTime = mTvp.group(1).trim();
            String rawVal  = mTvp.group(2).trim();

            // convert ISO→Helsinki
            String prettyTime = isoTime;
            try {
                Instant inst = Instant.parse(isoTime);
                ZonedDateTime hel = inst.atZone(ZoneId.of("Europe/Helsinki"));
                prettyTime = hel.format(
                        DateTimeFormatter.ofPattern("dd.MM. 'Klo' HH:mm", Locale.forLanguageTag("fi"))
                );
            } catch (Exception ignored) {}

            readings.add(new ParameterResult(prettyTime, rawVal));
        }

        // 4) Walk backwards to find first non‐NaN, non‐"-"
        for (int i = readings.size() - 1; i >= 0; i--) {
            ParameterResult pr = readings.get(i);
            if (!"NaN".equals(pr.value) && !"-".equals(pr.value)) {
                return pr;
            }
        }

        // 5) None found
        return new ParameterResult("-", "-");
    }
}
