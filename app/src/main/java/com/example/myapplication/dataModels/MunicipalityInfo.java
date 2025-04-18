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

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getPopulationChange() {
        return populationChange;
    }

    public void setPopulationChange(int populationChange) {
        this.populationChange = populationChange;
    }

    public double getJobSelfReliance() {
        return jobSelfReliance;
    }

    public void setJobSelfReliance(double jobSelfReliance) {
        this.jobSelfReliance = jobSelfReliance;
    }

    public double getEmploymentRate() {
        return employmentRate;
    }

    public void setEmploymentRate(double employmentRate) {
        this.employmentRate = employmentRate;
    }



}
