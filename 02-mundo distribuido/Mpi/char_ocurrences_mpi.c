// mpicc char_ocurrences_mpi.c -o char_occurrences
// cat longFile.txt | mpirun -np 2 char_occurrences a

// correr 10 veces: 
// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | mpirun -np 2 char_occurrences a') 2>> timeMpi.txt

// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | mpirun -np 2 char_occurrences a') 2>> timeMpiLoremIpsum.txt

// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | mpirun -np 2 char_occurrences a') 2>> timeMpi124mbLoremIpsum.txt

// fichero mpi
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define SIZE 128
#define TAG 0

int main( int argc, char **argv )
{
    int ierr, my_id;
    ierr = 0;

    if (argc != 2) {
      printf("Número de parámetros incorrecto, es necesario un char\n");
      exit( 0 );
    }

    char ch = tolower(argv[1][0]);

    int colonCount = 0;
    char buffer[SIZE];
    char bufferRecv[SIZE];
    MPI_Status stat;

    MPI_Init(&argc, &argv);

    /* find out MY process ID, and how many processes were started. */

    MPI_Comm_rank(MPI_COMM_WORLD, &my_id);
    
    //proceso padre id 0
    if (my_id == 0) {

        while (fgets(buffer, SIZE, stdin) > 0) {
            MPI_Send(buffer, SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD);
        }

        MPI_Send("EOF", SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD);

        while (strlen(bufferRecv) == 0) {
            MPI_Recv( bufferRecv, SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD, &stat);
        }
        printf("%s\n", bufferRecv);

    } else {
        // proceso hijo id 1
        int countChar = 0;
        MPI_Recv( buffer, SIZE, MPI_CHAR, 0, TAG, MPI_COMM_WORLD, &stat);
        while (strcmp(buffer, "EOF") != 0) {

            int i = 0;

            while ( buffer[i] ) {
                buffer[i] = tolower(buffer[i]);
                i++;
            }

            i           = 0;
            char* pTemp = buffer;

            while (pTemp != NULL) {
                pTemp = strchr(pTemp, ch);                                                                                                           
                if (pTemp) {
                    pTemp++;
                    countChar++;
                }

            }
            MPI_Recv( buffer, SIZE, MPI_CHAR, 0, TAG, MPI_COMM_WORLD, &stat);
        }

        char res[SIZE];

        snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", countChar);

        MPI_Send(res, SIZE, MPI_CHAR, 0, TAG, MPI_COMM_WORLD);

    }

    MPI_Finalize();

    return 0;
}