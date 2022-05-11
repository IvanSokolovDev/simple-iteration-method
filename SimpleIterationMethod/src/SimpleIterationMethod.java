import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SimpleIterationMethod {
    private int n;
    private final Double epsilon = 0.0001;
    private final List<List<Double>> matrix = new ArrayList<>();
    private final PrintMethods printMethods = new PrintMethods();
    private double[][] A;
    private double[] B;

    public void execute() throws Exception {
        inputMatrix();
        inputN();
        printMethods.startMatrix();
        printMatrix();

        A = new double[n][n];
        B = new double[n];

        // проверка на диагольнальное преобладание и выброс ошибки
        if (!diagonalDominance(matrix)) {
            throw new Exception("Отсутствует диагональное преобладание");
        }

        breakSystem(); // разделение системы на A и B
        gauss(); // решение системы методом гаусса
        castMatrix(matrix); // преобразование матрицы к итерационным формулам
        printMethods.changeMatrix();
        printMatrix();

        List<Double> alphas = getAlphas(matrix);
        printMethods.allAlphas(alphas);
        double maxAlpha = getMaxAlpha(alphas);
        printMethods.maxAlpha(maxAlpha);

        List<Double> betas = getBetas(matrix);
        printMethods.allBetas(betas);
        double maxBeta = getMaxBeta(betas);
        printMethods.maxBeta(maxBeta);

        Double gamma = getGamma(matrix);
        printMethods.gamma(gamma);

        // проверка альфа, бета, гамма и выброс ошибки
        if (!(maxAlpha < 1 || maxBeta < 1 || gamma < 1)) {
            throw new Exception("Alpha/Beta/Gamma >= 1");
        }

        String space = space(maxAlpha, maxBeta, gamma);
        printMethods.space(space);

        Integer countIterations = countIterationsForSpace(space, maxAlpha, maxBeta, gamma);
        printMethods.countOfIterations(countIterations);

        doIterations(matrix, countIterations); // проведение итераций
    }

    // ввод количества уравнений)
    public void inputN() {
        double numberDouble =  matrix.get(0).get(0);
        int numberInteger = (int) numberDouble;
        n = Integer.parseInt(Integer.valueOf(numberInteger).toString());
        matrix.remove(0);
    }

    // считывание системы с файла
    public void inputMatrix() {
        try {
            printMethods.inputFile();
            String filePath = inputFilePath();
            Scanner scanner = new Scanner(new File(filePath));

            // считываем строку
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                StringTokenizer stringTokenizer = new StringTokenizer(line, " "); // разбиваем строку по пробелам
                List<Double> row = new ArrayList<>();

                // записываем числа из разбитой строки список
                while (stringTokenizer.hasMoreTokens()) {
                    String token = stringTokenizer.nextToken();
                    row.add(Double.parseDouble(token));
                }

                matrix.add(row); // строку - список записываем в систему
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // ввож пути к файлу
    public String inputFilePath() {
        return new Scanner(System.in).nextLine();
    }

    // разделение системы на A и B
    public void breakSystem() {
        // проход по строкам
        for (int i = 0; i < n; i++) {
            List<Double> row = matrix.get(i);
            double freeMember = row.get(n);
            B[i] = freeMember;

            for (int j = 0; j < n; j++) {
                A[i][j] = row.get(j);
            }
        }
    }

    // решение системы
    public void gauss() {
        double[][] A = new double[n][n];
        double[] b = new double[n];

        for (int i = 0; i < matrix.size(); i++) {
            List<Double> row = matrix.get(i);
            b[i] = row.get(n);

            for (int j = 0; j < row.size() - 1; j++) {
                A[i][j] = row.get(j);
            }
        }

        GaussSeidel solver = new GaussSeidel(A, b);
        double[] x = solver.solveSystem(100);

        printMethods.systemSolve();

        for (int i = 0; i < x.length; i++) {
            System.out.printf("x%d = %f\n", i + 1, x[i]);
        }
    }

    // преобразование матрицы виду Ax = B
    public void castMatrix(List<List<Double>> matrix) {
        // проход по строкам системы
        for (int i = 0; i < n; i++) {
            List<Double> currentRow = matrix.get(i);
            Double diagonalElement = currentRow.get(i); // элемент на диагонали

            // проход по элементам строки
            for (int j = 0; j < n + 1; j++) {
                Double currentElem = currentRow.get(j) / diagonalElement; // элемент строки делим на элемент диагонали
                currentRow.set(j, currentElem);
            }


            currentRow.set(i, 0.0); // зануляем элемент диагонали
            matrix.set(i, currentRow); // старую строку в системе меням на измененную
        }

        // проход по строкам системы
        for (int i = 0; i < n; i++) {
            List<Double> currentRow = matrix.get(i);

            // проход по элементам строки
            for (int j = 0; j < n; j++) {
                Double currentElem = currentRow.get(j) * (-1); // умножаем на -1 элементы строки
                currentRow.set(j, currentElem);
            }

            matrix.set(i, currentRow); // старую строку меняем на новую
        }
    }

    // вывод матрицы
    public void printMatrix() {
        for (List<Double> row : matrix) {
            for (Double elem : row) {
                System.out.print(elem + " ");
            }
            System.out.println();
        }
    }

    // получение всех альфа
    public List<Double> getAlphas(List<List<Double>> matrix) {
        List<Double> alphas = new ArrayList<>();

        // проход по строкам системы
        for (int i = 0; i < n; i++) {
            double currentAlpha = 0;
            List<Double> currentRow = matrix.get(i);

            // проход по строке
            for (int j = 0; j < n; j++) {
                currentAlpha += Math.abs(currentRow.get(j)); // к альфе прибавляем модуль элемента строки
            }

            alphas.add(currentAlpha); // текущую альфа добавляем в список
        }

        return alphas;
    }

    // получение максимальной альфа
    public double getMaxAlpha(List<Double> alphas) {
        return Collections.max(alphas);
    }

    // получение всех бета
    public List<Double> getBetas(List<List<Double>> matrix) {
        List<Double> betas = new ArrayList<>();
        double currentBeta = 0;
        int rowNumber = 0;
        int columnNumber = 0;

        // условие выхода из перебора столбцов
        while (columnNumber < n) {
            List<Double> currentRow = matrix.get(rowNumber);
            currentBeta += Math.abs(currentRow.get(columnNumber)); // к текущей бета прибавляем модуль элемента столбца
            rowNumber++; // следующий элемент столбца

            // перебрали елементы столбца
            if (rowNumber == n) {
                betas.add(currentBeta); // добавляем бета в список
                currentBeta = 0;
                columnNumber++; // переход к следующему столбцу
                rowNumber = 0;
            }
        }

        return betas;
    }

    // получение максимальной бета
    public double getMaxBeta(List<Double> betas) {
        return Collections.max(betas);
    }

    // получение гамма
    public Double getGamma(List<List<Double>> matrix) {
        double gamma = 0;

        // проход по строкам системы
        for (int i = 0; i < n; i++) {
            List<Double> currentRow = matrix.get(i);

            // проход по строке
            for (int j = 0; j < n; j++) {
                gamma += Math.pow(currentRow.get(j), 2); // к гамма прибавляем квадрат элемента строки
            }
        }

        return gamma;
    }

    // количество итераций при альфа
    public int countIterationsAlpha(List<List<Double>> matrix, double alpha) {
        List<Double> row = matrix.get(0);
        List<Double> absoluteElementsRow = new ArrayList<>();

        row.forEach(elem -> absoluteElementsRow.add(0 - Math.abs(elem)));

        double r = Collections.max(absoluteElementsRow);
        int degree = 1;

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (formula(alpha, degree, r) < epsilon) {
                break;
            }
            degree++;
        }

        return degree;
    }

    // количество итераций при бета
    public int countIterationsBeta(List<List<Double>> matrix, double beta) {
        List<Double> row = matrix.get(0);
        double r = 0;
        int degree = 1;

        for (double elem : row) {
            r += Math.abs(0 - elem);
        }

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (formula(beta, degree, r) < epsilon) {
                break;
            }
            degree++;
        }

        return degree;
    }

    // количество итераций при гамма
    public int countIterationsGamma(List<List<Double>> matrix, double gamma) {
        double r = 0;

        for (List<Double> row : matrix)  {
            double currentElem = row.get(n);
            r += Math.pow(0 - currentElem, 2);
        }

        r = Math.sqrt(r);
        int degree = 1;

        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            if (formula(gamma, degree, r) < epsilon) {
                break;
            }
            degree++;
        }

        return degree;
    }

    // значение по формуле
    public double formula(double space, int degree, double r) {
        return (Math.pow(space, degree) / (1 - space)) * r;
    }

    // пространство
    public String space(Double alpha, Double beta, Double gamma) {
        List<Double> list = new ArrayList() {{
            add(alpha);
            add(beta);
            add(gamma);
        }};

        Double min = Collections.min(list);

        if (min.equals(alpha)) {
            return "alpha";
        }

        if (min.equals(beta)) {
            return "beta";
        }

        if (min.equals(gamma)) {
            return "gamma";
        }

        return null;
    }

    // количество итераций
    public Integer countIterationsForSpace(String space, double alpha, double beta, double gamma) {
        if (space.equals("alpha")) {
            return countIterationsAlpha(matrix, alpha);
        } else if (space.equals("beta")) {
            return countIterationsBeta(matrix, beta);
        } else if (space.equals("gamma")) {
            return countIterationsGamma(matrix, gamma);
        }

        return null;
    }

    // проведение итераций
    public void doIterations(List<List<Double>> matrix, int countIterations) {
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            result.add(0.0);
        }

        printMethods.iteration(n);

        // итерации
        for (int iteration = 0; iteration <= countIterations; iteration++) {
            if (countIterations > 10) {
                if (iteration < 5 || countIterations - iteration < 6) {
                    System.out.printf("iteration %d ", iteration);
                    System.out.println(result);
                }
            } else {
                System.out.printf("iteration %d ", iteration);
                System.out.println(result);
            }

            double[][] tempMatrix = listToArray(matrix); // система - список в массив

            // проход по строкам
            for (int i = 0; i < n; i++) {
                double[] row = tempMatrix[i];
                double currentX = 0;

                // проход по строке
                for (int j = 0; j < n; j++) {
                    double currentElement = row[j] * result.get(j); // текущий элемент строки умножаем на x прошлой итерации
                    row[j] = currentElement;
                }

                // формируем x для следующей итерации
                for (int j = 0; j < n + 1; j++) {
                    currentX += row[j];
                }

                result.set(i, currentX);
                tempMatrix[i] = row;
            }
        }

        residualVector(result);
    }

    // вектор невязки для исходной системы
    public void residualVector(List<Double> result) {
        List<Double> multi = new ArrayList<>();

        // умножаем левую часть системы на приближения x
        for (int i = 0; i < n; i++) {
            double[] rowA = A[i];

             double sum = 0;

            for (int j = 0; j < n; j++) {
                double rowResult = result.get(j);
                sum += rowA[j] * rowResult;
            }

            multi.add(sum);
        }

        List<Double> residualVector = new ArrayList<>();

        // из вектора свободных члено вычитаем ранее полученное произведение
        for (int i = 0; i < n; i++) {
            double diff = B[i] - multi.get(i);
            residualVector.add(diff);
        }

        printMethods.residualVector();
        System.out.println(residualVector);
    }

    // проверка диагонального преобладания
    public boolean diagonalDominance(List<List<Double>> matrix) {
        int indexDiagonalElement  = 0;

        for (int i = 0; i < n; i++) {
            double sum = 0;
            List<Double> currentRow = matrix.get(i);
            double diagonalElement = Math.abs(currentRow.get(indexDiagonalElement));

            for (int j = 0; j < n; j++) {
                if (j != i) {
                    sum += Math.abs(currentRow.get(j));
                }
            }

            if (diagonalElement < sum) {
                return false;
            }

            indexDiagonalElement++;
        }

        return true;
    }

    // система - список в массив
    public double[][] listToArray(List<List<Double>> matrix) {
        double[][] matrixArray = new double[n][n + 1];
        List<List<Double>> list = matrix;

        for (int i = 0; i < list.size(); i++) {
            List<Double> row = list.get(i);

            for (int j = 0; j < row.size(); j++) {
                matrixArray[i][j] = row.get(j);
            }
        }

        return matrixArray;
    }

    // стартовый метод
    public static void main(String[] args) {
        SimpleIterationMethod simpleIterationMethod = new SimpleIterationMethod();
        try {
            simpleIterationMethod.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
