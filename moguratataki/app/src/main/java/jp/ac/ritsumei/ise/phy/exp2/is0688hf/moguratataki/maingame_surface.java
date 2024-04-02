package jp.ac.ritsumei.ise.phy.exp2.is0688hf.moguratataki;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.Random;

public class maingame_surface extends SurfaceView implements Runnable,SurfaceHolder.Callback {

    private SurfaceHolder sHolder ; // SurfaceHolder を格納

    public maingame_surface(Context context) {
        super(context);
        initialize() ;
    }

    public maingame_surface(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize() ;
    }

    public maingame_surface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize() ;
    }

    public maingame_surface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize() ;
    }

    private void initialize() {
        sHolder = getHolder() ;// SurfaceHolder を取得
        sHolder.addCallback(this) ;// 自身をコールバックとして登録
    }

    private Thread thread;
    private SoundPlayer soundPlayer;

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        thread = new Thread(this) ; //新しくスレッドを作成
        thread.start() ; //スレッドをスタートさせる
        soundPlayer = new SoundPlayer(this.getContext());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        thread = null;// スレッドを停止させる
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float getTouchX = event.getX();
        float getTouchY = event.getY();

        for(int n=0; n<3; n++){
            for(int i=0; i<3; i++){
                float center_x = c.getWidth()/5 + n*350;
                float center_y = c.getHeight()/3 + i*400;
                if((center_x - 120.0f < getTouchX && getTouchX < center_x + 120.0f) && (center_y - 120.0f < getTouchY && getTouchY < center_y + 120.0f)){
                    if(appeared[n][i] <= 9 && appeared[n][i] >= 7){//うさぎを退治した時
                        // HPの減少
                       if(hp[2]==1){
                           hp[2]=0;
                       }
                       else if (hp[1]==1) {
                           hp[1]=0;
                       }
                       else{
                           hp[0]=0;
                           flag = 1;//drawOver()に遷移
                       }
                       soundPlayer.playHPdown();//HPが減るSE
                    }

                    if(appeared[n][i] <= 6 && appeared[n][i] >= 4){//ひまわりを退治した時
                        score += 10; //スコアの上昇
                        soundPlayer.playBeat(); //退治した時のSE
                    }

                    appeared[n][i] = 0;
                }

            }
        }

        return super.onTouchEvent(event) ; // 下位の View, Activity のタッチイベント の結果を返す
    }

    static final long FPS = 30 ;
    static final long FTIME = 1000 / FPS ;
    private long loopC;

    private int flag;

    private int score;

    Canvas c;

    @Override
    public void run() {
        while (thread != null) {
            loopC = 0; // ループ数のカウンタ
            long wTime = 0; // 次の描画までの待ち時間(ミリ秒)
            flag = 0;
            score = 0;
            long sTime = System.currentTimeMillis(); // 開始時の現在時刻
            while (thread != null) {
                try {
                    loopC++;
                     c = sHolder.lockCanvas();
                     if(flag == 0){
                         drawGame();//ゲーム表示
                     } else if (flag == 1) {
                         drawOver();//ゲームオーバー表示
                         thread = null;
                     }

                    wTime = (loopC *FTIME)-(System.currentTimeMillis() - sTime);
                    if (wTime > 0) {
                        Thread.sleep(wTime);
                    }
                }
                catch (InterruptedException e) {
                }
            }
        }
    }

    private final static float RADIUS = 120.0f ; // 円の半径
    //穴の数と位置に合わせて行列を作成する
    private int[][] appeared = new int[][]{
            {0,0,0},
            {0,5,0},
            {0,0,0}
    };
    private int[] hp = {1,1,1};

    public void drawGame(){
        Paint p = new Paint() ;

        //背景設定
        Bitmap grass_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        c.drawBitmap(grass_bmp,0,0 ,p) ;

        //スコア表示
        p.setColor(Color.BLACK);
        p.setTextSize(150);
        c.drawText(Integer.valueOf(score).toString(),c.getWidth()/2-50,c.getHeight()/10,p);

        //穴に何を出現させる・させないかを0~9の数字でランダムに振り分ける
        if(loopC%5 == 0){
            int flag_rest = 0;//他にもひまわりが残っていたとしてもHPが減らないようにflagで区別する。
            Random random = new Random() ;
            for(int n=0; n<3; n++){
                for(int i=0; i<3; i++){
                    if(flag_rest == 0 && appeared[n][i] <= 6 && appeared[n][i] >= 4){
                        if(hp[2]==1){
                            hp[2]=0;
                        }
                        else if (hp[1]==1) {
                            hp[1]=0;
                        }
                        else{
                            hp[0]=0;
                            flag = 1;//drawOver()に遷移
                        }
                        flag_rest = 1;
                        soundPlayer.playHPdown();//HPが減るSE
                    }
                    appeared[n][i] = random.nextInt(10);//新たにランダムに配置する
                }
            }
        }

        //うさぎの縮小化
        Bitmap rabbit_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.usagi);
        Matrix matrix = new Matrix();
        matrix.postScale(0.4f, 0.4f);//縮小度合いの行列
        Bitmap scaledRabbit_bmp = Bitmap.createBitmap(rabbit_bmp, 0, 0, rabbit_bmp.getWidth(), rabbit_bmp.getHeight(), matrix, true);//縮小化されたbmpの作成

        //ひまわりの縮小化
        Bitmap sunflower_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.himawari);
        matrix.postScale(0.3f, 0.3f);//縮小度合いの行列
        Bitmap scaledSunflower_bmp = Bitmap.createBitmap(sunflower_bmp, 0, 0, sunflower_bmp.getWidth(), sunflower_bmp.getHeight(), matrix, true);//縮小化されたbmpの作成

        //HPの縮小化
        Bitmap hp_bmp = BitmapFactory.decodeResource(getResources(), R.drawable.hp);
        Bitmap scaledHP_bmp = Bitmap.createBitmap(hp_bmp, 0, 0, hp_bmp.getWidth(), hp_bmp.getHeight(), matrix, true);//縮小化されたbmpの作成

        //穴生成&うさぎ・ひまわりのランダム出現
        for(int n=0; n<3; n++){
            for(int i=0; i<3; i++){
                //穴生成
                p.setColor(Color.BLACK) ;
                c.drawCircle(c.getWidth()/5 + n*350, c.getHeight()/3 + i*400, RADIUS, p) ;
                if(appeared[n][i] <= 6 && appeared[n][i] >= 4){//appearedが3~5のときはひまわりを表示する
                    c.drawBitmap(scaledSunflower_bmp,c.getWidth()/30 + n*350, c.getHeight()/4 + i*400,p);
                }
                else if (appeared[n][i] <= 9 && appeared[n][i] >= 7) {//appearedが4~9のときはうさぎを表示する
                    c.drawBitmap(scaledRabbit_bmp, c.getWidth()/10 + n*350, c.getHeight()/5 + i*400 ,p);
                }
            }
            //HPの表示
            if(hp[n]==1){//1だったらHP表示し、それ以外は非表示
                c.drawBitmap(scaledHP_bmp,c.getWidth()/3 + n*100,c.getHeight()-150,p);
            }
        }
        //キャンバスの反映
        sHolder.unlockCanvasAndPost(c) ;
    }


    public void drawOver(){
        soundPlayer.playGameOver();//ゲームオーバのSE

        Paint p = new Paint() ;

        p.setColor(Color.BLACK);
        c.drawRect(0, 0, c.getWidth(), c.getHeight(), p);

        p.setColor(Color.WHITE);
        p.setTextSize(200);
        c.drawText("Score:"+Integer.valueOf(score).toString(),c.getWidth()/15,c.getHeight()/3,p);
        p.setTextSize(100);
        c.drawText("GAME OVER",c.getWidth()/4,3*c.getHeight()/5,p);
        p.setTextSize(50);
        c.drawText("- Touch Screen -",c.getWidth()/3,3*c.getHeight()/5 + 50,p);

        //キャンバスの反映
        sHolder.unlockCanvasAndPost(c) ;
    }

    public boolean finish(){
        if(flag == 1){
            return true;
        }
        return false;
    }
}
