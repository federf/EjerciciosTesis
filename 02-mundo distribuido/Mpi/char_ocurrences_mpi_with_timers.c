// mpicc char_ocurrences_mpi_with_timers.c -o char_occurrences_with_timers
// cat longFile.txt | mpirun -np 2 char_occurrences_with_timers a

// correr 10 veces:
// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | mpirun -np 2 char_occurrences_with_timers a') 2>> timeMpi.txt

// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a') 2>> timeMpiLoremIpsum.txt

// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a') 2>> timeMpi124mbLoremIpsum.txt

//SALVAR TODO OUTPUT A UN ARCHIVO
// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | mpirun -np 2 char_occurrences_with_timers a >> timeMpiLongFileWithTimers.txt') 2>> timeMpiLongFileWithTimers.txt

//(time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a >> timeMpiLongIpsumWithTimers.txt') 2>> timeMpiLongIpsumWithTimers.txt

//(time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a >> timeMpi124mbLoremIpsumWithTimers.txt') 2>> timeMpi124mbLoremIpsumWithTimers.txt

// fichero mpi
#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <time.h>

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

    // medición tiempo
    clock_t begin;
    clock_t end;
    double time_spent = 0;

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
            //inicializo contador tiempo inicio
            begin = clock();

            MPI_Send(buffer, SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD);

            //inicializo contador tiempo inicio
            end = clock();
            //almaceno diferencia de tiempo
            time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;
        }

        //inicializo contador tiempo inicio
        begin = clock();

        MPI_Send("EOF", SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD);

        //inicializo contador tiempo inicio
        end = clock();
        //almaceno diferencia de tiempo
        time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;

        while (strlen(bufferRecv) == 0) {
            //inicializo contador tiempo inicio
            begin = clock();

            MPI_Recv( bufferRecv, SIZE, MPI_CHAR, 1, TAG, MPI_COMM_WORLD, &stat);

            //inicializo contador tiempo inicio
            end = clock();
            //almaceno diferencia de tiempo
            time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;
        }
        printf("%s\n", bufferRecv);

        printf("Tiempo hijo : %lf\n", time_spent);


    } else {
        // proceso hijo id 1

        //inicializo contador tiempo inicio
        begin = clock();

        int countChar = 0;
        MPI_Recv( buffer, SIZE, MPI_CHAR, 0, TAG, MPI_COMM_WORLD, &stat);

        //inicializo contador tiempo inicio
        end = clock();
        //almaceno diferencia de tiempo
        time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;

        while (strcmp(buffer, "EOF") != 0) {

            //inicializo contador tiempo inicio
            begin = clock();

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

            //inicializo contador tiempo inicio
            end = clock();
            //almaceno diferencia de tiempo
            time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;
        }

        //inicializo contador tiempo inicio
        begin = clock();

        char res[SIZE];

        snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", countChar);

        MPI_Send(res, SIZE, MPI_CHAR, 0, TAG, MPI_COMM_WORLD);

        //inicializo contador tiempo inicio
        end = clock();
        //almaceno diferencia de tiempo
        time_spent = time_spent + (double)(end - begin) / CLOCKS_PER_SEC;

        printf("Tiempo padre : %lf\n", time_spent);
    }

    MPI_Finalize();

    return 0;
}