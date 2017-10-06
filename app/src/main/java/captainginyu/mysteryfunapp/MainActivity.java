package captainginyu.mysteryfunapp;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech speaker;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speaker = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speaker.setLanguage(Locale.US);
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Problem initializing speech", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button = (Button) findViewById(R.id.speak_button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speaker.setPitch(0.5f);
                speaker.speak("Hello", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(1f);
                speaker.speak("wow", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(2f);
                speaker.speak("poop", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(0.5f);
                speaker.speak("Hello", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(1f);
                speaker.speak("wow", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(2f);
                speaker.speak("poop", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(0.5f);
                speaker.speak("Hello", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(1f);
                speaker.speak("wow", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(2f);
                speaker.speak("poop", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(0.5f);
                speaker.speak("Hello", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(1f);
                speaker.speak("wow", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(2f);
                speaker.speak("poop", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(0.5f);
                speaker.speak("Hello", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(1f);
                speaker.speak("wow", TextToSpeech.QUEUE_ADD, null, "test");
                speaker.setPitch(2f);
                speaker.speak("poop", TextToSpeech.QUEUE_ADD, null, "test");
            }
        });
    }
}
