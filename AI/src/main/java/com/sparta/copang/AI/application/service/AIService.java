package com.sparta.copang.AI.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.copang.AI.domain.model.AI;
import com.sparta.copang.AI.domain.repository.AIRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Slf4j
@Service
public class AIService {

    @Value("${gemini.api-key}")
    private String api_Key;

    private final RestTemplate restTemplate;
    private final AIRepository aiRepository;

    @Autowired
    public AIService(RestTemplateBuilder builder, AIRepository aiRepository) {
        this.restTemplate = builder.build();
        this.aiRepository = aiRepository;
    }

    public String requestPrompt(String prompt) {
        String response = extractTextFromResponse(sendRequest(prompt));

        AI ai = AI.builder().
                ai_id(UUID.randomUUID())
                .request(prompt)
                .response(response)
                .build();

        aiRepository.save(ai);
        return response;
    }

    public String sendRequest(String prompt) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://generativelanguage.googleapis.com")
                .path("/v1beta/models/gemini-1.5-flash-latest:generateContent")
                .queryParam("key", api_Key)
                .build()
                .toUri();

        log.info("uri: {}", uri);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // 요청 바디 생성
        Map<String, Object> parts = new HashMap<>();
        parts.put("text", prompt + "답변을 최대한 간결하게 50자 이하로");

        List<Map<String, Object>> partsList = new ArrayList<>();
        partsList.add(parts);

        Map<String, Object> contents = new HashMap<>();
        contents.put("parts", partsList);

        List<Map<String, Object>> contentsList = new ArrayList<>();
        contentsList.add(contents);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", contentsList);

        // HTTP 요청 엔티티 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // POST 요청을 보내고 응답을 받아옵니다.
        ResponseEntity<String> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                entity,
                String.class
        );


        log.info("statusCode = {}", responseEntity.getStatusCode());

        // 응답 바디를 반환합니다.
        return responseEntity.getBody();

    }

    // JSON 응답에서 text 값을 추출하는 메서드
    private String extractTextFromResponse(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode textNode = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text");
            return textNode.asText();
        } catch (Exception e) {
            log.error("Error parsing response JSON", e);
            return null;
        }
    }
}
