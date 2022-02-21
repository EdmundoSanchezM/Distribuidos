package Matrices;

class MultiplicaMatriz_2 {

    static int N = 10;
    static long[][] A = new long[N][N];
    static long[][] B = new long[N][N];
    static long[][] C = new long[N][N];

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        // inicializa las matrices A y B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 2 * j;
                B[i][j] = i - 2 * j;
                C[i][j] = 0;
            }
        }
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                System.out.print(A[i][j] + "\t|");
            }
            System.out.println();
        }
        System.out.println("  uwu ");
        for (int i = 0; i < N; i++) {
            System.out.print("|");
            for (int j = 0; j < N; j++) {
                System.out.print(B[i][j] + "\t|");
            }
            System.out.println();
        }

        // transpone la matriz B, la matriz traspuesta queda en B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                long x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        // multiplica la matriz A y la matriz B, el resultado queda en la matriz C
        // notar que los indices de la matriz B se han intercambiado
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                for (int k = 0; k < N; k++) {
                    C[i][j] += A[i][k] * B[j][k];
                }
            }
        }
        long checksum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }
        System.out.println("\nChecksum: " + checksum);

        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo: " + (t2 - t1) + "ms");
    }
}
