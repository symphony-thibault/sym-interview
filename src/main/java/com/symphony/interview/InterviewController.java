package com.symphony.interview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

@RestController
@Scope("singleton")
@RequestMapping(path = "/interview", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class InterviewController {

  private Logger logger = LoggerFactory.getLogger("logger");

  @Autowired
  private RemoteQuestionProvider remoteQuestionProvider;

  private List<RemoteQuestionProvider.Question> questions;
  private int selectedQuestionId = -1;

  @GetMapping("/{count}")
  public String getQuestions(@PathVariable int count, @RequestParam(defaultValue = "easy", required = false) RemoteQuestionProvider.Difficulty difficulty) {
    questions = new LinkedList<>();
    for (int i = 0; i < count; i++) {
      questions.add(this.remoteQuestionProvider.getRandomQuestion(difficulty));
    }

    StringBuilder response = new StringBuilder();

    for (int i = 0; i < questions.size(); i++) {
      response.append(String.format("%02d", i+1) + "   " + questions.get(i).getQuestion()).append("\n");
    }

    return response.toString();
  }

  @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
  public String selectQuestion(@PathVariable String id) {
    selectedQuestionId = Integer.parseInt(id);
    logger.debug(String.format("the candidate selected question with id %s", id));
    return "Question to answer: " + questions.get(selectedQuestionId - 1).getQuestion();
  }

  @PutMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
  public String answerQuestion(@RequestBody String response) {

    if(selectedQuestionId < 0) {
      throw new IllegalStateException("You must select a question before.");
    }

    final RemoteQuestionProvider.Question question = questions.get(selectedQuestionId - 1);

    if(Boolean.parseBoolean(question.getCorrectAnswer().toLowerCase()) == Boolean.parseBoolean(response)) {
      return "Correct answer =)";
    }

    return "Incorrect answer :(";
  }
}
