package com.example.smartcapivara;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import org.json.*;
import com.loopj.android.http.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


public class Cadastrar extends AppCompatActivity {

    private String ip_address;
    private String json_id;

    private EditText txt_nome;
    private EditText txt_doc;
    private EditText txt_phone;
    private EditText txt_email;
    private EditText txt_motivo;
    private EditText txt_data_inicio;
    private EditText txt_data_fim;
    private Spinner spin_autor;

    private ImageView cameraPic;
    private Bitmap imageToSend; //imagem de cadastro

    public static final int GET_FROM_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        this.json_id = "empty";

        txt_nome = (EditText) findViewById(R.id.gestor_txt_nome);
        txt_doc = (EditText) findViewById(R.id.txt_doc);
        txt_phone = (EditText) findViewById(R.id.txt_phone);
        txt_email = (EditText) findViewById(R.id.txt_email);
        txt_motivo = (EditText) findViewById(R.id.txt_motivo);
        txt_data_inicio = (EditText) findViewById(R.id.txt_data_inicio);
        txt_data_fim = (EditText) findViewById(R.id.txt_data_fim);
        spin_autor = (Spinner) findViewById(R.id.spin_autor);

        cameraPic = (ImageView) findViewById(R.id.cadastrar_picture);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                this.ip_address = extras.getString("ip_address");
            }
        }

        setVariables();

        Log.e("AVISO", "Entrou em Cadastro.");
        /*loadWebFromURL();*/

    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImageView icon = (ImageView) findViewById(R.id.lbl_pic_icon);
        Button btn_pic = (Button) findViewById(R.id.btn_pic);
        icon.setBackgroundResource(0);
        btn_pic.setBackgroundResource(0);


        imageToSend = (Bitmap) data.getExtras().get("data");
        cameraPic.setImageBitmap(imageToSend);
    }


    public void setVariables(){

        /**
         * Manipula Spinner
         */
        String url = this.ip_address + "/autorizante";

        final List<String> list;

        list = new ArrayList<String>();
        list.add("Autorizante");

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"

                try{
                    JSONObject arr = new JSONObject(new String(response));
                    Log.d("RESPONSE", arr.getJSONArray("data").toString());

                    int size = arr.getJSONArray("data").length();
                    for (int i = 0 ; i < size ; i++){
                        list.add(arr.getJSONArray("data").get(i).toString());
                    }

                }
                catch(Exception e){
                    Log.e("ERRO", e.getMessage());
                }

                Log.d("HTTP", "Deu certo");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });


        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_autor.setAdapter(adapter);

        /*txt_nome.setHighlightColor(Color.rgb(255,165,0));*/
        /*txt_nome.setHintTextColor(Color.rgb(230,115,38));
        txt_email.setHintTextColor(Color.rgb(230,115,38));
        txt_motivo.setHintTextColor(Color.rgb(230,115,38));
        txt_phone.setHintTextColor(Color.rgb(230,115,38));
        txt_doc.setHintTextColor(Color.rgb(230,115,38));
        txt_data_fim.setHintTextColor(Color.rgb(230,115,38));
        txt_data_inicio.setHintTextColor(Color.rgb(230,115,38));

        txt_nome.setTextColor(Color.rgb(230,115,38));

        txt_email.setDrawingCacheBackgroundColor(Color.rgb(230,115,38));*/
    }

    public void uploadPicture(View v){
        /*Toast.makeText(this, "Upload your picture",
                Toast.LENGTH_SHORT).show();*/
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }

    public void loadWebFromURL(){

        String url = "https://docs.google.com/forms/d/19FCKcKzP2reXIW3NGG-EdrJnYFYR57mjEvCIYjgefIc/viewform?edit_requested=true";

        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadUrl(url);
    }


    /**
     * A terminar: validação das entradas
     * @param v
     */
    public void concluir(View v){
        String nome = "" , email = "", motivo= "", autorizante = "", data_inicio = "", data_fim = "", doc = "", phone = "";

        if(entradasValidas()){

            nome = txt_nome.getText().toString();
            doc = txt_doc.getText().toString();
            phone = txt_phone.getText().toString();
            email = txt_email.getText().toString();
            motivo = txt_motivo.getText().toString();
            autorizante = spin_autor.getSelectedItem().toString();
            data_inicio = txt_data_inicio.getText().toString();
            data_fim = txt_data_fim.getText().toString();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

            /**
             * Envia dados de cadastro
             */
            JSONObject cadastro = new JSONObject();

            try {
                cadastro.put("nome", nome);
                cadastro.put("email", email);
                cadastro.put("fone", phone);
                cadastro.put("rg_passaporte", doc);
                cadastro.put("data_final", data_fim);
                cadastro.put("data_inicial", data_inicio);
                cadastro.put("motivo", motivo);
                cadastro.put("autorizante", autorizante);
                cadastro.put("foto", encodedImage);

                try {
                    Log.d("CADASTRO", cadastro.toString());

                    String url = this.ip_address + "/registro";

                    StringEntity entity = new StringEntity(cadastro.toString());

                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            Log.d("CADASTRO DADOS", "Enviado. " + response.toString());
                            /*try{
                                if( Boolean.parseBoolean(response.get("ok").toString()) ){
                                    Cadastrar.this.json_id = response.get("_id").toString();
                                }
                            }
                            catch(JSONException e){
                                Log.e("CADASTRO DADOS", e.getMessage());
                            }*/
                            Cadastrar.this.finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse)
                        {
                            Log.e("CADASTRO DADOS", errorResponse.toString());
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("CADASTRO DADOS", e.getMessage());
                }


            }
            catch (JSONException e){
                Log.e("ERRO JSON", e.getMessage());
            }

        }

        //sendPicture();

    }

    private  void sendPicture(){

        if(!this.json_id.equals("empty")){
            //JSONObject picBitmap = new JSONObject();
            Log.d("UPLOAD PICTURE", "Entrou: " + this.json_id);
            try {
                /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageToSend.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                picBitmap.put("foto", encodedImage);*/

                try {
                    Log.d("VAI ENVIAR IMAGEM", imageToSend.toString());

                    String url = this.ip_address + "/teste";

                    StringEntity entity = new StringEntity(imageToSend.toString());

                    AsyncHttpClient client = new AsyncHttpClient();

                    client.post(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            Log.d("ENVIOU IMAGEM", "Enviado. " + response.toString());
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse)
                        {
                            Log.e("ERRO IMAGEM", errorResponse.toString());
                        }
                    });
                }
                catch (Exception e) {
                    Log.e("ERRO IMAGEM", e.getMessage());
                }


            }
            catch (Exception e){
                Log.e("ERRO JSON", e.getMessage());
            }
        }
    }

    private boolean entradasValidas(){
        String nome = "" , email = "", motivo= "", autorizante = "", data_inicio = "", data_fim = "", doc = "";
        int val = 0;

        /**
         * Validação de Nome
         */
        try{
            nome = txt_nome.getText().toString();
            if(nome.isEmpty()){
                val = 1;
                Log.e("OI","Val: " + val);
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler nome: " + e.getMessage());
            val = 1;
        }

        /**
         * Validação de Documento
         */
        try{
            doc = txt_doc.getText().toString();
            if(doc.isEmpty() && val == 0){
                val = 2;
            }
        }
        catch (Exception e){
            val = 2;
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler documento: " + e.getMessage());
        }

        /**
         * Validação de Email
         */
        try{
            email = txt_email.getText().toString();
            if(email.isEmpty() && val == 0){
                val = 3;
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler email: " + e.getMessage());
            val = 3;
        }


        /**
         * Validação de Motivo
         */
        try{
            motivo = txt_motivo.getText().toString();
            if(motivo.isEmpty() && val == 0){
                val = 4;
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler motivo: " + e.getMessage());
            val = 4;
        }


        /**
         * Validação de Autorizante
         */
        try{
            autorizante = spin_autor.getSelectedItem().toString();
            if(autorizante.equals("Autorizante") && val == 0){
                val = 5;
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler autorizante: " + e.getMessage());
            val = 5;
        }


        /**
         * Validação de data de inicio
         */
        try{
            data_inicio = txt_data_inicio.getText().toString();
            if(data_inicio.isEmpty() && val == 0){
                val = 6;
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler data de inicio: " + e.getMessage());
            val = 6;
        }

        /**
         * Validação de data de fim
         */
        try{
            data_fim = txt_data_fim.getText().toString();
            if(data_fim.isEmpty() && val == 0){
                val = 7;
            }
        }
        catch (Exception e){
            Log.e("ERRO DE LEITURA", "Nao conseguiu ler data de fim: " + e.getMessage());
            val = 7;
        }


        /**
         * Tratamento de mensagem em caso de erro
         */
        if(val > 0){
            switch (val){
                case 1:
                    Toast.makeText(this, "Insira um nome valido",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    Toast.makeText(this, "Insira um documento valido",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 3:
                    Toast.makeText(this, "Insira um email valido",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 4:
                    Toast.makeText(this, "Insira um motivo",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 5:
                    Toast.makeText(this, "Escolha um autorizante",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 6:
                    Toast.makeText(this, "Insira uma data de inicio valida",
                            Toast.LENGTH_SHORT).show();
                    break;

                case 7:
                    Toast.makeText(this, "Insira uma data de fim valida",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        return true;
    }
}
