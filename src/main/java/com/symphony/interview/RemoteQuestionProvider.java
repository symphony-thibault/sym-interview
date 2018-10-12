package com.symphony.interview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * /!\ Can't be modified /!\
 *
 * Issued from : https://opentdb.com/api_config.php
 */
@Service
public class RemoteQuestionProvider {

  private static final int DEFAULT_CATEGORY = 18; // computer

  public enum Difficulty { easy, medium, hard }

  @Data
  public static class Question {

    private String category;
    private String type;
    private Difficulty difficulty;
    private String question;
    @JsonProperty("correct_answer")
    private String correctAnswer;
    @JsonProperty("incorrect_answers")
    private List<String> incorrectAnswers;
  }

  @Data
  private static class RemoteResponse {
    @JsonProperty("response_code")
    private int responseCode;
    private List<Question> results;
  }

  public Question getRandomQuestion(Difficulty difficulty) {
    final RemoteResponse response = new RestTemplate()
        .getForObject("https://opentdb.com/api.php?amount=1&category={category}&difficulty={difficult}&type=boolean",
            RemoteResponse.class,
            DEFAULT_CATEGORY,
            difficulty
        );
    return response.results.get(0);
  }
}
