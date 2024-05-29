package com.devomate.videoedit.library;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface VideoEditor {

    void processVideo(InputStream videoInput, OutputStream videoOutput, Map<EditAction, EditActionValue> edits, Map<String, InputStream> additionalInputs) throws IOException;

    public enum EditAction {
        CUT_VIDEO,
        ADD_AUDIO,
        ADD_WATERMARK,
        ADD_TEXT_WATERMARK,
        CONVERT_TO_LOW_RES
    }

    public class EditActionValue {
        private int intValue;

        private String stringValue;

        private int[] intRangeValue;

        public void setIntValue(int intValue) {
            this.intValue = intValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }

        public void setIntRangeValue(int[] intRangeValue) {
            this.intRangeValue = intRangeValue;
        }

        public int getIntValue() {
            return intValue;
        }

        public String getStringValue() {
            return stringValue;
        }

        public int[] getIntRangeValue() {
            return intRangeValue;
        }
    }
}
