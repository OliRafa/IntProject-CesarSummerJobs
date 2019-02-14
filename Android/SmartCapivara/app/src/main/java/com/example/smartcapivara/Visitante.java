package com.example.smartcapivara;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Visitante extends AppCompatActivity {

    private Pessoa usuario;
    private String ip_address;

    private TextView txt_nome, txt_datainicio, txt_datafim, txt_autorizante, txt_documento;

    private Bitmap bmpQR;
    private ImageView visitantePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitante);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_nome = (TextView) findViewById(R.id.vis_txt_nome);
        txt_datainicio = (TextView) findViewById(R.id.vis_txt_datainicio);
        txt_datafim = (TextView) findViewById(R.id.vis_txt_datafim);
        txt_autorizante = (TextView) findViewById(R.id.vis_txt_autorizante);
        txt_documento = (TextView) findViewById(R.id.vis_txt_documento);

        visitantePicture = (ImageView) findViewById(R.id.vis_img_cliente);

        this.usuario = new Pessoa();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            if(extras != null) {
                usuario.setNome( extras.getString("usuario_nome") );
                usuario.setEmail( extras.getString("usuario_email") );
                usuario.setRGPassaporte( extras.getString("usuario_documento") );
                usuario.setID( extras.getString("usuario_id") );
                usuario.setTipo( extras.getString("usuario_tipo") );
                usuario.setDataInicio( extras.getString("usuario_datainicio") );
                usuario.setDataFim( extras.getString("usuario_datafim") );
                usuario.setAutorizante( extras.getString("usuario_autorizante") );
                usuario.setFoto( extras.getString("usuario_foto") );

                this.ip_address = extras.getString("ip_address");

                Log.d("ACTIVITY Visitante", "IP ADDRESS: " + this.ip_address );
                Log.d("USUARIO", usuario.toString());

            }
        }

        loadData();
        loadQRCode();
    }

    public boolean onOptionsItemSelected(MenuItem item){

        finish();

        return true;
    }

    private void loadData(){
        txt_nome.setTextColor(Color.WHITE);
        txt_datainicio.setTextColor(Color.WHITE);
        txt_datafim.setTextColor(Color.WHITE);
        txt_autorizante.setTextColor(Color.WHITE);
        txt_documento.setTextColor(Color.WHITE);

        txt_nome.setTextSize(20);
        txt_datainicio.setTextSize(18);
        txt_datafim.setTextSize(18);
        txt_autorizante.setTextSize(18);
        txt_documento.setTextSize(18);

        txt_nome.setText( usuario.getNome() );
        txt_datainicio.setText( usuario.getDataInicio() );
        txt_datafim.setText( usuario.getDataFim() );
        txt_autorizante.setText( usuario.getAutorizante() );
        txt_documento.setText( usuario.getRGPassaporte() );

        try{
            byte[] decodedString = Base64.decode(usuario.getFoto(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            visitantePicture.setImageBitmap(decodedByte);

            Log.d("BASE64" , usuario.getFoto() );
        }
        catch (Exception e){
            Log.e("PROBLEM LOADING PICTURE", e.getMessage());
        }

    }

    private void loadQRCode(){
        String url = ip_address + "/visitante" + "/" + this.usuario.getID();
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.d("GET QRCODE Start", "Load QRCode");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.d("GET QRCODE Response", response.toString() );

                Visitante.this.bmpQR = BitmapFactory.decodeByteArray(response, 0, response.length);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e("GET QRCODE Error", errorResponse.toString() );
            }
        });
    }

    public void btnQRCode(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.custom_layout, null);

        ImageView qrCode = (ImageView)  dialogLayout.findViewById(R.id.image_to_show);

        qrCode.setImageBitmap(bmpQR);

        builder.setPositiveButton("OK", null);
        builder.setView(dialogLayout);
        builder.show();
    }
}
