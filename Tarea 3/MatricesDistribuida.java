
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

class MatricesDistribuida {

    static Object obj = new Object();
    static int N = 1500;
    static long[][] A = new long[N][N];
    static long[][] B = new long[N][N];
    static long[][] C = new long[N][N];

    static class Worker extends Thread {

        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                //Recibimos el numero del nodo que se ejecuto desde el cliente
                int x = entrada.readInt();
                if (x == 1) {
                    for (int i = 0; i < N / 2; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(A[i][j]);//Enviar la matriz A1 al nodo 1
                        }
                    }
                    for (int i = 0; i < N / 2; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(B[i][j]);//Enviar la matriz B1 al nodo 1
                        }
                    }
                } else if (x == 2) {
                    for (int i = 0; i < N / 2; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(A[i][j]);//Enviar la matriz A1 al nodo 2
                        }
                    }
                    for (int i = N / 2; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(B[i][j]);//Enviar la matriz B2 al nodo 2
                        }
                    }
                } else if (x == 3) {
                    for (int i = N / 2; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(A[i][j]);//Enviar la matriz A2 al nodo 3
                        }
                    }
                    for (int i = 0; i < N / 2; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(B[i][j]);//Enviar la matriz B1 al nodo 3
                        }
                    }
                } else if (x == 4) {
                    for (int i = N / 2; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(A[i][j]);//Enviar la matriz A2 al nodo 4
                        }
                    }
                    for (int i = N / 2; i < N; i++) {
                        for (int j = 0; j < N; j++) {
                            salida.writeLong(B[i][j]);//Enviar la matriz B2 al nodo 4
                        }
                    }
                }

                synchronized (obj) {
                    if (x == 1) {//Nodo 1
                        //Recibe la matriz C1 del nodo 1
                        for (int i = 0; i < (N / 2); i++) {
                            for (int j = 0; j < (N / 2); j++) {
                                C[i][j] = entrada.readLong();
                            }
                        }
                    } else if (x == 2) {//Nodo 2
                        //Recibe la matriz C2 del nodo 2
                        for (int i = 0; i < (N / 2); i++) {
                            for (int j = (N / 2); j < N; j++) {
                                C[i][j] = entrada.readLong();
                            }
                        }
                    } else if (x == 3) {//Nodo 3
                        //Recibe la matriz C3 del nodo 3
                        for (int i = (N / 2); i < N; i++) {
                            for (int j = 0; j < (N / 2); j++) {
                                C[i][j] = entrada.readLong();
                            }
                        }
                    } else if (x == 4) {//Nodo 4
                        //Recibe la matriz C4 del nodo 4
                        for (int i = (N / 2); i < N; i++) {
                            for (int j = (N / 2); j < N; j++) {
                                C[i][j] = entrada.readLong();
                            }
                        }
                    }
                }
                entrada.close();
                salida.close();
                conexion.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
            System.exit(1);
        }

        int nodo = Integer.valueOf(args[0]);
        String ip = args[1];
        if (nodo == 0) {
            //Inicializando el servidor
            ServerSocket servidor = new ServerSocket(20000);
            //Inicializando A y B
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = i + 2 * j;
                    B[i][j] = i - 2 * j;
                    C[i][j] = 0;
                }
            }
            if (N == 10) {
                System.out.println("\nValor Matriz A\n");
                ImprimirMatriz(A);
                System.out.println("\nValor Matriz B\n");
                ImprimirMatriz(B);
            }
            //Transpone la matriz B, la matriz traspuesta queda en B
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < i; j++) {
                    long x = B[i][j];
                    B[i][j] = B[j][i];
                    B[j][i] = x;
                }
            }
            Worker v[] = new Worker[4];//Aceptaremos 4 clientes
            int nodosClientes = 0;
            //Espera y aceptacion de cada nodo cliente que se va conectar al nodo servidor
            while (nodosClientes != 4) {
                Socket conexion = servidor.accept();
                v[nodosClientes] = new Worker(conexion);
                v[nodosClientes].start();
                nodosClientes++;
            }
            nodosClientes = 0;
            //Esperamos a que los nodos terminen para avanzar.
            while (nodosClientes < 4) {
                v[nodosClientes].join();
                nodosClientes++;
            }
            //Inicializamos la variable checksum
            long checksum = 0;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    checksum += C[i][j];
                }
            }
            if (N == 10) {
                System.out.println("\nValor Matriz C\n");
                ImprimirMatriz(C);
            }
            System.out.println("\nChecksum: " + checksum);
        } else {// Nodos clientes
            //Creamos la conexion
            Socket conexion = null;
            for (;;)
                try {
                conexion = new Socket(ip, 20000);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            //Enviamos el numero del nodo en que se ejecuta el cliente al servidor
            salida.writeInt(nodo);
            //Declaramos Matriz A2 y Matriz B2, guardaran las mitades recibidas
            long[][] A2 = new long[N / 2][N];
            long[][] B2 = new long[N / 2][N];
            //Declaramos Matriz Cc, guardaran los cuartos de C
            long[][] Cc = new long[N / 2][N / 2];
            //Reciben An y Bn, donde n puede ser 1 o 2
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < N; j++) {
                    A2[i][j] = entrada.readLong();//Recibimos la mitad de A.
                }
            }
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < N; j++) {
                    B2[i][j] = entrada.readLong();//Recibimos la mitad de B.
                }
            }
            //Cn=AmitadxBmitad (renglon por renglon)
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < (N / 2); j++) {
                    for (int k = 0; k < N; k++) {
                        Cc[i][j] += A2[i][k] * B2[j][k];
                    }
                }
            }
            //Enviar la matriz Cn al nodo 0
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < (N / 2); j++) {
                    salida.writeLong(Cc[i][j]);
                }
            }
            entrada.close();
            salida.close();
            conexion.close();
        }
    }

    public static void ImprimirMatriz(long[][] matriz) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(matriz[i][j] + "\t");
            }
            System.out.println();
        }
    }
}