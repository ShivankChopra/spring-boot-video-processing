package com.devomate.videoedit.library;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FFmpegCliWrapperVideoEditorTest {

    private final String FFMPEG_PATH = "/opt/homebrew/bin/ffmpeg";
    private final String FFPROBE_PATH = "/opt/homebrew/bin/ffprobe";

    @Test
    public void testCutVideo() throws IOException {
        VideoEditor ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);

        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";

        ve.cutVideo(videoLocation, outputLocation, 0, 3);

        FFmpegProbeResult result = ve.probeVideo(outputLocation);

        for (int i = 0; i < result.streams.size(); i++) {
            assertEquals(3, (long)result.streams.get(i).duration);
        }
    }

    @Test
    public void testAddTextWatermark() throws IOException {
        VideoEditor ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        ve.addTextWatermark(videoLocation, outputLocation, "Devomate");
    }

    @Test
    public void testAddImgWatermark() throws IOException {
        VideoEditor ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        final String imgLocation = System.getProperty("user.dir") + "/resources/devomate_img.webp";
        ve.addImgWatermark(videoLocation, outputLocation, imgLocation);
    }

    @Test
    public void testAddAudio() throws IOException {
        VideoEditor ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        final String audioLocation = System.getProperty("user.dir") + "/resources/test.mp3";
        ve.addAudio(videoLocation, outputLocation, audioLocation);
    }
}