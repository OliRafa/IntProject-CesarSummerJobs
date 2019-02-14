package com.example.smartcapivara;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Entrar extends AppCompatActivity {

    private String ip_address;

    private EditText txt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                this.ip_address = extras.getString("ip_address");
                Log.d("ACTIVITY Entrar", "IP ADDRESS: " + this.ip_address );
            }
        }

        txt_login = (EditText) findViewById(R.id.txt_login);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    public void login(View v){
        String doc = txt_login.getText().toString();

        JSONObject cadastro = new JSONObject();

        try {
            cadastro.put("rg_passaporte", doc);

            String url = this.ip_address + "/auth";

            StringEntity entity = new StringEntity(cadastro.toString());

            AsyncHttpClient client = new AsyncHttpClient();

            Log.d("JSON", "Enviado para " + url + "\nDados: " + doc);
            client.post(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                    Log.d("JSON Requisicao", response.toString() );
                    try{
                        Pessoa usuario = new Pessoa();
                        usuario.setNome( response.getJSONObject("data").get("nome").toString() );
                        usuario.setID( response.getJSONObject("data").get("_id").toString() );
                        usuario.setRGPassaporte( response.getJSONObject("data").get("rg_passaporte").toString() );
                        usuario.setEmail( response.getJSONObject("data").get("email").toString() );
                        usuario.setTipo( response.get("tipo_usuario").toString() );

                        Log.d("JSON Requisicao", "Recebido: " + usuario.toString());

                        if(usuario.getTipo().equals("autorizante")){
                            Log.d("LOGIN", "LOG INTO AUTORIZANTE");
                            Intent intent = new Intent(Entrar.this, Historico.class);

                            intent.putExtra("usuario_nome", usuario.getNome());
                            intent.putExtra("usuario_id", usuario.getID());
                            intent.putExtra("usuario_tipo", usuario.getTipo());
                            intent.putExtra("usuario_documento", usuario.getRGPassaporte());
                            intent.putExtra("usuario_email", usuario.getEmail());


                            intent.putExtra("ip_address", Entrar.this.ip_address);
                            startActivity(intent);
                        }

                        else if(usuario.getTipo().equals("visitante")){
                            usuario.setDataInicio( response.getJSONObject("data").get("data_inicial").toString() );
                            usuario.setDataFim( response.getJSONObject("data").get("data_final").toString() );
                            usuario.setAutorizante( response.getJSONObject("data").get("autorizante").toString() );

                            try{
                                usuario.setFoto( response.getJSONObject("data").get("foto").toString() );
                                Log.i("FOTO BASE ", usuario.getFoto());
                            }
                            catch (JSONException e){
                                Log.e("PROBLEMA EM FOTO", e.getMessage());
                            }

                            Log.d("LOGIN", "LOG INTO VISITANTE");
                            Intent intent = new Intent(Entrar.this, Visitante.class);

                            intent.putExtra("usuario_nome", usuario.getNome());
                            intent.putExtra("usuario_id", usuario.getID());
                            intent.putExtra("usuario_tipo", usuario.getTipo());
                            intent.putExtra("usuario_documento", usuario.getRGPassaporte());
                            intent.putExtra("usuario_email", usuario.getEmail());
                            intent.putExtra("usuario_datainicio", usuario.getDataInicio());
                            intent.putExtra("usuario_datafim", usuario.getDataFim());
                            intent.putExtra("usuario_autorizante", usuario.getAutorizante());
                            intent.putExtra("usuario_foto", usuario.getFoto());


                            intent.putExtra("ip_address", Entrar.this.ip_address);
                            startActivity(intent);
                        }
                    }
                    catch (JSONException e) {
                        Log.e("JSON Requisicao", e.getMessage());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                    try {
                        Log.e("JSON Requisicao", errorResponse.toString());
                    }
                    catch (NullPointerException err ){
                        Log.e("JSON Requisicao", err.getMessage());
                    }
                }
            });
        }
        catch (Exception e) {
            Log.e("JSON Requisicao", e.getMessage());
        }
    }
}
