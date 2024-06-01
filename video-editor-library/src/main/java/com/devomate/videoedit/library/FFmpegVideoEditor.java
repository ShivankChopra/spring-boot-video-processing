package com.devomate.videoedit.library;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.builder.FFmpegOutputBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FFmpegVideoEditor implements VideoEditor {

    private final FFprobe ffprobe;
    private final FFmpegExecutor ffmpegExecutor;

    public FFmpegVideoEditor(String ffmpegPath, String ffprobePath) throws IOException {
        FFmpeg ffmpeg = new FFmpeg(ffmpegPath);
        this.ffprobe = new FFprobe(ffprobePath);
        this.ffmpegExecutor = new FFmpegExecutor(ffmpeg, this.ffprobe);
    }

    @Override
    public void processVideo(InputStream videoInput, OutputStream videoOutput, Map<EditAction, EditActionValue> edits, Map<String, InputStream> additionalInputs) throws IOException {
        String currentTs = Long.toString(new Date().getTime());

        List<Path> tempFilesToDelete = new ArrayList<>(5);

        Path tempInput = Files.createTempFile("in_" + currentTs, ".mp4");
        tempFilesToDelete.add(tempInput);

        Path tempOutput = Files.createTempFile("out_" + currentTs, ".mp4");
        tempFilesToDelete.add(tempOutput);

        try (FileOutputStream fos = new FileOutputStream(tempInput.toFile())) {
            videoInput.transferTo(fos);
        }

        FFmpegBuilder fb = new FFmpegBuilder();
        fb.setInput(tempInput.toString());
        FFmpegOutputBuilder fob = fb.addOutput(tempOutput.toString());

        if (edits.containsKey(EditAction.CUT_VIDEO)) {
            int[] timeRange = edits.get(EditAction.CUT_VIDEO).getIntRangeValue();
            fb.addExtraArgs("-ss", Integer.toString(timeRange[0]));
            fb.addExtraArgs("-to", Integer.toString(timeRange[1]));
        }

        if (edits.containsKey(EditAction.CONVERT_TO_LOW_RES)) {
            if (edits.containsKey(EditAction.ADD_AUDIO)) {
                fb.setComplexFilter("[0:v]scale=-2:480[v]; [0:a][1:a]amix=inputs=2[a];");
                fob.addExtraArgs("-map", "[v]");
                fob.addExtraArgs("-map", "[a]");
                fob.setAudioCodec("aac");
            } else {
                fob.setVideoFilter("scale=-2:480").setAudioCodec("copy");
            }
        }

        if (edits.containsKey(EditAction.ADD_AUDIO)) {
            Path tempAudio = Files.createTempFile("in_audio_" + currentTs, ".mp3");
            tempFilesToDelete.add(tempAudio);

            String audioId = edits.get(EditAction.ADD_AUDIO).getStringValue();
            try (FileOutputStream audioStream = new FileOutputStream(tempAudio.toFile())) {
                additionalInputs.get(audioId).transferTo(audioStream);
            }

            fb.addInput(tempAudio.toString());
            fob.setVideoCodec("libx264").setAudioCodec("aac").addExtraArgs("-map", "0:v:0").addExtraArgs("-map", "1:a:0").setStrict(FFmpegBuilder.Strict.EXPERIMENTAL);
        }

        fb.overrideOutputFiles(true);

        this.ffmpegExecutor.createJob(fb).run();

        try (FileInputStream fis = new FileInputStream(tempOutput.toFile())) {
            fis.transferTo(videoOutput);
        }

        for (Path tempFile : tempFilesToDelete) {
            Files.delete(tempFile);
        }
    }
}
