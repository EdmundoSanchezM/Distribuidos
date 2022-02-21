import java.rmi.*;

public class ClienteRMI{
    static int N = 3000;
    static double[][] A = new double[N][N];
    static double[][] B = new double[N][N];
    static double[][] C = new double[N][N];

    static double[][] separa_matriz(double[][] A, int inicio){
        double [][] M = new double [N/3][N];
        for(int i = 0; i < N/3; i++)
            for(int j = 0; j < N; j++)        
                M[i][j] = A[i+inicio][j];
        return M;
    }


    static void acomoda_matriz(double[][] C, double [][] c, int renglon, int columna){
        for(int i = 0; i<N/3; i++)
            for(int j = 0; j<N/3; j++)
                C[i + renglon][j + columna] = c[i][j];
    }

    public static void main(String[] args) throws Exception{
        //Nodo 1
        String url1 = "rmi://10.0.0.5/matriz";//IP privada de la MV del servidor
        InterfaceRMI r1 = (InterfaceRMI)Naming.lookup(url1);
        //Nodo 2
        String url2 = "rmi://10.0.0.6/matriz";//IP privada de la MV del servidor
        InterfaceRMI r2 = (InterfaceRMI)Naming.lookup(url2);
        //Nodo 3
        String url3 = "rmi://10.0.0.7/matriz";//IP privada de la MV del servidor
        InterfaceRMI r3 = (InterfaceRMI)Naming.lookup(url3);
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

        // transpone la matriz B, la matriz traspuesta queda en B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        double [][] A1 = separa_matriz(A,0);
        double [][] A2 = separa_matriz(A,N/3);
        double [][] A3 = separa_matriz(A,(2*N)/3);
        double [][] B1 = separa_matriz(B,0);
        double [][] B2 = separa_matriz(B,N/3);
        double [][] B3 = separa_matriz(B,(2*N)/3);


        double [][] C1 = r1.multiplica_matrices(A1,B1,N);
        double [][] C2 = r1.multiplica_matrices(A1,B2,N);
        double [][] C3 = r1.multiplica_matrices(A1,B3,N);
        double [][] C4 = r2.multiplica_matrices(A2,B1,N);
        double [][] C5 = r2.multiplica_matrices(A2,B2,N);
        double [][] C6 = r2.multiplica_matrices(A2,B3,N);
        double [][] C7 = r3.multiplica_matrices(A3,B1,N);
        double [][] C8 = r3.multiplica_matrices(A3,B2,N);
        double [][] C9 = r3.multiplica_matrices(A3,B3,N);

        acomoda_matriz(C, C1,0,0);
        acomoda_matriz(C, C2,0,N/3);
        acomoda_matriz(C, C3,0,(2*N)/3);
        acomoda_matriz(C, C4,N/3,0);
        acomoda_matriz(C, C5,N/3,N/3);
        acomoda_matriz(C, C6,N/3,(2*N)/3);
        acomoda_matriz(C, C7,(2*N)/3,0);
        acomoda_matriz(C, C8,(2*N)/3,N/3);
        acomoda_matriz(C, C9,(2*N)/3,(2*N)/3);
        if (N == 9) {
            System.out.println("\nValor Matriz C\n");
            ImprimirMatriz(C);
        }
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