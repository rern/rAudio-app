package com.raudio;

import static com.raudio.R.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy( policy );
        setTheme( style.baseTheme );
        setContentView( layout.dialog );
        // get saved data
        SharedPreferences sharedPreferences = getSharedPreferences( "com.raudio_preferences", MODE_PRIVATE );
        String ipSaved = sharedPreferences.getString( "ip", "192.168.1." );
        // input text
        EditText editText = findViewById( id.editText );
        editText.setImeOptions( EditorInfo.IME_ACTION_DONE ); // for enter key
        editText.setInputType( InputType.TYPE_CLASS_NUMBER );
        editText.setInputType( InputType.TYPE_NUMBER_FLAG_DECIMAL );
        editText.setKeyListener( DigitsKeyListener.getInstance( "0123456789." ) );
        editText.setText( ipSaved );
        editText.requestFocus();
        // show keyboard
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        // button
        Button button= findViewById( id.button );
        button.setOnClickListener( v -> {
            String ipNew = editText.getText().toString();
            String ip4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
            if ( !ipNew.matches( ip4 ) ) { // ip valid
                dialogError( "valid:", ipNew );
                return;
            }
            try { // ip reachable
                Socket soc = new Socket();
                soc.connect( new InetSocketAddress( ipNew, 80 ), 2000 );
            } catch ( IOException ex ) {
                dialogError( "found:", ipNew );
                return;
            }
            // save data
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString( "ip", ipNew );
            editor.apply();
            // setup WebView
            setContentView( layout.activity_main );
            WebView webView = findViewById( id.webView );
            webView.setBackgroundColor( Color.BLACK );
            webView.setWebViewClient( new WebViewClient() );
            // enable javascript
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled( true );
            // load
            webView.loadUrl( "http://" + ipNew );
        } );
        editText.setOnEditorActionListener( ( v, actionId, event ) -> {
            if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                // trigger setPositiveButton()
                button.performClick();
                return true;
            }
            return false;
        } );
    }

    private void dialogError( String error, String ip ) {
        // dialog box
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );
        alertDialog.setIcon( android.R.drawable.ic_dialog_alert )
                .setTitle( "IP address not "+ error )
                .setMessage( "\n           "+ ip )
                .setPositiveButton( "Retry", ( dialog, which ) -> dialog.dismiss() );
        // set icon color red - must create() dialog object
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        ImageView imageView = dialog.findViewById( android.R.id.icon );
        if ( imageView != null ) imageView.setColorFilter( Color.parseColor( "#bb2828" ), android.graphics.PorterDuff.Mode.SRC_IN );
    }
}
