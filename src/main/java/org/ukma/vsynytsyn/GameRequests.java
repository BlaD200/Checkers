package org.ukma.vsynytsyn;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.ukma.vsynytsyn.dto.GameStatus;
import org.ukma.vsynytsyn.dto.JoinStatus;
import org.ukma.vsynytsyn.dto.MoveStatus;
import sun.plugin.dom.exception.InvalidStateException;

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


    public JoinStatus joinGame(String teamName) throws IOException {
        URL url = new URL(String.format("http://localhost:8081/game?team_name=%s", teamName));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setConnectTimeout(5000);
        con.setReadTimeout(5000);

        JoinStatus joinStatus = null;

        int status = con.getResponseCode();
        if (status > 299) {
            String errorResponse = getResponse(con.getErrorStream());
            System.err.println(errorResponse);

        } else {
            String jsonResponse = getResponse(con.getInputStream());
            joinStatus = objectMapper.readValue(jsonResponse, JoinStatus.class);

            color = joinStatus.getData().getColor();
            token = "Token " + joinStatus.getData().getToken();
        }
        con.disconnect();

        return joinStatus;
    }


    public MoveStatus move(int fromPosition, int toPosition) throws IOException {
        if (color == null || token == null)
            throw new InvalidStateException("Couldn't make move. Join the game first.");
        String requestJson = String.format("{\n" +
                "    \"move\": [%s, %s]\n" +
                "}", fromPosition, toPosition);

        URL url = new URL("http://localhost:8081/move");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");

        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(requestJson);
        out.flush();
        out.close();
        con.setConnectTimeout(1000);
        con.setReadTimeout(1000);

        MoveStatus moveStatus = null;

        int status = con.getResponseCode();
        if (status > 299) {
            String errorResponse = getResponse(con.getErrorStream());
            System.err.println(errorResponse);

        } else {
            String jsonResponse = getResponse(con.getInputStream());
            moveStatus = objectMapper.readValue(jsonResponse, MoveStatus.class);
        }
        con.disconnect();

        return moveStatus;
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
}
