package rahul.jagtap.dmas.utils;

import android.text.Editable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

import java.lang.reflect.Field;
import java.util.Locale;

public class CustomTagHandler implements Html.TagHandler {
    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("span")) {
            if (opening) {
                // Start processing the tag
                handleStartTag(output, xmlReader);
            } else {
                // End processing the tag
                handleEndTag(output);
            }
        }
    }

    private void handleStartTag(Editable output, XMLReader xmlReader) {
        String style = getAttributeValue("style", xmlReader);
        if (style != null && style.toLowerCase(Locale.ROOT).contains("font-size")) {
            String fontSize = extractFontSize(style);
            if (fontSize != null) {
                // Apply the relative font size based on the extracted value
                float relativeSize = parseFontSize(fontSize);
                if (relativeSize > 0) {
                    output.setSpan(new RelativeSizeSpan(relativeSize), output.length(), output.length(), Editable.SPAN_MARK_MARK);
                }
            }
        }
    }

    private void handleEndTag(Editable output) {
        Object span = getLastSpan(output, RelativeSizeSpan.class);
        if (span != null) {
            int where = output.getSpanStart(span);
            output.setSpan(span, where, output.length(), Editable.SPAN_EXCLUSIVE_EXCLUSIVE);
            output.removeSpan(span);
        }
    }

    private String getAttributeValue(String attribute, XMLReader xmlReader) {
        try {
            // Use reflection to access private fields in the XMLReader
            Field elementField = xmlReader.getClass().getDeclaredField("theNewElement");
            elementField.setAccessible(true);
            Object element = elementField.get(xmlReader);
            if (element != null) {
                Field attsField = element.getClass().getDeclaredField("theAtts");
                attsField.setAccessible(true);
                Object atts = attsField.get(element);
                Field dataField = atts.getClass().getDeclaredField("data");
                dataField.setAccessible(true);
                String[] data = (String[]) dataField.get(atts);
                for (int i = 0; i < data.length; i++) {
                    if (data[i].equals(attribute)) {
                        return data[i + 1];
                    }
                }
            }
        } catch (Exception e) {
            Log.e("TagHandler", "Error getting attribute value: " + attribute, e);
        }
        return null;
    }

    private String extractFontSize(String style) {
        // Extract the font-size from the style attribute (e.g., "font-size:20px;")
        for (String rule : style.split(";")) {
            if (rule.trim().startsWith("font-size")) {
                return rule.split(":")[1].trim();
            }
        }
        return null;
    }

    private float parseFontSize(String fontSize) {
        try {
            // Remove 'px' and convert the string to float (assuming px units)
            fontSize = fontSize.replace("px", "").trim();
            float size = Float.parseFloat(fontSize);
            return size / 16.0f;  // Assuming 16px is the default base font size
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private <T> Object getLastSpan(Editable text, Class<T> kind) {
        Object[] spans = text.getSpans(0, text.length(), kind);
        if (spans.length == 0) {
            return null;
        } else {
            return spans[spans.length - 1];
        }
    }
}