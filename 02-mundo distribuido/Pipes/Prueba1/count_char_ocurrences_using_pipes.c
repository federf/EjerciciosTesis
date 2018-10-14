//gcc -o countchar count_char_ocurrences_using_pipes.c && ./countchar e

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <ctype.h>
#include <fcntl.h>
#include <sys/wait.h> 
#include <ctype.h>

#define SIZE 128

char* ReadFile(char *filename, int size)
{
    char *buffer = NULL;
    int read_size;
    FILE *handler = fopen(filename, "r");

    if (handler)
    {
        // Allocate a string that can hold it all
        buffer = (char*) malloc(sizeof(char) * (size + 1) );

        // Read it all in one operation
        read_size = fread(buffer, sizeof(char), size, handler);

        // fread doesn't set it so put a \0 in the last position
        // and buffer is now officially a string
        buffer[size] = '\0';

        if (size != read_size)
        {
            // Something went wrong, throw away the memory and set
            // the buffer to NULL
            free(buffer);
            buffer = NULL;
        }

        // Always remember to close the file.
        fclose(handler);
    }
    return buffer;
}

int getFileSize(char *filename)
{
  char *buffer = NULL;
  int string_size, read_size;
  FILE *handler = fopen(filename, "r");

  if (handler)
  {
      // Seek the last byte of the file
      fseek(handler, 0, SEEK_END);
      // Offset from the first to the last byte, or in other words, filesize
      string_size = ftell(handler);
      return string_size;
  }
  return 0;
}

#include <sys/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
 
 
int main( int argc, char **argv )
{
    pid_t pid;
    int a[2], b[2], readbytes;

    if (argc != 2){
      printf("Incorrect number of arguments. A character is needed\n");
      exit( 0 );
    }

    char ch       = tolower(argv[1][0]);
    int countChar = 0;
    int filesize  = getFileSize("longFile.txt");
    char *string  = ReadFile("longFile.txt", filesize);
    char buffer[filesize];
     
    pipe( a );
    pipe( b );
     
    if ( (pid=fork()) == 0 )
    { // hijo
        close( a[1] ); /* cerramos el lado de escritura del pipe */
        close( b[0] ); /* cerramos el lado de lectura del pipe */
     
        while( (readbytes=read( a[0], buffer, filesize ) ) > 0){
            for(int i = 0; i< readbytes; i++){
                if (tolower(buffer[i]) == ch){
                    countChar++;
                }
            }

        }
        // cerramos el lado de lectura del pipe
        close( a[0] );
     
        char res[SIZE]; 

        snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", countChar);

        strcpy( buffer, res );
        write( b[1], buffer, strlen( buffer ) );
        close( b[1] );
    }
    else
    { // padre
        close( a[0] ); /* cerramos el lado de lectura del pipe */
        close( b[1] ); /* cerramos el lado de escritura del pipe */
 
        strcpy( buffer, string );
        //write( a[1], buffer, strlen( buffer ) );
        write( a[1], buffer, strlen( buffer ) );
        close( a[1]);
 
        while( (readbytes=read( b[0], buffer, SIZE )) > 0)
            write( 1, buffer, readbytes );
        close( b[0]);
    }
    waitpid( pid, NULL, 0 );
    exit( 0 );
}
