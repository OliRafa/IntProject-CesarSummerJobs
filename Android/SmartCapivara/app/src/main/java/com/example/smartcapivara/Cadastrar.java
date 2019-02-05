package com.example.smartcapivara;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class Cadastrar extends AppCompatActivity {

    private EditText txt_nome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        txt_nome = (EditText) findViewById(R.id.txt_nome);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setVariables();

        /*loadWebFromURL();*/

    }

    public boolean onOptionsItemSelected(MenuItem item){

        /*Toast.makeText(this, "Das kann uns keiner nehmen",
                Toast.LENGTH_SHORT).show();*/

        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    public void setVariables(){

    }

    public void loadWebFromURL(){

        String url = "https://docs.google.com/forms/d/19FCKcKzP2reXIW3NGG-EdrJnYFYR57mjEvCIYjgefIc/viewform?edit_requested=true";

        WebView webView = new WebView(this);
        setContentView(webView);
        webView.loadUrl(url);
    }

    public void concluir(View v){
        Toast.makeText(this, txt_nome.getText(),
                Toast.LENGTH_SHORT).show();

        JSONObject cadastroJSON = new JSONObject();
        try {
            cadastroJSON.put("name", txt_nome.getText().toString());

            //new SendDeviceDetails().execute("http://52.88.194.67:8080/IOTProjectServer/registerDevice", cadastroJSON.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
