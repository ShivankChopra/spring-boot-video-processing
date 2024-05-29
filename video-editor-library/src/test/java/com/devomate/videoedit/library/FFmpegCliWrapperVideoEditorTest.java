package com.devomate.videoedit.library;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FFmpegCliWrapperVideoEditorTest {

    private final String FFMPEG_PATH = "/opt/homebrew/bin/ffmpeg";
    private final String FFPROBE_PATH = "/opt/homebrew/bin/ffprobe";

    @Test
    public void testCutVideo() throws IOException {
        VideoEditor ve = new FFmpegVideoEditor(FFMPEG_PATH, FFPROBE_PATH);

        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";

        try (FileInputStream videoStream = new FileInputStream(videoLocation);
             FileOutputStream outputStream = new FileOutputStream(outputLocation)
        ) {
            Map<VideoEditor.EditAction, VideoEditor.EditActionValue> edits = new HashMap<>();

            VideoEditor.EditActionValue rangeVal = new VideoEditor.EditActionValue();
            rangeVal.setIntRangeValue(new int[] {0, 3});

            edits.put(VideoEditor.EditAction.CUT_VIDEO, rangeVal);

            ve.processVideo(videoStream, outputStream, edits, null);
        }
    }

    @Test
    public void testAddTextWatermark() throws IOException {
        VideoEditorOld ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        ve.addTextWatermark(videoLocation, outputLocation, "Devomate");
    }

    @Test
    public void testAddImgWatermark() throws IOException {
        VideoEditorOld ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        final String imgLocation = System.getProperty("user.dir") + "/resources/devomate_img.webp";
        ve.addImgWatermark(videoLocation, outputLocation, imgLocation);
    }

    @Test
    public void testAddAudio() throws IOException {
        VideoEditorOld ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";
        final String audioLocation = System.getProperty("user.dir") + "/resources/test.mp3";
        ve.addAudio(videoLocation, outputLocation, audioLocation);
    }

    @Test
    public void testConvertToLowResolution() throws IOException {
        VideoEditorOld ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);
        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_low_res_480p.mp4";
        ve.convertToLowResolution(videoLocation, outputLocation);
    }
}