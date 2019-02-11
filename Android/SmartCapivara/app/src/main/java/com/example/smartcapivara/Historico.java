package com.example.smartcapivara;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Historico extends AppCompatActivity {

    private String ip_address;

    private ListView validacoes;

    private Pessoa gestor;
    private ArrayList<Pessoa> solicitantes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                this.ip_address = extras.getString("ip_address");
                Log.d("ACTIVITY STARTED", "IP ADDRESS" + this.ip_address );
            }
        }
         */

        validacoes = (ListView) findViewById(R.id.lista_solicitacoes);
        solicitantes = new ArrayList<>();

        String[] nomes = new String[] { "Luffy", "Katakuri", "Doflamingo",
                "Akainu", "Aokiji", "Kizaru", "Sengoku", "Garp",
                "Mihawk", "Crocodile", "Hancock", "Law", "Kid", "Zoro",
                "Marco", "Charlotte", "Jinbe", "Lucci", "Enel", "Whitebeard",
                "Blackbeard", "Roger", "Shanks" };

        final ArrayList<String> nome = new ArrayList<String>();
        for (int i = 0; i < nomes.length; ++i) {
            nome.add(nomes[i]);

            Pessoa usuario = new Pessoa();
            usuario.setNome(nomes[i]);
            usuario.setID(Integer.toString(i));
            solicitantes.add(usuario);
        }
        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, nome);
        validacoes.setAdapter(adapter);

        validacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
        String message = "Nome: " + Historico.this.solicitantes.get(position).getNome() +
                "\tID: " + Historico.this.solicitantes.get(position).getID() + "\n";

        Toast.makeText(Historico.this, message, Toast.LENGTH_SHORT).show();

        /*
        Log.d("LOGIN", "LOG INTO AUTORIZANTE");
        Intent intent = new Intent(Historico.this, Gestor.class);

        intent.putExtra("usuario_nome", gestor.getNome());
        intent.putExtra("usuario_id", gestor.getID());
        intent.putExtra("usuario_tipo", gestor.getTipo());
        intent.putExtra("usuario_documento", gestor.getRGPassaporte());
        intent.putExtra("usuario_email", gestor.getEmail());
        intent.putExtra("usuario_email", gestor.getEmail());

        intent.putExtra("visitante_id", solicitantes.get(position).getID() );

        intent.putExtra("ip_address", Historico.this.ip_address);
        startActivity(intent);
         */
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
}
