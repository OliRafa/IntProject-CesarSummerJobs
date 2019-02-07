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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;

public class Gestor extends AppCompatActivity{

    private String ip = "http://179.106.208.38:8080/";

    private String visitante_id;
    private String autorizante_id;

    private ImageView imageView;

    private TextView txt_nome, txt_cargo, txt_data, txt_setor, txt_matricula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestor);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Pessoa usuario = new Pessoa();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                usuario.setNome( extras.getString("usuario_nome") );
                usuario.setEmail( extras.getString("usuario_email") );
                usuario.setRGPassaporte( extras.getString("usuario_documento") );
                usuario.setID( extras.getString("usuario_id") );
                usuario.setTipo( extras.getString("usuario_tipo") );

                Log.d("USUARIO", usuario.toString());

                this.autorizante_id = usuario.getID();
            }
        }

        //new Thread(new RequisicaoPush()).start();

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
        txt_nome.setText("Exemplo");
        txt_cargo.setText("Exemplo");
        txt_data.setText("01/01/200");
        txt_setor.setText("Exemplo");
        txt_matricula.setText("999999999");


        JSONObject cadastro = new JSONObject();

        try {
            cadastro.put("autorizante", "hehehe");

            String url = ip + "push";

            StringEntity entity = new StringEntity(cadastro.toString());

            AsyncHttpClient client = new AsyncHttpClient();

            //client.addHeader("autorizante", "huehue");
            Log.d("JSON", "Enviado: " + cadastro.toString());
            client.post(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response)                {
                    Log.d("JSON Requisicao", "Recebido: " + response.toString());

                    for(int i=0;i<response.length();i++){
                        try{
                            JSONObject aux = response.getJSONObject(i);
                            if(aux.get("_id").toString().equals("5c5b2904191c821d5c701a88")){
                                txt_nome.setText( aux.get("nome").toString() );
                                txt_cargo.setText( aux.get("motivo").toString() );
                                txt_data.setText( aux.get("data_inicial").toString() );
                                txt_setor.setText("Celtab");
                                txt_matricula.setText( aux.get("rg_passaporte").toString() );
                                visitante_id = aux.get("_id").toString();

                                Log.i("JSONArray",aux.toString());
                            }
                        }
                        catch (JSONException e){
                            Log.e("JSONArray", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                    Log.e("JSON Requisicao", errorResponse.toString());
                }
            });
        }
        catch (Exception e) {
            Log.e("JSON Requisicao", e.getMessage());
        }


    }

    public void gestor_confirm(View v){
        /*Toast.makeText(this, "Access confirmed!",
                Toast.LENGTH_SHORT).show();*/

        visitanteValidation(true);
    }

    public void gestor_deny(View v){
        /*Toast.makeText(this, "Access denied!",
                Toast.LENGTH_SHORT).show();*/

        visitanteValidation(false);
    }

    private void visitanteValidation(boolean validation){
        Log.d("JSON Validation", "ID: " + visitante_id);
        JSONObject cadastro = new JSONObject();
        try {
            cadastro.put("id_autorizante", autorizante_id);
            cadastro.put("id_visitante", visitante_id);
            cadastro.put("validado", validation);

            String url = ip + "visitante";

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
