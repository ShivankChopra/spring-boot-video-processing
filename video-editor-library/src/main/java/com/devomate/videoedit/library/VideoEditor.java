package com.devomate.videoedit.library;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;

import java.io.IOException;

public interface VideoEditor {

    FFmpegProbeResult probeVideo(String videoLocation) throws IOException;

    void cutVideo(String videoLocation, String outputLocation, int startTime, int endTime);
}
