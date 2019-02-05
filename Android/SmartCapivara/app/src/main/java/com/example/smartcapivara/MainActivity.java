package com.example.smartcapivara;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sc_cadastrar(View v){

       // Toast.makeText(this, "Cadastro!",
       //         Toast.LENGTH_LONG).show();

        startActivity(new Intent(MainActivity.this, Cadastrar.class));

    }

    public void sc_entrar(View v){
        /*Toast.makeText(this, "This is my Toast message!",
                Toast.LENGTH_LONG).show();*/
        startActivity(new Intent(MainActivity.this, Gestor.class));
    }
}
