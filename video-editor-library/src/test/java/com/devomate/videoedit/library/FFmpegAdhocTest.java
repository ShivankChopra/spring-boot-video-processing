package com.devomate.videoedit.library;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;

public class FFmpegAdhocTest {

    private static final String FFMPEG_PATH = "/opt/homebrew/bin/ffmpeg";
    private static final String FFPROBE_PATH = "/opt/homebrew/bin/ffprobe";

    public static void main(String[] args) {
        try {
            FFmpeg ffmpeg = new FFmpeg(FFMPEG_PATH);
            FFprobe ffprobe = new FFprobe(FFPROBE_PATH);

            String filepath = System.getProperty("user.dir") + "/resources/test.mp4";

            // Test with a sample media file
            FFmpegProbeResult probeResult = ffprobe.probe(filepath);

            System.out.println("File info:");
            System.out.println("  Format: " + probeResult.getFormat().format_long_name);
            System.out.println("  Duration: " + probeResult.getFormat().duration);
            System.out.println("  Bit rate: " + probeResult.getFormat().bit_rate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


