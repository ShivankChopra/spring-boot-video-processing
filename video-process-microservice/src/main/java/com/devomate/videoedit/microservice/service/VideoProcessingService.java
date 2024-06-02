package com.devomate.videoedit.microservice.service;

import com.devomate.videoedit.library.FFmpegVideoEditor;
import com.devomate.videoedit.library.VideoEditor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class VideoProcessingService {

    // @TODO: autowire using config?
    private final VideoEditor videoEditor;

    public VideoProcessingService(@Value("${video-editor.ffmpeg-path}") String ffmpegPath, @Value("${video-editor.ffprobe-path}") String ffprobePath) throws IOException {
        videoEditor = new FFmpegVideoEditor(ffmpegPath, ffprobePath);
    }

    public void processVideo(String taskMessage) {
        Map<VideoEditor.EditAction, VideoEditor.EditActionValue> edits = new HashMap<>();

        edits.put(VideoEditor.EditAction.CONVERT_TO_LOW_RES, null);

        System.out.println("In process video : " + taskMessage);
    }
}

