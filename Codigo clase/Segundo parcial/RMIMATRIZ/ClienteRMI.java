import java.rmi.*;

public class ClienteRMI{
    static int N = 6;
    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    static int[][] C = new int[N][N];

    static int[][] sepera_matriz(int[][] A, int inicio){
        int [][] M = new int [N/2][N];
        for(int i = 0; i < N/2; i++)
            for(int j = 0; j < N; j++)        
                M[i][j] = A[i+inicio][j];
        return M;
    }

    static void acomoda_matriz(int[][] C, int [][] c, int renglon, int columna){
        for(int i = 0; i<N/2; i++)
            for(int j = 0; j<N/2; j++)
                C[i + renglon][j + columna] = c[i][j];
    }

    public static void main(String[] args) throws Exception{
        String url = "rmi://localhost/matriz";//IP privada de la MV del servidor
        InterfaceRMI r = (InterfaceRMI)Naming.lookup(url);
         // inicializa las matrices A y B
         for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i - j;
                B[i][j] = 1 + 2 * j;
                C[i][j] = 0;
            }
        }
        // transpone la matriz B, la matriz traspuesta queda en B
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                int x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        int [][] A1 = sepera_matriz(A,0);
        int [][] A2 = sepera_matriz(A,N/2);
        int [][] B1 = sepera_matriz(B,0);
        int [][] B2 = sepera_matriz(B,N/2);

        int [][] C1 = r.multiplica_matrices(A1,B1,N);
        int [][] C2 = r.multiplica_matrices(A1,B2,N);
        int [][] C3 = r.multiplica_matrices(A2,B1,N);
        int [][] C4 = r.multiplica_matrices(A2,B2,N);

        acomoda_matriz(C, C1,0,0);
        acomoda_matriz(C, C2,0,N/2);
        acomoda_matriz(C, C3,N/2,0);
        acomoda_matriz(C, C4,N/2,N/2);
        
        //checksm (3420) 
        int checksum = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }
        System.out.println("\nChecksum: " + checksum);
    }
}