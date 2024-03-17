package com.example.quiz.service;

import com.example.quiz.dto.QuizCheck;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuizRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class QuizService {

    @Autowired
    QuizRepository quizRepository;
    ArrayList<Double> ratios = new ArrayList<>();
    ArrayList<Integer> order;
    int score;
    Long quizSize;


    private static String post(String apiUrl, Map<String, String> requestHeaders, String requestBody) {
        HttpURLConnection con = connect(apiUrl);

        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(requestBody.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect(); // Connection을 재활용할 필요가 없는 프로세스일 경우
        }
    }

    private static HttpURLConnection connect(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection) url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body) {
        InputStreamReader streamReader = new InputStreamReader(body, StandardCharsets.UTF_8);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }

    private static String search(String keyword, String category){
        String clientId = "8_7VAfuaP6qAF3H22ZtB"; //애플리케이션 클라이언트 아이디
        String clientSecret = "hAOM8Q_RAH"; //애플리케이션 클라이언트 시크릿

        String apiUrl = "https://openapi.naver.com/v1/datalab/shopping/category/keyword/gender";

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        requestHeaders.put("Content-Type", "application/json");

        String requestBody = "{" +
                "\"startDate\":\"2024-02-01\"," +
                "\"endDate\":\"2024-02-29\"," +
                "\"timeUnit\":\"month\"," +
                "\"category\":\"" + category + "\"," +
                "\"keyword\":\"" + keyword + "\"," +
                "\"device\":\"\"," +
                "\"gender\":\"\"," +
                "\"ages\":[]" +
                "}";

        String responseBody = post(apiUrl, requestHeaders, requestBody);
        return responseBody;
    }

    public boolean check(QuizCheck quizCheck){
        if(quizCheck.getLr() == 1){ //왼쪽
            if(ratios.get(1) > ratios.get(0))
                return true;
            else
                return false;
        }
        else{ // 오른쪽
            if(ratios.get(0) > ratios.get(1))
                return true;
            else
                return false;
        }
    }

    public long start(){
        score = 0;
        order = new ArrayList<>();
        quizSize = quizRepository.findMaxId();
        for(int i = 1; i <= quizSize; i++)
            order.add(i);
        Collections.shuffle(order);
        return (long)order.get(score);
    }

    public long nextRound(){
        score = score + 1;
        if(score == quizSize)
            return 0l;
        return (long)order.get(score);
    }

    public ArrayList<Double> progress(Long id) {

        Quiz q = quizRepository.findById(id).orElse(null);
        String keyword = q.getKeyword();
        String category = q.getCategory();
        String res = search(keyword, category);

        try {
            JSONObject jsonRes = new JSONObject(res);

            JSONArray resultsArray = jsonRes.getJSONArray("results");
            JSONObject resultObj = resultsArray.getJSONObject(0);

            JSONArray data = resultObj.getJSONArray("data");

            ratios.clear();
            for (int i = 0; i < data.length(); i++) {
                JSONObject dataObj = data.getJSONObject(i);
                ratios.add(dataObj.getDouble("ratio"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ratios;
    }

    public String getKeyword(Long id) {
        Quiz q = quizRepository.findById(id).orElse(null);
        return q.getKeyword();
    }

    public int getScore() {
        return score;
    }
}
