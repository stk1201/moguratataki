package jp.ac.ritsumei.ise.phy.exp2.is0688hf.moguratataki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class maingame extends AppCompatActivity {

    maingame_surface surface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maingame);
        surface = new maingame_surface(this);
    }

    public void onGameScreenTapped(View view){
        surface = findViewById(R.id.maingame_surface2);//クラスの呼び出し
        if(surface.finish() == true){
            Button home = findViewById(R.id.homepage);//ホームボタンの定義
            home.setVisibility(View.VISIBLE);//ボタンを表示する
        }
    }

    public void onHomeButtonTapped(View view) {
        Intent intent = new Intent(this, MainActivity.class) ;
        startActivity(intent) ;
    }
}