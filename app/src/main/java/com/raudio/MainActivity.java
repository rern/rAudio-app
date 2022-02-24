package com.raudio;

import static com.raudio.R.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( layout.activity_main );
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy( policy );
        dialogIP( false );
    }

    @SuppressLint( "SetJavaScriptEnabled" )
    private void dialogIP( boolean keyboard ) {
        // setup WebView
        WebView webView = findViewById( id.webView );
        webView.setBackgroundColor( Color.BLACK );
        webView.setWebViewClient( new WebViewClient() );
        // enable javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled( true );
        // get saved data
        SharedPreferences sharedPreferences = getSharedPreferences( "com.raudio_preferences", MODE_PRIVATE );
        String ipSaved = sharedPreferences.getString( "ip", "192.168.1." );
        // setup input text for dialog box
        EditText editText = new EditText( this );
        editText.setImeOptions( EditorInfo.IME_ACTION_DONE ); // for enter key
        editText.setInputType( InputType.TYPE_CLASS_NUMBER );
        editText.setInputType( InputType.TYPE_NUMBER_FLAG_DECIMAL );
        editText.setKeyListener( DigitsKeyListener.getInstance( "0123456789." ) );
        editText.setSingleLine();
        editText.setTextAlignment( WebView.TEXT_ALIGNMENT_CENTER );
        editText.setText( ipSaved );
        boolean showKeyboard = keyboard || !validIP4( ipSaved );
        if ( showKeyboard ) editText.requestFocus();
        // set stroke color
        editText.setBackgroundResource( R.drawable.edit_text );
        // input text margin - put EditText inside LinearLayout > set margins of layout
        LinearLayout layout = new LinearLayout( this );
        layout.setOrientation( LinearLayout.VERTICAL );
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );
        params.setMargins( 70, 50, 70, 0 );
        layout.addView( editText, params );
        // dialog box
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );
        alertDialog.setIcon( mipmap.ic_launcher )
                .setTitle( "IP Address" )
                .setView( layout )
                .setPositiveButton( "Ok", ( dialog, whichButton ) -> {
                            String ipNew = editText.getText().toString();
                            if ( !validIP4( ipNew ) ) {
                                dialogError( "Not valid: "+ ipNew );
                                return;
                            }
                            if ( !reachableIP( ipNew ) ) {
                                dialogError( "Not found: "+ ipNew );
                                return;
                            }
                            // save data
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString( "ip", ipNew );
                            editor.apply();
                            webView.loadUrl( "http://" + ipNew );

                        } )
                .setNegativeButton( "Cancel", ( dialog, which ) -> finish() );
        // show keyboard and enter key press - must create() dialog object
        AlertDialog dialog = alertDialog.create();
        if ( showKeyboard ) dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE );
        dialog.show();
        // enter key press
        editText.setOnEditorActionListener( ( v, actionId, event ) -> {
            if ( actionId == EditorInfo.IME_ACTION_DONE ) {
                // trigger setPositiveButton()
                dialog.getButton( DialogInterface.BUTTON_POSITIVE ).performClick();
                return true;
            }
            return false;
        });
    }

    private void dialogError( String message ) {
        // hide keyboard
        InputMethodManager imm = ( InputMethodManager ) getSystemService( Activity.INPUT_METHOD_SERVICE );
        imm.toggleSoftInput( InputMethodManager.HIDE_IMPLICIT_ONLY, 0 );
        // dialog box
        AlertDialog.Builder alertDialog = new AlertDialog.Builder( this );
        alertDialog.setIcon( mipmap.ic_launcher )
                .setTitle( "IP Address" )
                .setMessage( "\n          "+ message )
                .setPositiveButton( "Retry", ( dialog, which ) -> dialogIP( true ) )
                .setNegativeButton( "Cancel", ( dialog, which ) -> finish() )
                .show();
    }

    private boolean reachableIP( String ip ) {
        try {
            try ( Socket soc = new Socket() ) {
                soc.connect( new InetSocketAddress( ip, 80 ), 2000 );
                return true;
            }
        } catch ( IOException ex ) {
            return false;
        }
    }
    private boolean validIP4( String ip ) {
        String ip4 = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches( ip4 );
    }
}
