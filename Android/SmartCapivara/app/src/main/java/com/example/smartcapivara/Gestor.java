package com.example.smartcapivara;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Random;

public class Gestor extends AppCompatActivity{

    private ImageView imageView;

    private TextView txt_nome, txt_cargo, txt_data, txt_setor, txt_matricula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Thread(new RequisicaoPush()).start();

        imageView = (ImageView) findViewById(R.id.img_cliente);

        txt_nome = (TextView) findViewById(R.id.txt_nome);
        txt_cargo = (TextView) findViewById(R.id.txt_cargo);
        txt_data = (TextView) findViewById(R.id.txt_data);
        txt_setor = (TextView) findViewById(R.id.txt_setor);
        txt_matricula = (TextView) findViewById(R.id.txt_matricula);

        loadImageFromURL();
        setData();
        loadData();
    }


    public boolean onOptionsItemSelected(MenuItem item){

        /*Toast.makeText(this, "Das kann uns keiner nehmen",
                Toast.LENGTH_SHORT).show();*/

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    private void loadImageFromURL(){
        String url = "https://media.licdn.com/dms/image/C5603AQE_Foq49nWlAA/profile-displayphoto-shrink_200_200/0?e=1554940800&v=beta&t=M_XP7qB45YbOeSSJxe1uPEDdImmuZEsCIVuTHH0uQco";

        Picasso.with(this)
                .load(url)
                .resize(200, 200)         //optional
                .centerCrop()                        //optional
                .into(this.imageView);
    }


    private void setData(){
        txt_nome.setTextColor(Color.WHITE);
        txt_cargo.setTextColor(Color.WHITE);
        txt_data.setTextColor(Color.WHITE);
        txt_setor.setTextColor(Color.WHITE);
        txt_matricula.setTextColor(Color.WHITE);

        txt_nome.setTextSize(20);
        txt_cargo.setTextSize(18);
        txt_data.setTextSize(18);
        txt_setor.setTextSize(18);
        txt_matricula.setTextSize(18);
    }


    private void loadData(){
        txt_nome.setText("Nicolas Queiroz de Vasconcelos");
        txt_cargo.setText("Summer");
        txt_data.setText("07/01/2019");
        txt_setor.setText("Celtab");
        txt_matricula.setText("215513200");

    }

    public void gestor_confirm(View v){
        Toast.makeText(this, "Access confirmed!",
                Toast.LENGTH_SHORT).show();
    }

    public void gestor_deny(View v){
        Toast.makeText(this, "Access denied!",
                Toast.LENGTH_SHORT).show();
    }


    class RequisicaoPush implements Runnable{

        RequisicaoPush(){

        }

        @Override
        public void run(){
            for(int i = 0 ; i < 100 ; i++ ){
                Log.d("THREAD", "Current value is " + i);

                try{
                    Thread.sleep(1000);
                }
                catch(Exception e){
                    Log.e("ERRO", "Xablau: " + e.getMessage());
                }
            }
        }
    }
}
