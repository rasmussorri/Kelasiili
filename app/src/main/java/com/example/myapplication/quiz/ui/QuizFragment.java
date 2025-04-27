package com.example.myapplication.quiz.ui;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myapplication.quiz.model.Question;
import com.example.myapplication.quiz.data.QuizQuestionGenerator;
import com.example.myapplication.R;
import com.example.myapplication.municipality.model.MunicipalityInfo;
import com.example.myapplication.core.util.SearchedMunicipalitiesManager;

import java.util.Collections;
import java.util.List;


public class QuizFragment extends Fragment {

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView questionText;
    private TextView questionCounterTextView;
    private RadioGroup answersGroup;
    private Button nextButton;


    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        questionText = view.findViewById(R.id.questionTextView);
        questionCounterTextView = view.findViewById(R.id.questionCounterTextView);
        answersGroup = view.findViewById(R.id.answersRG);
        nextButton = view.findViewById(R.id.nextButton);
        // Inflate the layout for this fragment

        // Tekee seuraava-napista läpinäkyvän kunnes käyttäjä on valinnut vaihtoehdon
        nextButton.setAlpha(0.5f);
        answersGroup.setOnCheckedChangeListener((group, checkedId) -> {
            nextButton.setAlpha(1.0f);
        });

        loadQuestions();

        nextButton.setOnClickListener(v -> {
            int selectedId = answersGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                Question current = questionList.get(currentQuestionIndex);
                if (selectedId == current.getCorrectAnswerIndex()) {
                    score++;
                }

                currentQuestionIndex++;
                if (currentQuestionIndex < questionList.size()) {
                    displayQuestion(questionList.get(currentQuestionIndex));
                } else {
                    showQuizResult();
                }
            }
        });

        return view;
    }
    private void loadQuestions() {
        List<MunicipalityInfo> municipalities = SearchedMunicipalitiesManager.getAll();

        // Jos kunnissa ei ole tarpeeksi tietoa
        if (municipalities.size() < 3) {
            questionList = Collections.singletonList(new Question(
                    "Ei tarpeeksi kuntia quizin tekemiseen.\nHae ensin vähintään 3 kuntaa!",
                    new String[]{"OK"}, 0
            ));
        } else {
            questionList = QuizQuestionGenerator.generateQuestions(municipalities);
        }

        currentQuestionIndex = 0;
        displayQuestion(questionList.get(currentQuestionIndex));
    }

    private void displayQuestion(Question q) {
        // Päivitä laskuri: “Kysymys X/10”
        int total = questionList.size();
        questionCounterTextView.setText(
                "Kysymys " + (currentQuestionIndex + 1) + "/" + total
        );

        // Varsinainen kysymys ja vaihtoehdot
        questionText.setText(q.getQuestionText());
        answersGroup.removeAllViews();
        String[] options = q.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(options[i]);
            rb.setId(i);
            answersGroup.addView(rb);
        }

        nextButton.setAlpha(0.5f);
        answersGroup.clearCheck();
    }
    private void showQuizResult() {
        String result = "Visa valmis! Pisteesi: " + score + "/" + questionList.size();
        new AlertDialog.Builder(getContext())
                .setTitle("Tulokset")
                .setMessage(result)
                .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                .show();
    }
}