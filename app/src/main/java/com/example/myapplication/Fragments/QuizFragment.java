package com.example.myapplication.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.myapplication.Question;
import com.example.myapplication.R;

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

    }

    private void displayQuestion(Question q) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);
        questionText = view.findViewById(R.id.questionText);
        answersGroup = view.findViewById(R.id.answersGroup);
        nextButton = view.findViewById(R.id.nextButton);
        // Inflate the layout for this fragment

        // Tekee seuraava-napista läpinäkyvä kunnes käyttäjä on valinnut vaihtoehdon
        nextButton.setAlpha(0.5f);
        answersGroup.setOnCheckedChangeListener((group, checkedId) -> {
            nextButton.setAlpha(1.0f);
        });

        return view;
    }
}