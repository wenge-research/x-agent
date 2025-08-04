type ISaveAudioData = "pcm" | "wav";
declare class AudioPlayer {
    constructor(processorPath?: string);
    private toSampleRate;
    private resumePlayDuration;
    private fromSampleRate;
    private isAudioDataEnded;
    private playAudioTime?;
    private status;
    private audioContext?;
    private bufferSource?;
    private audioDatas;
    private pcmAudioDatas;
    private audioDataOffset;
    private processor;
    postMessage({ type, data, isLastData }: {
        type: "base64" | "string" | "Int16Array" | "Float32Array";
        data: string | Int16Array | Float32Array;
        isLastData: boolean;
    }): void;
    private onPlay?;
    private onStop?;
    private playAudio;
    reset(): void;
    start({ autoPlay, sampleRate, resumePlayDuration }?: {
        autoPlay?: boolean;
        sampleRate?: number;
        resumePlayDuration?: number;
    }): void;
    play(): void;
    stop(): void;
    getAudioDataBlob(type: ISaveAudioData): Blob | undefined;
}

export { AudioPlayer as default };
