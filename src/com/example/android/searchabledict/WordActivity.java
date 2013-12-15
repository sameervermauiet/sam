package com.example.android.searchabledict;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
public class WordActivity extends Activity implements 
TextToSpeech.OnInitListener,OnItemSelectedListener  {
	Button b1;
	TextView word;
	TextView definition;
	private TextToSpeech tts;
	Spinner speed1;
	private static String speed="Normal";

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word);
b1=(Button) findViewById(R.id.button1);
speed1=(Spinner) findViewById(R.id.spinner1);
tts=new TextToSpeech(this, this);

loadSpinnerData();
speed1.setOnItemSelectedListener(this);

b1.setOnClickListener(new View.OnClickListener() {

	@Override
	public void onClick(View arg0) {
	
		setSpeed();
        speakOut();

		
	}
});
    
if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            ActionBar actionBar = getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Uri uri = getIntent().getData();
        Cursor cursor = managedQuery(uri, null, null, null, null);

        if (cursor == null) {
            finish();
        } else {
            cursor.moveToFirst();

             word = (TextView) findViewById(R.id.word);
            definition = (TextView) findViewById(R.id.definition);

            int wIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_WORD);
            int dIndex = cursor.getColumnIndexOrThrow(DictionaryDatabase.KEY_DEFINITION);

            word.setText(cursor.getString(wIndex));
            definition.setText(cursor.getString(dIndex));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case android.R.id.home:
                Intent intent = new Intent(this, SearchableDictionary.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

		    int result = tts.setLanguage(Locale.US);

		    if (result == TextToSpeech.LANG_MISSING_DATA
		            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
		        Log.e("TTS", "This Language is not supported");
		    } else {
		    	b1.setEnabled(true);
		        speakOut();
		    }

		} else {
		    Log.e("TTS", "Initilization Failed!");
		}

		}

		
	

private void speakOut() {
String text1 = word.getText().toString();
String text2 = definition.getText().toString();

tts.speak(text1, TextToSpeech.QUEUE_FLUSH, null);
tts.speak(text2, TextToSpeech.QUEUE_FLUSH, null);

}
public void onDestroy() {
	if (tts != null) {
	    tts.stop();
	    tts.shutdown();
	}
	super.onDestroy();
	}

private void setSpeed(){
	if(speed.equals("Very Slow")){
		tts.setSpeechRate(0.1f);
	}
	if(speed.equals("Slow")){
		tts.setSpeechRate(0.5f);
	}
	if(speed.equals("Normal")){
		tts.setSpeechRate(1.0f);//default 1.0
	}
	if(speed.equals("Fast")){
		tts.setSpeechRate(1.5f);
	}
	if(speed.equals("Very Fast")){
		tts.setSpeechRate(2.0f);
	}
}

private void loadSpinnerData() {
    List<String> lables = new ArrayList<String>();
    lables.add("Very Slow");
    lables.add("Slow");
    lables.add("Normal");
    lables.add("Fast");
    lables.add("Very Fast");
    
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, lables);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    speed1.setAdapter(dataAdapter);
    
  }




@Override
public void onItemSelected(AdapterView<?> parent, View arg1, int position, long arg3) {

    speed = parent.getItemAtPosition(position).toString();


	    Toast.makeText(parent.getContext(), "You selected: " + speed,
	            Toast.LENGTH_LONG).show();

}

@Override
public void onNothingSelected(AdapterView<?> arg0) {
	
}

}

