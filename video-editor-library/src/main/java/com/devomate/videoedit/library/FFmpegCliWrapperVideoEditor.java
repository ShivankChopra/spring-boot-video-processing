package com.devomate.videoedit.library;

import java.io.IOException;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class FFmpegCliWrapperVideoEditor implements VideoEditor {

    private final FFprobe ffprobe;
    private final FFmpegExecutor ffmpegExecutor;

    public FFmpegCliWrapperVideoEditor(String ffmpegPath, String ffprobePath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.ffmpegExecutor = new FFmpegExecutor(ffmpeg, this.ffprobe);
    }

    @Override
    public FFmpegProbeResult probeVideo(String videoLocation) throws IOException {
        return this.ffprobe.probe(videoLocation);
    }

    @Override
    public void cutVideo(String videoLocation, String outputLocation, int startTime, int endTime) {
        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(videoLocation);
        fb.overrideOutputFiles(true);
        fb.addOutput(outputLocation);
        fb.addExtraArgs("-ss", Integer.toString(startTime));
        fb.addExtraArgs("-to", Integer.toString(endTime));
        this.ffmpegExecutor.createJob(fb).run();
    }
}
