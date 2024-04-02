package jp.ac.ritsumei.ise.phy.exp2.is0688hf.moguratataki;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundPlayer {
    private static SoundPool soundPool;

    private static int gameover;
    private static int beat;
    private static int hpdown;

    public SoundPlayer(Context context) {

        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        gameover = soundPool.load(context, R.raw.gameover, 0);
        beat = soundPool.load(context, R.raw.beat, 0);
        hpdown = soundPool.load(context, R.raw.hpdown, 0);
    }

    public void playGameOver() {
        soundPool.play(gameover, 1.0f, 1.0f, 0, 0, 1.0f);
    }
    public void playBeat() {
        soundPool.play(beat, 1.0f, 1.0f, 0, 0, 1.0f);
    }
    public void playHPdown() {
        soundPool.play(hpdown, 1.2f, 1.2f, 0, 0, 1.0f);
    }
}
