package com.devomate.videoedit.library;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class FFmpegVideoEditor implements VideoEditor {

    @Override
    public OutputStream processVideo(InputStream videoInput, Map<String, InputStream> additionalInputs, Map<EditAction, EditActionValue> edits) {
        return null;
    }
}
