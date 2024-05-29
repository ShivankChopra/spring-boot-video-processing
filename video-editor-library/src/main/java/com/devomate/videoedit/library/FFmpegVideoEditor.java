package com.devomate.videoedit.library;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.Map;

public class FFmpegVideoEditor implements VideoEditor {

    private final FFprobe ffprobe;
    private final FFmpegExecutor ffmpegExecutor;

    public FFmpegVideoEditor(String ffmpegPath, String ffprobePath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.ffmpegExecutor = new FFmpegExecutor(ffmpeg, this.ffprobe);
    }

    private static void transferData(InputStream inputStream, OutputStream outputStream) throws IOException {
        ReadableByteChannel inputChannel = Channels.newChannel(inputStream);
        WritableByteChannel outputChannel = Channels.newChannel(outputStream);

        ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

        while (inputChannel.read(buffer) != -1) {
            buffer.flip();
            outputChannel.write(buffer);
            buffer.clear();
        }
    }

    @Override
    public void processVideo(InputStream videoInput, OutputStream videoOutput, Map<EditAction, EditActionValue> edits, Map<String, InputStream> additionalInputs) throws IOException {
        String currentTs = Long.toString(new Date().getTime());

        Path tempInput = Files.createTempFile("in_" + currentTs, ".mp4");
        Path tempOutput = Files.createTempFile("out_" + currentTs, ".mp4");

        try (FileOutputStream fos = new FileOutputStream(tempInput.toFile())) {
            videoInput.transferTo(fos);
        }

        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(tempInput.toString());
        fb.overrideOutputFiles(true);
        fb.addOutput(tempOutput.toString());

        if (edits.containsKey(EditAction.CUT_VIDEO)) {
            int[] timeRange = edits.get(EditAction.CUT_VIDEO).getIntRangeValue();

            fb.addExtraArgs("-ss", Integer.toString(timeRange[0]));
            fb.addExtraArgs("-to", Integer.toString(timeRange[1]));
        }

        this.ffmpegExecutor.createJob(fb).run();

        try (FileInputStream fis = new FileInputStream(tempOutput.toFile())) {
            fis.transferTo(videoOutput);
        }

        Files.delete(tempInput);
        Files.delete(tempOutput);
    }
}
