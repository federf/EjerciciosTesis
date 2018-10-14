//gcc -o divided_countchar_v2 divided_count_char_ocurrences_using_pipes_v2.c && ./divided_countchar_v2 e longFile.txt
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

    if (argc != 3) {
      printf("Incorrect number of arguments. A char and a file are needed\n");
      exit( 0 );
    }

    char ch        = tolower(argv[1][0]);
    char* filename = argv[2];

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
        FILE *fp;

        if ((fp = fopen(filename, "r")) == NULL ) {
            printf("Could not open file %s\n", filename);
	        exit(1);
        }

        close( a[0] ); /* cerramos el lado de lectura del pipe */
        close( b[1] ); /* cerramos el lado de escritura del pipe */

        int countSend = 0;

        while( !feof(fp) ) {
            countSend++;
            fgets(buffer, SIZE, fp);
            write(a[1], buffer, SIZE);
        }

        printf("padre envía %i mensajes\n", countSend);
        
        close(a[1]);

        wait(NULL);
        
        while( (readbytes = read( b[0], buffer, SIZE )) > 0) {
            write( 1, buffer, readbytes );
        }

        close(b[0]);

        fclose(fp);

    }
}
