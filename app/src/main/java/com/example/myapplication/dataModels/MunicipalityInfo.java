package com.example.myapplication.dataModels;

public class MunicipalityInfo {
    private String name;
    private int population;
    private int populationChange;
    private double jobSelfReliance;
    private double employmentRate;

    public MunicipalityInfo(String name, int population, int populationChange, double jobSelfReliance, double employmentRate) {
        this.name = name;
        this.population = population;
        this.populationChange = populationChange;
        this.jobSelfReliance = jobSelfReliance;
        this.employmentRate = employmentRate;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getPopulationChange() {
        return populationChange;
    }

    public double getJobSelfReliance() {
        return jobSelfReliance;
    }

    public double getEmploymentRate() {
        return employmentRate;
    }

}
