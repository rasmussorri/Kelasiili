package com.example.myapplication.core.util;

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
    // luokka johon tallentuu aikaleima
    public static class ParameterResult {
        public final String time;
        public final String value;
        public ParameterResult(String time, String value) {
            this.time  = time;
            this.value = value;
        }
    }

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


    public static ParameterResult parseTimeValuePair(String xml, String paramKey) {
        // Etsitään ensin PointTimeSeriesObservation tagi, joka sisältää halutun paramKey:n
        String obsRx = "(?s)<omso:PointTimeSeriesObservation.*?"
                + "param=" + paramKey + ".*?"
                + "</omso:PointTimeSeriesObservation>";
        Matcher mObs = Pattern.compile(obsRx).matcher(xml);
        if (!mObs.find()) {
            return new ParameterResult("-", "-");
        }
        String block = mObs.group();

        // Etsitään kaikki tagit, joissa voisi olla arvo
        Pattern tvp = Pattern.compile(
                "(?s)<wml2:MeasurementTVP>.*?"
                        +   "<wml2:time>([^<]+)</wml2:time>.*?"
                        +   "<wml2:value>([^<]+)</wml2:value>.*?"
                        + "</wml2:MeasurementTVP>"
        );
        Matcher mTvp = tvp.matcher(block);

        // Kerätään aikaleima ja arvo listaan
        List<ParameterResult> readings = new ArrayList<>();
        while (mTvp.find()) {
            String isoTime = mTvp.group(1).trim();
            String rawVal  = mTvp.group(2).trim();

            // Muutetaan Helsingin aikavyöhykkeelle
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

        // Lähdetään purkamaan listaa taaksepäin, jotta löytyisi ensimmäinen ei NaN arvo
        for (int i = readings.size() - 1; i >= 0; i--) {
            ParameterResult pr = readings.get(i);
            if (!"NaN".equals(pr.value) && !"-".equals(pr.value)) {
                return pr;
            }
        }

        // Ei löydetty arvoja palautetaan "-"
        return new ParameterResult("-", "-");
    }
}
