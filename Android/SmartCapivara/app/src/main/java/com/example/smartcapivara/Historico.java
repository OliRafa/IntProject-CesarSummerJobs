package com.example.smartcapivara;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Historico extends AppCompatActivity {

    private String ip_address;

    private ListView lista_validacoes;
    final ArrayList<String> lista_validacoes_nome = new ArrayList<String>();

    private Pessoa gestor;
    private ArrayList<Pessoa> solicitantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                gestor = new Pessoa();

                gestor.setNome( extras.getString("usuario_nome") );
                gestor.setEmail( extras.getString("usuario_email") );
                gestor.setRGPassaporte( extras.getString("usuario_documento") );
                gestor.setID( extras.getString("usuario_id") );
                gestor.setTipo( extras.getString("usuario_tipo") );
                Log.d("Dados Gestor", gestor.toString() );

                this.ip_address = extras.getString("ip_address");
                Log.d("ACTIVITY Historico", "IP ADDRESS: " + this.ip_address );
            }
        }

        lista_validacoes = (ListView) findViewById(R.id.lista_solicitacoes);
        solicitantes = new ArrayList<>();

        loadData();

        lista_validacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Historico.this.seeRequest(position);
            }

        });
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void seeRequest(int position){
        Pessoa visitante = solicitantes.get(position);

        Log.d("Solicitante", visitante.toString());

        Intent intent = new Intent(Historico.this, Gestor.class);

        intent.putExtra("gestor_nome", gestor.getNome());
        intent.putExtra("gestor_id", gestor.getID());
        intent.putExtra("gestor_tipo", gestor.getTipo());
        intent.putExtra("gestor_documento", gestor.getRGPassaporte());
        intent.putExtra("gestor_email", gestor.getEmail());

        intent.putExtra("visitante_nome", visitante.getNome());
        intent.putExtra("visitante_id", visitante.getID());
        intent.putExtra("visitante_tipo", visitante.getTipo());
        intent.putExtra("visitante_documento", visitante.getRGPassaporte());
        intent.putExtra("visitante_email", visitante.getEmail());
        intent.putExtra("visitante_datainicio", visitante.getDataInicio());
        intent.putExtra("visitante_datafim", visitante.getDataFim());
        intent.putExtra("visitante_autorizante", visitante.getAutorizante());
        intent.putExtra("visitante_cargo", visitante.getCargo());
        intent.putExtra("visitante_motivo", visitante.getMotivo());
        intent.putExtra("visitante_foto", visitante.getFoto());

        intent.putExtra("ip_address", Historico.this.ip_address);
        startActivity(intent);

    }

    private void loadData(){

        JSONObject cadastro = new JSONObject();

        try {
            cadastro.put("autorizante", gestor.getNome());

            String url = this.ip_address + "/push";

            StringEntity entity = new StringEntity(cadastro.toString());

            AsyncHttpClient client = new AsyncHttpClient();

            Log.d("JSON", "Enviado: " + cadastro.toString());
            client.post(getApplicationContext(), url, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response)                {
                    Log.d("JSON Requisicao", "Recebido: " + response.toString());

                    for(int i=0;i<response.length();i++){
                        try{
                            JSONObject aux = response.getJSONObject(i);

                            Pessoa solicitante = new Pessoa();
                            solicitante.setNome( aux.get("nome").toString() );
                            solicitante.setID( aux.get("_id").toString() );
                            solicitante.setEmail( aux.get("email").toString() );
                            solicitante.setFone( aux.get("fone").toString() );
                            solicitante.setRGPassaporte( aux.get("rg_passaporte").toString() );
                            solicitante.setDataInicio( aux.get("data_inicial").toString() );
                            solicitante.setDataFim( aux.get("data_final").toString() );
                            solicitante.setMotivo( aux.get("motivo").toString() );
                            solicitante.setAutorizante( aux.get("autorizante").toString() );

                            try{
                                solicitante.setFoto( aux.get("foto").toString() );
                                Log.i("FOTO BASE ", solicitante.getFoto());
                            }
                            catch (JSONException e){
                                Log.e("PROBLEMA EM FOTO", e.getMessage());
                            }

                            Historico.this.lista_validacoes_nome.add( aux.get("nome").toString() );

                            Log.i("NOVO SOLICITANTE",solicitante.toString());

                            Historico.this.solicitantes.add(solicitante);
                        }
                        catch (JSONException e){
                            Log.e("JSONArray", e.getMessage());
                        }
                    }

                    Historico.this.setListView();
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

    private void setListView(){
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, lista_validacoes_nome);
        lista_validacoes.setAdapter(adapter);
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

    /*class RequisicaoPush implements Runnable{

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
    }*/
}
