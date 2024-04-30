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

    @Override
    public void addTextWatermark(String videoLocation, String outputLocation, String text) {
        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(videoLocation);
        fb.addOutput(outputLocation);
        fb.setComplexFilter(
                "drawbox=y=ih-ih/10:color=black@0.5:width=iw:height=40," +
                "drawtext=text='" + text + "'" +
                ":fontsize=24" +
                ":fontcolor=white" +
                ":x=(w-tw)/2:y=(h-th-10):box=1:boxcolor=black@0.5:boxborderw=5"
        );
        this.ffmpegExecutor.createJob(fb).run();
    }

    @Override
    public void addImgWatermark(String videoLocation, String outputLocation, String imgLocation) {
        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(videoLocation);
        fb.addInput(imgLocation);
        fb.addOutput(outputLocation);
        fb.setComplexFilter("[1]scale=iw*0.25:ih*0.25,format=rgba,colorchannelmixer=aa=0.5[wm];[0][wm]overlay=W-w-10:H-h-10");
        this.ffmpegExecutor.createJob(fb).run();
    }

    @Override
    public void addAudio(String videoLocation, String outputLocation, String audioLocation) {
        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(videoLocation);
        fb.addInput(audioLocation);
        fb
                .addOutput(outputLocation)
                .setVideoCodec("copy")
                .setAudioCodec("aac")
                .addExtraArgs("-map", "0:v:0")
                .addExtraArgs("-map", "1:a:0")
                .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL)
                .done();
        this.ffmpegExecutor.createJob(fb).run();
    }

}
