package com.example.carla.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


public class MainActivity extends ActionBarActivity
{

    public enum CMD_SPEECH
    {
        ACENDER_LUZ,
        LIGAR_AR,
        APAGAR_LUZ
    }

    private Button buttonOk;
    private Button buttonNextScreen;
    private Activity thisActivity;
    private final int REQ_CODE_SPEECH_INPUT = 500;
    private TextView textViewResults;
    private int wordsFound;
    private Queue<CMD_SPEECH> commandQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commandQueue = new LinkedBlockingQueue<CMD_SPEECH>();

        thisActivity = this;
        buttonOk = (Button) findViewById(R.id.buttonFalar);
        buttonNextScreen = (Button) findViewById(R.id.buttonNextScreen);
        textViewResults = (TextView) findViewById(R.id.textViewResults);

        buttonOk.setOnClickListener(buttonOk_Click);
        buttonNextScreen.setOnClickListener(buttonNextScreen_Click);
    }

    View.OnClickListener buttonOk_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(thisActivity);
            alertBuilder.setTitle("Olá!");
            alertBuilder.setMessage("Eu sou o universo. Converse comigo apertando este botão.");
            alertBuilder.setNeutralButton("Falar", buttonSpeak_Click);
            alertBuilder.show();
        }
    };

    DialogInterface.OnClickListener buttonSpeak_Click = new DialogInterface.OnClickListener()
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            promptSpeechInput();
        }
    };

    View.OnClickListener buttonNextScreen_Click = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Intent i = new Intent(getApplicationContext(), MainActivity2Activity.class);
            i.putExtra("refTexto", "E aí, nego drama?");
            startActivity(i);
        }
    };

    private void promptSpeechInput()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //putExtra parâmetros = referência, valor.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_message));
        try
        {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e)
        {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_message_not_supported), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQ_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && data != null)
                {
                    ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textViewResults.setText(results.get(0));

                    findInSpeech(results.get(0));
                }
            }
        }
    }

    private void findInSpeech(String result)
    {
        if(result.contains("acender") && result.contains("luz"))
        {
            commandQueue.add(CMD_SPEECH.ACENDER_LUZ);
        }
        if(result.contains("ligar") && result.contains("ar condicionado"))
        {
            commandQueue.add(CMD_SPEECH.LIGAR_AR);
        }
        if(result.contains("apagar") && result.contains("luz"))
        {
            commandQueue.add(CMD_SPEECH.APAGAR_LUZ);
        }

        processCommand();
    }

    private void processCommand()
    {
        while (!commandQueue.isEmpty())
        {
            switch (commandQueue.remove())
            {
                case ACENDER_LUZ:
                    Toast.makeText(getApplicationContext(), "Luzes acesas.", Toast.LENGTH_SHORT).show();
                    break;
                case LIGAR_AR:
                    Toast.makeText(getApplicationContext(), "Ar ligado.", Toast.LENGTH_SHORT).show();
                    break;
                case APAGAR_LUZ:
                    Toast.makeText(getApplicationContext(), "Luzes apagadas", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getApplicationContext(), "Desculpe, pode repetir?", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
