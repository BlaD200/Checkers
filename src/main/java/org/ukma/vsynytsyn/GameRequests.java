package org.ukma.vsynytsyn;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.JoinStatus;
import org.ukma.vsynytsyn.dto.MoveStatus;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameRequests {

    @Getter
    private String color;
    @Getter
    private String token;

    private String authHeaderName = "Authorization";

    private ObjectMapper objectMapper = new ObjectMapper();


    public GameStatus gameStatus() throws IOException {
        URL url = new URL("http://localhost:8081/game");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);

        GameStatus gameStatus = null;

        int status = con.getResponseCode();
        if (status > 299) {
            String errorResponse = getResponse(con.getErrorStream());
            System.err.println(errorResponse);

        } else {
            String jsonResponse = getResponse(con.getInputStream());
            gameStatus = objectMapper.readValue(jsonResponse, GameStatus.class);
        }
        con.disconnect();

        return gameStatus;
    }


    @org.jetbrains.annotations.NotNull
    private String getResponse(InputStream inputStream) throws IOException {
        Reader streamReader;
        streamReader = new InputStreamReader(inputStream);
        BufferedReader in = new BufferedReader(streamReader);
        String inputLine;
        StringBuilder jsonResponse = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            jsonResponse.append(inputLine);
        }
        in.close();

        return jsonResponse.toString();
    }


    public JoinStatus joinGame() {
        color = "";
        token = "Token " + "";
        return null;
    }


    public MoveStatus move(int fromPosition, int toPosition) {
        return null;
    }
}
