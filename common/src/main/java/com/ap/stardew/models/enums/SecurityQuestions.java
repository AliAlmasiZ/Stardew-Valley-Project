package com.ap.stardew.models.enums;

public enum SecurityQuestions {
    COLOR("Whats your favorite color?", 1),
    PET("Whats your pet name?", 2),
    COUNTRY("Whats your favorite country?", 3),
    SPORT("Whats your favorite sport?", 4),
    GAME("Whats your favorite game?", 5),
    ;
    private final String question;
    private final int questionNumber;

    SecurityQuestions(String question, int questionNumber) {
        this.question = question;
        this.questionNumber = questionNumber;
    }

    public String getQuestion() {
        return question;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public static SecurityQuestions getQuestion(int questionNumber) {
        for (SecurityQuestions question : SecurityQuestions.values()) {
            if (questionNumber == question.questionNumber) {
                return question;
            }
        }
        return null;
    }

    public static String getQuestionList() {
        StringBuilder questions = new StringBuilder();
        for (SecurityQuestions question : SecurityQuestions.values()) {
            questions.append(question.getQuestionNumber()).append("- ").append(question.getQuestion()).append("\n");
        }
        return questions.toString();
    }

    @Override
    public String toString() {
        return question;
    }
}
