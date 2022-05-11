import java.util.List;

public class PrintMethods {
    public void allAlphas(List<Double> alphas) {
        System.out.println("-------------------------\n" +
                "Все альфа");
        System.out.println(alphas);
    }

    public void maxAlpha(Double alpha) {
        System.out.println("Максимальная альфа " +  alpha);
    }

    public void allBetas(List<Double> betas) {
        System.out.println("-------------------------\n" +
                "Все бета");
        System.out.println(betas);
    }

    public void maxBeta(Double beta) {
        System.out.println("Максимальная бета " +  beta);
    }

    public void gamma(Double gamma) {
        System.out.println("-------------------------\n" +
                "Гамма " + gamma +
                "\n-------------------------");
    }

    public void space(String space) {
        System.out.println("Пространство " + space);
    }

    public void countOfIterations(Integer count) {
        System.out.println("Количество итераций " + count +
                "\n-------------------------");
    }

    public void startMatrix() {
        System.out.println("Система в виде Ax = B");
    }

    public void changeMatrix() {
        System.out.println("-------------------------\n" +
                "Матрица в виде Ax + B (итерационные формулы)");
    }

    public void systemSolve() {
        System.out.println("-------------------------\n" +
                "Решение системы");
    }

    public void iteration(int n) {
        System.out.print("                     ");
        for (int i = 0 ; i < n; i++) {
            System.out.printf("x%d                     ", i + 1);
        }
        System.out.println();
    }

    public void residualVector() {
        System.out.println("-------------------------\n" +
                "Вектор невязки для исходной системы");
    }

    public void inputFile() {
        System.out.println("Введите полный путь к файлу");
    }
}

