package ro.pub.cs.systems.eim.practicaltest02var03;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02Var03MainActivity extends AppCompatActivity {


    // Server widgets
    private EditText serverPortEditText = null;
    private Button connectButton = null;


    // Client widgets
    private EditText clientWordEditText = null;
    private Button getWebInformationButton = null;
    private TextView webInformationTextView = null;


    //network threads
    private ServerThread serverThread = null;
    private ClientThread clientThread = null;


    //ConnectListener
    private ConnectListener connectListener = new ConnectListener();
    private class ConnectListener implements Button.OnClickListener {

        @Override
        public void onClick(View v) {
            String serverPort = serverPortEditText.getText().toString();

            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Server port must be filled!", Toast.LENGTH_SHORT).show();
                return;
            }

            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread != null) {
                serverThread.start();
            } else {
                Log.e(Constants.TAG, "[Main Activity] Could not create server thread");
            }
        }
    }


    private GetWebInformationButtonClickListener getWebInformationButtonClickListener = new GetWebInformationButtonClickListener();
    private class GetWebInformationButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientPort   = serverPortEditText.getText().toString();

            if (serverThread == null || !serverThread.isAlive()) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] There is no server to connect to!");
                return;
            }

            //TODO get text from input fields
            String clientWord   = clientWordEditText.getText().toString();

            //set result text view to empty
            webInformationTextView.setText(Constants.EMPTY_STRING);

            //TODO start the client thread with all parameters
            clientThread = new ClientThread(clientPort, clientWord, webInformationTextView);
            clientThread.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_var03_main);

        //server view
        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectListener);

        //client view
        clientWordEditText = (EditText)findViewById(R.id.client_word_edit_text);
        //TODO rest of info
        getWebInformationButton = (Button)findViewById(R.id.get_web_info_button);
        getWebInformationButton.setOnClickListener(getWebInformationButtonClickListener);

        webInformationTextView = (TextView)findViewById(R.id.web_info_text_view);

    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopThread();
        }

        super.onDestroy();
    }
}
