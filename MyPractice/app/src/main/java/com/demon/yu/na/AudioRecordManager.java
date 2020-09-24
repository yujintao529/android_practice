package com.demon.yu.na;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.demon.yu.avd.WebVadHelper;

import java.nio.ShortBuffer;

public class AudioRecordManager {
    private AudioRecord audioRecord;
    private static final int mSampleRateInHz = 48000;
    private static final int STATE_IDLE = 9;
    private static final int STATE_RECODING = 1;
    private static final int STATE_RECODING_STOP = 2;
    private static final int STATE_DEAD = 3;
    private static final int mChannelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static final int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int mBufferSizeInBytes;
    private RecordThread recordThread = new RecordThread("recordThread");
    private ShortBuffer shortBuffer;
    private short[] shortArr;
    private short[] shortArrTemp;
    private volatile int state = STATE_IDLE;

    private OnRecordingListener onRecordingListener;
    private OnPersonDetectListener onPersonDetectListener;

    private WebVadHelper webVadHelper;
    private int validateSize;


    public AudioRecordManager() {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
        validateSize = calculate(mSampleRateInHz, 10);
        shortBuffer = ShortBuffer.allocate(validateSize);
        shortArr = new short[validateSize];
        shortArrTemp = new short[validateSize];
        webVadHelper = new WebVadHelper();
        webVadHelper.setMode(WebVadHelper.MODEL_2);

    }

    private int calculate(int sampleRateInHz, int level) {
        //10ms,20ms,30ms
        return sampleRateInHz / 1000 * level;
    }

    public synchronized void startRecord() {
        if (state == STATE_IDLE) {
            state = STATE_RECODING;
            recordThread.start();
        }
    }

    public synchronized void stop() {
        if (state == STATE_RECODING) {
            state = STATE_RECODING_STOP;
            audioRecord.stop();
        }
    }

    public synchronized void reStartRecord() {
        if (state == STATE_RECODING_STOP) {
            state = STATE_RECODING;
            audioRecord.startRecording();
        }
    }

    public synchronized void dead() {
        if (state == STATE_RECODING_STOP) {
            state = STATE_DEAD;
            audioRecord.release();
            webVadHelper.release();
        }
    }


    private void onReadFloatBuffer(short[] floats, int length) {
        if (onRecordingListener != null) {
            onRecordingListener.onRecording(floats, length);
        }
        int result = webVadHelper.process(mSampleRateInHz, shortArrTemp, length);
        if (onPersonDetectListener != null) {
            onPersonDetectListener.onDetect(result == WebVadHelper.ACTIVE_VOICE);
        }
    }

    public class RecordThread extends Thread {

        public RecordThread(@NonNull String name) {
            super(name);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            for (; ; ) {
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED || state == STATE_DEAD) {
                    break;
                }
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                }
            }
            if (state == STATE_RECODING && audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                audioRecord.startRecording();
            }
            while (state == STATE_RECODING) {
                if (audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                    int result = audioRecord.read(shortArr, 0, validateSize, AudioRecord.READ_BLOCKING);
                    if (result == validateSize) {
                        onReadFloatBuffer(shortArr, result);
                    }
                    try {
                        sleep(10);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    public OnRecordingListener getOnRecordingListener() {
        return onRecordingListener;
    }

    public void setOnRecordingListener(OnRecordingListener onRecordingListener) {
        this.onRecordingListener = onRecordingListener;
    }

    public interface OnRecordingListener {
        public void onRecording(short[] shorts, int dataLength);
    }

    public OnPersonDetectListener getOnPersonDetectListener() {
        return onPersonDetectListener;
    }

    public void setOnPersonDetectListener(OnPersonDetectListener onPersonDetectListener) {
        this.onPersonDetectListener = onPersonDetectListener;
    }

    public interface OnPersonDetectListener {
        public void onDetect(boolean hasPerson);
    }

}
