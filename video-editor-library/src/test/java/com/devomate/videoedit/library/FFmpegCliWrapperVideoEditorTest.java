package com.devomate.videoedit.library;

import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class FFmpegCliWrapperVideoEditorTest {

    @Test
    public void testCutVideo() throws IOException {
        final String FFMPEG_PATH = "/opt/homebrew/bin/ffmpeg";
        final String FFPROBE_PATH = "/opt/homebrew/bin/ffprobe";

        VideoEditor ve = new FFmpegCliWrapperVideoEditor(FFMPEG_PATH, FFPROBE_PATH);

        final String videoLocation = System.getProperty("user.dir") + "/resources/test.mp4";
        final String outputLocation = System.getProperty("user.dir") + "/resources/test_out.mp4";

        ve.cutVideo(videoLocation, outputLocation, 0, 3);

        FFmpegProbeResult result = ve.probeVideo(outputLocation);

        for (int i = 0; i < result.streams.size(); i++) {
            assertEquals(3, (long)result.streams.get(i).duration);
        }
    }
}