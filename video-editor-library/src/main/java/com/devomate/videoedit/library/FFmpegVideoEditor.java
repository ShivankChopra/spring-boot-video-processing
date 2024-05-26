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
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FFmpegVideoEditor implements VideoEditor {

    private final FFprobe ffprobe;
    private final FFmpegExecutor ffmpegExecutor;

    public FFmpegVideoEditor(String ffmpegPath, String ffprobePath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.ffmpegExecutor = new FFmpegExecutor(ffmpeg, this.ffprobe);
    }

    private static void createNamedPipe(String pipeName) throws IOException {
        Path pipePath = Paths.get(pipeName);
        if (Files.notExists(pipePath))
            Files.createFile(pipePath);

        Files.setPosixFilePermissions(pipePath, PosixFilePermissions.fromString("rw-rw-rw-"));
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
    public boolean processVideo(InputStream videoInput, OutputStream videoOutput, Map<EditAction, EditActionValue> edits, Map<String, InputStream> additionalInputs) throws InterruptedException {
        long currentTs = new Date().getTime();

        String inputFilePipePath = System.getProperty("java.io.tmpdir") + "/in_" + currentTs;
        String outputFilePipePath = System.getProperty("java.io.tmpdir") + "/out_" + currentTs;

        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(inputFilePipePath);
        fb.overrideOutputFiles(true);
        fb.addOutput(outputFilePipePath);

        if (edits.containsKey(EditAction.CUT_VIDEO)) {
            int[] timeRange = edits.get(EditAction.CUT_VIDEO).getIntRangeValue();

            fb.addExtraArgs("-ss", Integer.toString(timeRange[0]));
            fb.addExtraArgs("-to", Integer.toString(timeRange[1]));
        }

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            try (OutputStream pipeOutputStream = new FileOutputStream(inputFilePipePath)) {
                transferData(videoInput, pipeOutputStream);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        });

        executor.submit(() -> {
            try (InputStream pipeInputStream = new FileInputStream(outputFilePipePath)) {
                transferData(pipeInputStream, videoOutput);
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        });

        this.ffmpegExecutor.createJob(fb).run();

        return executor.awaitTermination(15, TimeUnit.MINUTES);
    }
}
