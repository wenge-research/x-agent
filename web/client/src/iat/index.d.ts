declare class RecorderManager {
    /**
     * 构造函数
     * @param processorPath processor的文件路径，如果processor.worker.js的访问地址为`/a/b/processor.worker.js`,则processorPath 为`/a/b`
     *
     */
    constructor(processorPath: string);
    private audioBuffers;
    private processorPath;
    private audioContext?;
    private audioTracks?;
    private audioWorklet?;
    onStop?: (audioBuffers: ArrayBuffer[]) => void;
    onFrameRecorded?: (params: {
        isLastFrame: boolean;
        frameBuffer: ArrayBuffer;
    }) => void;
    /**
     * 监听录音开始事件
     */
    onStart?: () => void;
    start({ sampleRate, frameSize, arrayBufferType, }: {
        sampleRate?: number;
        frameSize?: number;
        arrayBufferType?: "short16" | "float32";
    }): Promise<void>;
    stop(): void;
}

export { RecorderManager as default };
