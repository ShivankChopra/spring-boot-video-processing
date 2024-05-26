package com.devomate.videoedit.microservice.service;

import com.devomate.videoedit.library.FFmpegCliWrapperVideoEditor;
import com.devomate.videoedit.library.VideoEditorOld;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class VideoProcessingService {

    @Value("video-editor.ffmpeg-path")
    private String ffmpegPath;

    @Value("video-editor.ffprobe-path")
    private String ffprobePath;

    // @TODO: autowire using config?
    private final VideoEditorOld videoEditor;

    public VideoProcessingService() throws IOException {
        videoEditor = new FFmpegCliWrapperVideoEditor(ffmpegPath, ffprobePath);
    }

    public void processVideo(String taskMessage) {
        videoEditor.convertToLowResolution(taskMessage, taskMessage);
    }
}
