package com.raudio;

import static com.raudio.R.*;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.DigitsKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
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

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                                                                    .permitAll()
                                                                    .build();
        StrictMode.setThreadPolicy( policy );
        // get saved data
        SharedPreferences sharedPreferences = getSharedPreferences( "com.raudio_preferences", MODE_PRIVATE );
        String ipSaved = sharedPreferences.getString( "ip", "192.168.1." );
        if ( !ipSaved.equals( "192.168.1." ) && ipReachable( ipSaved ) ) {
            loadPage( ipSaved );
            return;
        }
        // set page style
        setContentView( layout.startup );
        // input text
        EditText editText = findViewById( id.editText );
        editText.setText( ipSaved );
        // keyboard format
        editText.setImeOptions( EditorInfo.IME_ACTION_DONE ); // for enter key
        editText.setKeyListener( DigitsKeyListener.getInstance( "0123456789." ) ); // allow multiple dots
        editText.requestFocus(); // needed by showKeyboard()
        if ( ipSaved.equals( "192.168.1." ) ) showKeyboard();
        // button tap
        Button button= findViewById( id.button );
        button.setOnClickListener( v -> {
            String ipNew = editText.getText().toString();
            // validate
            String ip4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
            if ( !ipNew.matches( ip4 ) ) { // ip valid
                errorDialog( "valid", ipNew );
                return;
            }
            if ( !ipReachable( ipNew ) ) {
                errorDialog( "found", ipNew );
                return;
            }
            // save data
            sharedPreferences.edit()
                             .putString( "ip", ipNew )
                             .apply();
            loadPage( ipNew );
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

    public void errorDialog( String error, String ipNew ) {
        Dialog dialog = new Dialog( this );
        // set dialog style
        dialog.setContentView( layout.dialog );
        // title and body text
        TextView titleText = dialog.findViewById( id.titleText );
        String title = "IP address not "+ error +" !";
        titleText.setText( title );
        TextView bodyText = dialog.findViewById( id.bodyText );
        bodyText.setText( ipNew );
        // button tap
        Button button = dialog.findViewById( id.button );
        button.setOnClickListener( v -> {
            dialog.dismiss();
            showKeyboard();
        } );

        dialog.show();
    }

    public boolean ipReachable( String ip ) {
        try {
            Socket soc = new Socket();
            soc.connect( new InetSocketAddress( ip, 80 ), 2000 );
            return true;
        } catch ( IOException ex ) {
            return false;
        }
    }

    public class JsToAndroid { // call from js: NAME.FUNCTION()
        @JavascriptInterface
        public void changeIP( String ip ) {
            SharedPreferences sharedPreferences = getSharedPreferences( "com.raudio_preferences", MODE_PRIVATE );
            if ( ip == null ) {
                sharedPreferences.edit()
                                 .remove( "ip" )
                                 .apply();
            } else {
                sharedPreferences.edit()
                                 .putString( "ip", ip )
                                 .apply();
            }
        }
    }

    @SuppressLint( "SetJavaScriptEnabled" )
    public void loadPage( String ip ) {
        // set page layout to WebView
        setContentView( layout.activity_main );
        WebView webView = findViewById( id.webView );
        webView.setBackgroundColor( Color.BLACK );
        webView.setWebViewClient( new WebViewClient() );
        // enable javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled( true );
        // javascript to android
        webView.setWebChromeClient( new WebChromeClient() );
        webView.addJavascriptInterface( new JsToAndroid(), "Android" ); // name for js NAME.FUNCTION()
        // load
        webView.loadUrl( "http://" + ip );
    }

    public void showKeyboard() {
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE );
    }
}
