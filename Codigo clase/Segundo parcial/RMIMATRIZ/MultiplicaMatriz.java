class MultiplicaMatriz{
    static int N = 9;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];

    static double[][] sepera_matriz(double[][] A, int inicio){
        double [][] M = new double [N/3][N];
        for(int i = 0; i < N/3; i++)
            for(int j = 0; j < N; j++)        
                M[i][j] = A[i+inicio][j];
        return M;
    }

    static double[][] multiplica_matrices(double[][] A, double [][] B){
        double [][] C = new double[N/3][N/3];
        for(int i = 0; i < N/3; i++)
            for(int j = 0; j < N/3; j++)
                for(int k = 0; k < N; k++)
                    C[i][j] += A[i][k] * B[j][k];
        return C;
    }

    static void acomoda_matriz(double[][] C, double [][] c, int renglon, int columna){
        for(int i = 0; i<N/3; i++)
            for(int j = 0; j<N/3; j++)
                C[i + renglon][j + columna] = c[i][j];
    }

    public static void main(String[] args) throws Exception{
        // inicializa las matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 4 * j;
                B[i][j] = i - 4 * j;
                C[i][j] = 0;
            }
        }
        if (N == 9) {
            System.out.println("\nValor Matriz A\n");
            ImprimirMatriz(A);
            System.out.println("\nValor Matriz B\n");
            ImprimirMatriz(B);
        }
        //B transpuesta
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        double [][] A1 = sepera_matriz(A,0);
        double [][] A2 = sepera_matriz(A,N/3);
        double [][] A3 = sepera_matriz(A,(2*N)/3);
        double [][] B1 = sepera_matriz(B,0);
        double [][] B2 = sepera_matriz(B,N/3);
        double [][] B3 = sepera_matriz(B,(2*N)/3);


        double [][] C1 = multiplica_matrices(A1,B1);
        double [][] C2 = multiplica_matrices(A1,B2);
        double [][] C3 = multiplica_matrices(A1,B3);
        double [][] C4 = multiplica_matrices(A2,B1);
        double [][] C5 = multiplica_matrices(A2,B2);
        double [][] C6 = multiplica_matrices(A2,B3);
        double [][] C7 = multiplica_matrices(A3,B1);
        double [][] C8 = multiplica_matrices(A3,B2);
        double [][] C9 = multiplica_matrices(A3,B3);

        acomoda_matriz(C, C1,0,0);
        acomoda_matriz(C, C2,0,N/3);
        acomoda_matriz(C, C3,0,(2*N)/3);
        acomoda_matriz(C, C4,N/3,0);
        acomoda_matriz(C, C5,N/3,N/3);
        acomoda_matriz(C, C6,N/3,(2*N)/3);
        acomoda_matriz(C, C7,(2*N)/3,0);
        acomoda_matriz(C, C8,(2*N)/3,N/3);
        acomoda_matriz(C, C9,(2*N)/3,(2*N)/3);
        if (N == 10) {
            System.out.println("\nValor Matriz C\n");
            ImprimirMatriz(C);
        }
        //checksm (3420) 
        double checksum = 0.0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }
        System.out.println("\nChecksum: " + checksum);
    }
    public static void ImprimirMatriz(double[][] matriz) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }
}