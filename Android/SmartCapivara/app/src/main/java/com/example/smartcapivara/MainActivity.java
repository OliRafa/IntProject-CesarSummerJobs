package com.example.smartcapivara;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String ip_address = "http://179.106.208.36:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("ACTIVITY STARTED", "IP ADDRESS" + this.ip_address );
    }

    public void sc_cadastrar(View v){

       // Toast.makeText(this, "Cadastro!",
       //         Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, Cadastrar.class);
        intent.putExtra("ip_address", this.ip_address );
        startActivity(intent);

    }

    public void sc_entrar(View v){
        /*Toast.makeText(this, "This is my Toast message!",
                Toast.LENGTH_LONG).show();*/
        Intent intent = new Intent(MainActivity.this, Entrar.class);
        intent.putExtra("ip_address", this.ip_address );
        startActivity(intent);
    }

    public void sc_historico(View v){
        Intent intent = new Intent(MainActivity.this, Historico.class);
        startActivity(intent);
    }
}
