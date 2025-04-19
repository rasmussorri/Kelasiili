package com.example.myapplication.fragments;

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

import com.example.myapplication.Question;
import com.example.myapplication.QuizQuestionGenerator;
import com.example.myapplication.R;
import com.example.myapplication.dataModels.MunicipalityInfo;

import java.util.ArrayList;
import java.util.List;


public class QuizFragment extends Fragment {

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private TextView questionText;
    private RadioGroup answersGroup;
    private Button nextButton;




    public QuizFragment() {
        // Required empty public constructor
    }

    private void loadQuestions() {
        // Normally, you'd fetch this from an API or ViewModel
        List<MunicipalityInfo> municipalities = FakeDataProvider.getMunicipalities(); // Replace with real data
        questionList = QuizQuestionGenerator.generateQuestions(municipalities);
        currentQuestionIndex = 0;
        displayQuestion(questionList.get(currentQuestionIndex));
    }

    private void displayQuestion(Question q) {
        questionText.setText(q.getQuestionText());
        answersGroup.removeAllViews(); // Clear old options

        String[] options = q.getOptions();
        for (int i = 0; i < options.length; i++) {
            RadioButton rb = new RadioButton(getContext());
            rb.setText(options[i]);
            rb.setId(i); // Important: ID matches index
            answersGroup.addView(rb);
        }

        nextButton.setAlpha(0.5f); // Make button dimmed again
        answersGroup.clearCheck(); // Clear previous selection
    }
    private void showQuizResult() {
        String result = "Quiz complete! Your score: " + score + "/" + questionList.size();
        new AlertDialog.Builder(getContext())
                .setTitle("Results")
                .setMessage(result)
                .setPositiveButton("OK", (dialog, which) -> requireActivity().onBackPressed())
                .show();
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
    public static class FakeDataProvider {
        public static List<MunicipalityInfo> getMunicipalities() {
            List<MunicipalityInfo> list = new ArrayList<>();
            list.add(new MunicipalityInfo("Helsinki", 650000, 58000, 50.0, 1.2));
            list.add(new MunicipalityInfo("Espoo", 300000, 37500, 60.0, 1.5));
            list.add(new MunicipalityInfo("Tampere", 250000, 32300, 58.5, 1.1));
            return list;
        }
    }
}