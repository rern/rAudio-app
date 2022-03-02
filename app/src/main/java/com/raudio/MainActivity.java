package com.raudio;

import static com.raudio.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        setContentView( layout.activity_startup );
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
        // force show keyboard - needs requestFocus()
        if ( ipSaved.equals( "192.168.1." ) ) getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        // button
        Button button= findViewById( id.button );
        button.setOnClickListener( v -> {
            String ipNew = editText.getText().toString();
            // validate
            String ip4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
            if ( !ipNew.matches( ip4 ) ) { // ip valid
                CustomDialog customDialog = new CustomDialog();
                customDialog.showDialog( this, "valid", ipNew );
                return;
            }
            try { // ip reachable
                Socket soc = new Socket();
                soc.connect( new InetSocketAddress( ipNew, 80 ), 2000 );
            } catch ( IOException ex ) {
                CustomDialog customDialog = new CustomDialog();
                customDialog.showDialog( this, "found", ipNew );
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
        // keyboard enter key
        editText.setOnEditorActionListener( ( v, actionId, event ) -> {
            if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                button.performClick();
                return true;
            }
            return false;
        } );
    }

    public static class CustomDialog {
        public void showDialog( Activity activity, String error, String ip ){
            final Dialog dialog = new Dialog( activity );
            dialog.requestWindowFeature( Window.FEATURE_NO_TITLE );
            dialog.setCancelable( false );
            dialog.setContentView( R.layout.dialog );

            TextView titleText = ( TextView ) dialog.findViewById( R.id.titleText );
            String msg = "IP address not "+ error +" !";
            titleText.setText( msg );
            TextView bodyText = ( TextView ) dialog.findViewById( R.id.bodyText );
            bodyText.setText( ip );

            Button button = ( Button ) dialog.findViewById( R.id.button );
            button.setOnClickListener( v -> dialog.dismiss() );
            dialog.show();
        }
    }
}
