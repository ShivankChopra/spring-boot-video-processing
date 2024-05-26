package com.devomate.videoedit.library;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.IOException;

public interface VideoEditorOld {

    FFmpegProbeResult probeVideo(String videoLocation) throws IOException;

    void cutVideo(String videoLocation, String outputLocation, int startTime, int endTime);

    void addTextWatermark(String videoLocation, String outputLocation, String text);

    void addImgWatermark(String videoLocation, String outputLocation, String imgLocation);

    void addAudio(String videoLocation, String outputLocation, String audioLocation);

    //void addAudio(InputStream videoInput, InputStream audioInput, OutputStream output);

    void convertToLowResolution(String videoLocation, String outputLocation);
}
