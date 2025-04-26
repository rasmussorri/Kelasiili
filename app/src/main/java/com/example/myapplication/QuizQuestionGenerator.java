package com.example.myapplication;

import com.example.myapplication.dataModels.MunicipalityInfo;

import java.util.*;

public class QuizQuestionGenerator {

    public static List<Question> generateQuestions(List<MunicipalityInfo> municipalities) {
        List<Question> allQuestions = new ArrayList<>();

        allQuestions.add(generatePopulationQuestion(municipalities));
        allQuestions.add(generateEmploymentRateQuestion(municipalities));
        allQuestions.add(generateJobSelfRelianceQuestion(municipalities));
        allQuestions.add(generatePopulationChangeQuestion(municipalities));
        allQuestions.add(generateLowestPopulationQuestion(municipalities));
        allQuestions.add(generateLowestEmploymentRateQuestion(municipalities));
        allQuestions.add(generateLowestJobSelfRelianceQuestion(municipalities));
        allQuestions.add(generateNegativePopulationChangeQuestion(municipalities));
        allQuestions.add(generateOver100SelfRelianceQuestion(municipalities));
        allQuestions.add(generateHighEmploymentAndPopulationQuestion(municipalities));

        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(10, allQuestions.size()));
    }

    private static Question generatePopulationQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Which city has the largest population?",
                Comparator.comparingInt(MunicipalityInfo::getPopulation));
    }

    private static Question generateEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Which city has the highest employment rate?",
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));
    }

    private static Question generateJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Which city has the highest job self-reliance?",
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));
    }

    private static Question generatePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Which city had the highest population growth?",
                Comparator.comparingDouble(MunicipalityInfo::getPopulationChange));
    }

    private static Question generateLowestPopulationQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Which city has the smallest population?",
                Comparator.comparingInt(MunicipalityInfo::getPopulation));
    }

    private static Question generateLowestEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Which city has the lowest employment rate?",
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));
    }

    private static Question generateLowestJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Which city has the lowest job self-reliance?",
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));
    }

    private static Question generateNegativePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "In which city did the population decrease the most?",
                Comparator.comparingDouble(MunicipalityInfo::getPopulationChange));
    }

    private static Question generateOver100SelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> filtered = new ArrayList<>();
        for (MunicipalityInfo m : municipalities) {
            if (m.getJobSelfReliance() > 100) {
                filtered.add(m);
            }
        }
        if (filtered.size() < 3) {
            return generateJobSelfRelianceQuestion(municipalities);
        }
        return generateMaxQuestion(filtered,
                "Which city has more jobs than working residents (job self-reliance > 100%)?",
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));
    }

    private static Question generateHighEmploymentAndPopulationQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> filtered = new ArrayList<>();
        for (MunicipalityInfo m : municipalities) {
            if (m.getPopulation() > 50000) {
                filtered.add(m);
            }
        }
        if (filtered.size() < 3) {
            return generateEmploymentRateQuestion(municipalities);
        }
        return generateMaxQuestion(filtered,
                "Which city has the highest employment rate among cities with over 50,000 residents?",
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));
    }

    // Generic helpers
    private static Question generateMaxQuestion(List<MunicipalityInfo> list, String questionText,
                                                Comparator<MunicipalityInfo> comparator) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(list, 3);
        MunicipalityInfo correct = Collections.max(selected, comparator);

        String[] options = new String[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            options[i] = selected.get(i).getName();
        }
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question(questionText, options, correctIndex);
    }

    private static Question generateMinQuestion(List<MunicipalityInfo> list, String questionText,
                                                Comparator<MunicipalityInfo> comparator) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(list, 3);
        MunicipalityInfo correct = Collections.min(selected, comparator);

        String[] options = new String[selected.size()];
        for (int i = 0; i < selected.size(); i++) {
            options[i] = selected.get(i).getName();
        }
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question(questionText, options, correctIndex);
    }

    private static List<MunicipalityInfo> getRandomMunicipalities(List<MunicipalityInfo> list, int count) {
        List<MunicipalityInfo> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }
}



/* package com.example.myapplication;

import com.example.myapplication.dataModels.MunicipalityInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class QuizQuestionGenerator {

    // This class generates quiz questions based on municipality data.
    // It creates questions about population, employment rate, job self-reliance, and population change.
    // Each question has a correct answer and multiple options.
    // The questions are generated by selecting random municipalities and comparing their data.
    // The correct answer is the municipality with the highest value for the specific question.
    public static List<Question> generateQuestions(List<MunicipalityInfo> municipalities) {
        List<Question> allQuestions = new ArrayList<>();

        allQuestions.add(generatePopulationQuestion(municipalities));
        allQuestions.add(generateEmploymentRateQuestion(municipalities));
        allQuestions.add(generateJobSelfRelianceQuestion(municipalities));
        allQuestions.add(generatePopulationChangeQuestion(municipalities));
        allQuestions.add(generateLowestPopulationQuestion(municipalities));
        allQuestions.add(generateLowestEmploymentRateQuestion(municipalities));
        allQuestions.add(generateLowestJobSelfRelianceQuestion(municipalities));
        allQuestions.add(generateNegativePopulationChangeQuestion(municipalities));
        allQuestions.add(generateOver100SelfRelianceQuestion(municipalities));
        allQuestions.add(generateHighEmploymentAndPopulationQuestion(municipalities));

        Collections.shuffle(allQuestions);
        return allQuestions.subList(0, Math.min(10, allQuestions.size()));
    }
    private static Question generatePopulationQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.max(selected, Comparator.comparingInt(MunicipalityInfo::getPopulation));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("Which city has the largest population?", options, correctIndex);
    }

    private static Question generateEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.max(selected, Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("Which city has the highest employment rate?", options, correctIndex);
    }

    private static Question generateJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.max(selected, Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("In which city do the most people work in their home municipality?", options, correctIndex);
    }

    private static Question generatePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.max(selected, Comparator.comparingDouble(MunicipalityInfo::getPopulationChange));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("Which city had the highest population growth?", options, correctIndex);
    }

    private static Question generateLowestPopulationQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.min(selected, Comparator.comparingInt(MunicipalityInfo::getPopulation));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("Which city has the smallest population?", options, correctIndex);
    }

    private static Question generateNegativePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.min(selected, Comparator.comparingDouble(MunicipalityInfo::getPopulationChange));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("In which city did the population decrease the most?", options, correctIndex);
    }

    private static Question generateOver100SelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> filtered = municipalities.stream()
                .filter(m -> m.getJobSelfReliance() > 100)
                .toList();

        if (filtered.size() < 3) return generateJobSelfRelianceQuestion(municipalities); // fallback

        List<MunicipalityInfo> selected = getRandomMunicipalities(filtered, 3);
        MunicipalityInfo correct = Collections.max(selected, Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));

        String[] options = selected.stream().map(MunicipalityInfo::getName).toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question("Which city has more jobs than working residents (job self-reliance > 100%)?", options, correctIndex);
    }

    private static Question generateLowestEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        // Valitaan satunnainen kolmen kunnan joukko
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        // Etsitään pienin työllisyysaste näistä
        MunicipalityInfo correct = Collections.min(selected, Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));

        // Muodostetaan vaihtoehdot
        String[] options = selected.stream()
                .map(MunicipalityInfo::getName)
                .toArray(String[]::new);
        // Oikean indeksin hakeminen
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question(
                "Which city has the lowest employment rate?",
                options,
                correctIndex
        );
    }

    // Valitsee kolmen kunnan joukosta sen, jonka työpaikkaomavaraisuus on pienin
    private static Question generateLowestJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> selected = getRandomMunicipalities(municipalities, 3);
        MunicipalityInfo correct = Collections.min(
                selected,
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance)
        );

        String[] options = selected.stream()
                .map(MunicipalityInfo::getName)
                .toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question(
                "Which city has the lowest job self-reliance?",
                options,
                correctIndex
        );
    }

    // Suodattaa kunnat, joissa on yli 50 000 asukasta, ja valitsee niistä korkeimman työllisyysasteen
    private static Question generateHighEmploymentAndPopulationQuestion(List<MunicipalityInfo> municipalities) {
        List<MunicipalityInfo> filtered = new ArrayList<>();
        for (MunicipalityInfo m : municipalities) {
            if (m.getPopulation() > 50000) {
                filtered.add(m);
            }
        }

        if (filtered.size() < 3) {
            return generateEmploymentRateQuestion(municipalities);
        }

        List<MunicipalityInfo> selected = getRandomMunicipalities(filtered, 3);
        MunicipalityInfo correct = Collections.max(
                selected,
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate)
        );

        String[] options = selected.stream()
                .map(MunicipalityInfo::getName)
                .toArray(String[]::new);
        int correctIndex = Arrays.asList(options).indexOf(correct.getName());

        return new Question(
                "Which city has the highest employment rate among cities with over 50,000 residents?",
                options,
                correctIndex
        );
    }
    private static List<MunicipalityInfo> getRandomMunicipalities(List<MunicipalityInfo> list, int count) {
        List<MunicipalityInfo> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }
}

*/
