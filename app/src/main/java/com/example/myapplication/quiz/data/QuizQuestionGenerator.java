package com.example.myapplication.quiz.data;

import com.example.myapplication.municipality.model.MunicipalityInfo;
import com.example.myapplication.quiz.model.Question;

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
        return generateMaxQuestion(municipalities, "Missä kunnassa on suurin asukasluku?",
                Comparator.comparingInt(MunicipalityInfo::getPopulation));
    }

    private static Question generateEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Missä kaupungissa on suurin työllisyysaste?",
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));
    }

    private static Question generateJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Missä kunnassa on suurin työpaikkaomavaraisuusaste?",
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));
    }

    private static Question generatePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        return generateMaxQuestion(municipalities, "Missä kunnassa väestönkasvu on ollut suurinta?",
                Comparator.comparingDouble(MunicipalityInfo::getPopulationChange));
    }

    private static Question generateLowestPopulationQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Missä kunnassa on pienin asukasluku?",
                Comparator.comparingInt(MunicipalityInfo::getPopulation));
    }

    private static Question generateLowestEmploymentRateQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Missä kunnassa on pienin työllisyysaste?",
                Comparator.comparingDouble(MunicipalityInfo::getEmploymentRate));
    }

    private static Question generateLowestJobSelfRelianceQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Missä kunnassa on matalin työpaikkaomavaraisuusaste?",
                Comparator.comparingDouble(MunicipalityInfo::getJobSelfReliance));
    }

    private static Question generateNegativePopulationChangeQuestion(List<MunicipalityInfo> municipalities) {
        return generateMinQuestion(municipalities, "Missä kunnassa väestö väheni eniten?",
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