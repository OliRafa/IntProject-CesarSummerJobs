package com.example.smartcapivara;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Gestor extends AppCompatActivity{

    private String ip_address;

    private Pessoa gestor, visitante;

    private ImageView visitantePicture;

    private TextView txt_nome, txt_cargo, txt_datainicio, txt_datafim, txt_motivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                gestor = new Pessoa();
                gestor.setNome( extras.getString("gestor_nome") );
                gestor.setEmail( extras.getString("gestor_email") );
                gestor.setRGPassaporte( extras.getString("gestor_documento") );
                gestor.setID( extras.getString("gestor_id") );
                gestor.setTipo( extras.getString("gestor_tipo") );

                visitante = new Pessoa();
                visitante.setNome( extras.getString("visitante_nome") );
                visitante.setEmail( extras.getString("visitante_email") );
                visitante.setRGPassaporte( extras.getString("visitante_documento") );
                visitante.setID( extras.getString("visitante_id") );
                visitante.setTipo( extras.getString("visitante_tipo") );
                visitante.setDataInicio( extras.getString("visitante_datainicio") );
                visitante.setDataFim( extras.getString("visitante_datafim") );
                visitante.setAutorizante( extras.getString("visitante_autorizante") );
                visitante.setCargo( extras.getString("visitante_cargo") );
                visitante.setMotivo( extras.getString("visitante_motivo") );
                visitante.setFoto( extras.getString("visitante_foto") );

                this.ip_address = extras.getString("ip_address");

                Log.d("ACTIVITY Gestor", "IP ADDRESS: " + this.ip_address );
                Log.d("GESTOR", gestor.toString());
                Log.d("VISITANTE", visitante.toString());
            }
        }

        //new Thread(new RequisicaoPush()).start();

        visitantePicture = (ImageView) findViewById(R.id.gestor_img_cliente);

        txt_nome = (TextView) findViewById(R.id.gestor_txt_nome);
        txt_cargo = (TextView) findViewById(R.id.gestor_txt_cargo);
        txt_datainicio = (TextView) findViewById(R.id.gestor_txt_datainicio);
        txt_datafim = (TextView) findViewById(R.id.gestor_txt_datafim);
        txt_motivo = (TextView) findViewById(R.id.gestor_txt_motivo);

        loadImage();
        setData();
        loadData();
    }


    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void loadImage(){
        /*String url = "https://media.licdn.com/dms/image/C5603AQE_Foq49nWlAA/profile-displayphoto-shrink_200_200/0?e=1554940800&v=beta&t=M_XP7qB45YbOeSSJxe1uPEDdImmuZEsCIVuTHH0uQco";

        Picasso.with(this)
                .load(url)
                .resize(200, 200)         //optional
                .centerCrop()                        //optional
                .into(this.imageView);*/

        try{
            byte[] decodedString = Base64.decode(visitante.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            visitantePicture.setImageBitmap(decodedByte);

            Log.d("BASE64" , visitante.getFoto() );
        }
        catch (Exception e){
            Log.e("PROBLEM LOADING PICTURE", e.getMessage());
        }
    }


    private void setData(){
        txt_nome.setTextColor(Color.WHITE);
        txt_cargo.setTextColor(Color.WHITE);
        txt_datainicio.setTextColor(Color.WHITE);
        txt_datafim.setTextColor(Color.WHITE);
        txt_motivo.setTextColor(Color.WHITE);

        txt_nome.setTextSize(20);
        txt_cargo.setTextSize(18);
        txt_datainicio.setTextSize(18);
        txt_datafim.setTextSize(18);
        txt_motivo.setTextSize(18);
    }


    private void loadData(){
        txt_nome.setText( visitante.getNome() );
        txt_cargo.setText( visitante.getCargo() );
        txt_datainicio.setText( visitante.getDataInicio() );
        txt_datafim.setText( visitante.getDataFim() );
        txt_motivo.setText( visitante.getMotivo() );
    }

    public void gestor_confirm(View v){
        visitanteValidation(true);
        finish();
    }

    public void gestor_deny(View v){
        visitanteValidation(false);
        finish();
    }

    private void visitanteValidation(boolean validation){
        Log.d("JSON Validation", "ID: " + visitante.getID());
        JSONObject cadastro = new JSONObject();
        try {
            cadastro.put("id_autorizante", gestor.getID());
            cadastro.put("id_visitante", visitante.getID());
            cadastro.put("validado", validation);

            String url = this.ip_address + "/visitante";

            StringEntity entity = new StringEntity(cadastro.toString());

            AsyncHttpClient client = new AsyncHttpClient();

            Log.d("JSON", "Enviado: " + cadastro.toString());
            client.put(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response)                {
                    Log.d("JSON Validation", "Recebido: " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                    Log.e("JSON Validation", errorResponse.toString());
                }
            });
        }
        catch (Exception e) {
            Log.e("JSON Validation", e.getMessage());
        }
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
