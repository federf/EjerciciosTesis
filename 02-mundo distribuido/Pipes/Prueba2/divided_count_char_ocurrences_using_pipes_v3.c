//gcc -o divided_countchar_v3 divided_count_char_ocurrences_using_pipes_v3.c && cat longFile.txt | ./divided_countchar_v3 .

// correr 10 veces:
// (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | ./divided_countchar_v3 a') 2>> timePipes.txt

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <ctype.h>
#include <sys/wait.h>

#define SIZE 128
 
int main( int argc, char **argv )
{
    pid_t pid;
    int status = 0;
    int a[2], b[2], readbytes;

    if (argc != 2) {
      printf("Incorrect number of arguments. A char is needed\n");
      exit( 0 );
    }

    char ch        = tolower(argv[1][0]);

    int colonCount = 0;
    char buffer[SIZE];
    
    pipe( a );
    pipe( b );
     
    if ( (pid = fork()) == 0 ) { // hijo
        close( a[1] ); /* cerramos el lado de escritura del pipe */
        close( b[0] ); /* cerramos el lado de lectura del pipe */

        int countChar    = 0;
        int countReceive = 0;

        while ((readbytes = read( a[0], buffer, SIZE )) > 0) {

            countReceive++;
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
            
        }

        printf("hijo recibe %i mensajes\n", countReceive);

        // cerramos el lado de lectura del pipe
        close(a[0]);
     
        char res[SIZE];

        snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", countChar);

        strcpy( buffer, res );
        write( b[1], buffer, strlen( buffer ) );
        close( b[1] );
        exit(0);

    } else { // padre

        close( a[0] ); /* cerramos el lado de lectura del pipe */
        close( b[1] ); /* cerramos el lado de escritura del pipe */

        int countSend = 0;

        while (fgets(buffer, SIZE, stdin) > 0) {
            countSend++;
            write(a[1], buffer, SIZE);
        }

        printf("padre envía %i mensajes\n", countSend);
        
        close(a[1]);

        wait(NULL);
        
        while( (readbytes = read( b[0], buffer, SIZE )) > 0) {
            write( 1, buffer, readbytes );
        }

        close(b[0]);
    }
}
