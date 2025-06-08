package com.uniovi.sercheduler.localsearch.export;

public class CSVExporter {
/*
    public static void createCSV(String fileName){
            createDataCSV(fileName);
            createResumeCSV(fileName);
            createDetailCSV(fileName);
    }

    public static void appendCSV(NeighborhoodObserver observer, String fileName){

            appendDataCSV(observer, fileName);

            appendResumeCSV(observer, fileName);

            appendDetailCSV(observer, fileName);

    }

    private static void appendDetailCSV(NeighborhoodObserver observer, String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_detail.csv", true)) {

            StringBuilder newLine = new StringBuilder();

            for(int run = 0; run < observer.getExecutions().size(); run++) {
                for(int iteration = 0; iteration < observer.getExecutions().get(run).numberOfIterations(); iteration++) {
                    newLine.append("??")
                            .append(";")
                            .append(run + 1)
                            .append(";")
                            .append(iteration + 1)
                            .append(";")
                            .append(observer.getExecutions().get(run).reachedMakespanList().get(iteration))
                            .append(";")
                            .append(observer.getExecutions().get(run).improvementRatioWithRespectToLastIteration().get(iteration))
                            .append(";")
                            .append(observer.getExecutions().get(run).numberOfGeneratedNeighborsList().get(iteration))
                            .append(";");
                    if (observer.getExecutions().get(run).betterNeighborsRatioList().isEmpty()) {
                        newLine.append("-")
                                .append(";")
                                .append("-")
                                .append(";")
                                .append("-")
                                .append("\n");
                    } else {
                        newLine.append(observer.getExecutions().get(run).betterNeighborsRatioList().get(iteration))
                                .append(";")
                                .append(observer.getExecutions().get(run).allNeighborsImprovingRatioList().get(iteration))
                                .append(";")
                                .append(observer.getExecutions().get(run).betterNeighborsImprovingRatioList().get(iteration))
                                .append("\n");
                    }
                }
            }

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void appendResumeCSV(NeighborhoodObserver observer, String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_resume.csv", true)) {

            String newLine = observer.getStrategyName() +
                    ";" +
                    observer.avgReachedCost() +
                    ";" +
                    observer.avgExecutionTime() +
                    ";" +
                    observer.avgIterations() +
                    ";" +
                    observer.avgGeneratedNeighbors() +
                    ";" +
                    observer.getBestReachedMakespan() +
                    ";" +
                    observer.getWorstReachedMakespan() +
                    ";" +
                    observer.standardDeviation() +
                    "\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void appendDataCSV(NeighborhoodObserver observer, String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_data.csv", true)) {

            StringBuilder newLine = new StringBuilder();

            for(int i = 0; i < observer.getExecutions().size(); i++) {
                newLine.append(observer.getExecutions().get(i).strategyName())
                        .append(";")
                        .append(observer.getExecutions().get(i).bestReachedMakespan())
                        .append(";")
                        .append(observer.getExecutions().get(i).executionTime())
                        .append(";")
                        .append(observer.getExecutions().get(i).numberOfIterations())
                        .append(";")
                        .append(observer.getExecutions().get(i).avgGeneratedNeighbors())
                        .append(";");
                if (observer.getExecutions().get(i).betterNeighborsRatioList().isEmpty()) {
                        newLine.append("-")
                            .append(";")
                            .append("-")
                            .append(";")
                            .append("-")
                            .append("\n");
                } else {
                    newLine.append(observer.getExecutions().get(i).avgBetterNeighborsRatio())
                            .append(";")
                            .append(observer.getExecutions().get(i).avgAllNeighborsImprovingRatio())
                            .append(";")
                            .append(observer.getExecutions().get(i).avgBetterNeighborsImprovingRatio())
                            .append("\n");
                }
            }

            writer.write(newLine.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createDetailCSV(String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_detail.csv", false)) {

            String newLine = "Experiment" +
                    ";" +
                    "Run number" +
                    ";" +
                    "Iteration number" +
                    ";" +
                    "Best makespan reached" +
                    ";" +
                    "Improvement ratio with respect to last iteration" +
                    ";" +
                    "Generated neighbors" +
                    ";" +
                    "Average percentage of neighbors that outperform their source solution" +
                    ";" +
                    "Average improvement ratio from all neighbors" +
                    ";" +
                    "Average improvement ratio from neighbors that outperform their source solution" +
                    ";" +
                    "Best makespan" +
                    ";" +
                    "Worst makespan" +
                    ";" +
                    "Standard deviation" +
                    "\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createResumeCSV(String fileName) {

        try (FileWriter writer = new FileWriter(fileName + "_resume.csv", false)) {

            String newLine = "Method" +
                    ";" +
                    "Avg(Best_Mkp)" +
                    ";" +
                    "Avg(Exec_Time)" +
                    ";" +
                    "Avg(LS_Iters)" +
                    ";" +
                    "Avg(Neighb)" +
                    "\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void createDataCSV(String fileName){

        try (FileWriter writer = new FileWriter(fileName + "_data.csv", false)) {

            String newLine = "Method" +
                    ";" +
                    "Run number" +
                    ";" +
                    "Best makespan reached" +
                    ";" +
                    "Executing time" +
                    ";" +
                    "Number of local search iterations" +
                    ";" +
                    "Average generated neighbors" +
                    ";" +
                    "Average percentage of neighbors that outperform their source solution" +
                    ";" +
                    "Average improvement ratio from all neighbors" +
                    ";" +
                    "Average improvement ratio from neighbors that outperform their source solution" +
                    "\n";

            writer.write(newLine);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
*/
}
