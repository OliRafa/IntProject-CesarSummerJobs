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

    private String ip = "http://179.106.208.38:8080";

    EditText txt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_login = (EditText) findViewById(R.id.txt_login);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void login(View v){
        String doc = txt_login.getText().toString();

        /*Log.d("TESTE", doc);*/

        JSONObject cadastro = new JSONObject();

        try {
            cadastro.put("rg_passaporte", doc);

            String url = this.ip + "/auth";

            StringEntity entity = new StringEntity(cadastro.toString());

            AsyncHttpClient client = new AsyncHttpClient();

            Log.d("JSON", "Enviado para " + url);
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
                            Intent intent = new Intent(Entrar.this, Gestor.class);
                            intent.putExtra("usuario_nome", usuario.getNome());
                            intent.putExtra("usuario_id", usuario.getID());
                            intent.putExtra("usuario_tipo", usuario.getTipo());
                            intent.putExtra("usuario_documento", usuario.getRGPassaporte());
                            intent.putExtra("usuario_email", usuario.getEmail());
                            startActivity(intent);
                        }
                    }
                    catch (JSONException e) {
                        Log.e("JSON Requisicao", e.getMessage());
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
}
