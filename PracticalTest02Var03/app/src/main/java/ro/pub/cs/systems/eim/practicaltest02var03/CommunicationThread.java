package ro.pub.cs.systems.eim.practicaltest02var03;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by student on 19.05.2017.
 */

public class CommunicationThread extends Thread {

    private ServerThread serverThread = null;
    private Socket socket = null;

    public CommunicationThread() {}

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }



    @Override
    public void run() {

        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            //obtain printer and writer on communication channel
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client !");
            //TODO read info_i
            String word = bufferedReader.readLine();
            if (word == null || word.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client !");
                return;
            }


            //obtain cache object reference
            HashMap<String, String> data = serverThread.getData();
            String webInformation = null;

            //TODO replace null with key
            if (data.containsKey(word)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                webInformation = data.get(word);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + word);
                //HttpResponse httpGetResponse = httpClient.execute(httpGet);
                //HttpEntity httpGetEntity = httpGetResponse.getEntity();

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpGet, responseHandler);

                /*HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, word));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);*/


                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }


                //get web response
                webInformation = pageSourceCode;
                data.put(word, webInformation);
            }
            if (webInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Web Information is null!");
                return;
            }
            String result = null;
            //TODO put information into result from webInformation object getter method for specific field
            printWriter.println(webInformation);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } /*catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } */finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
