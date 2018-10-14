//gcc -o divided_countchar divided_count_char_ocurrences_using_pipes.c && ./divided_countchar e
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>
#include <ctype.h>
#include <fcntl.h>
#include <sys/wait.h> 
#include <ctype.h>
#include <assert.h>

#define SIZE 128


//obtiene el tamaño de un archivo
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

// lee el contenido del archivo de un tamaño dado
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
//divide un archivo de texto en subcadenas dado un cierto caracter delimitador
char** str_split(char* a_str, const char a_delim)
{
    char** result    = 0;
    size_t count     = 0;
    char* tmp        = a_str;
    char* last_comma = 0;
    char delim[2];
    delim[0] = a_delim;
    delim[1] = 0;

    /* Count how many elements will be extracted. */
    while (*tmp)
    {
        if (a_delim == *tmp)
        {
            count++;

            last_comma = tmp;
        }
        tmp++;
    }

    /* Add space for trailing token. */
    count += last_comma < (a_str + strlen(a_str) - 1);

    /* Add space for terminating null string so caller
       knows where the list of returned strings ends. */
    count++;

    result = malloc(sizeof(char*) * count);

    if (result)
    {
        size_t idx  = 0;
        char* token = strtok(a_str, delim);

        while (token)
        {
            assert(idx < count);
            *(result + idx++) = strdup(token);
            token = strtok(0, delim);
        }
        assert(idx == count - 1);
        *(result + idx) = 0;
    }

    return result;
}
 
 
int main( int argc, char **argv )
{
    pid_t pid, wpid;
    int status = 0;
    int a[2], b[2], readbytes;

    if (argc != 2){
      printf("Incorrect number of arguments. A character is needed\n");
      exit( 0 );
    }

    char ch        = tolower(argv[1][0]);
    int countChar  = 0;
    int filesize   = getFileSize("longFile.txt");
    int colonCount = 0;
    char buffer[filesize];
    
    pipe( a );
    pipe( b );
     
    if ( (pid=fork()) == 0 )
    { // hijo
        close( a[1] ); /* cerramos el lado de escritura del pipe */
        close( b[0] ); /* cerramos el lado de lectura del pipe */
     
        while( (readbytes=read( a[0], buffer, filesize ) ) > 0){
            colonCount++;
            //printf("hijo indice: %d\n", colonCount);
            for(int i = 0; i< readbytes; i++){
                if (tolower(buffer[i]) == ch){
                    countChar++;
                }
            }

        }
        // cerramos el lado de lectura del pipe
        close( a[0] );
     
        char res[SIZE];


        if(ch == ','){
            snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", (colonCount-1));
        }else{
            snprintf(res, sizeof res, "%s%c%s%d\n", "Cantidad de repeticiones del caracter '", ch, "' : ", countChar);
        }

        strcpy( buffer, res );
        write( b[1], buffer, strlen( buffer ) );
        close( b[1] );
        exit(0);
    }
    else
    { // padre
        char *string  = ReadFile("longFile.txt", filesize);

        char * delimiter = ".";

        //char** substrings = str_split(string, '.');
        char * token = strtok(string, delimiter);
        close( a[0] ); /* cerramos el lado de lectura del pipe */
        close( b[1] ); /* cerramos el lado de escritura del pipe */
 
        /*for(int i=0; *(substrings + i); i++){
            printf("padre indice: %d\n", i);
            char * parte = *(substrings + i);
            write( a[1], parte, strlen( parte ) );
            free(parte);
        }*/
        while(token != NULL)
        {
            //printf("'%s'\n", ptr);
            write(a[1], token, strlen(token));
            token = strtok(NULL, delimiter);
        }
        
        close( a[1]);
        
        while( (readbytes=read( b[0], buffer, SIZE )) > 0)
            write( 1, buffer, readbytes );
        close( b[0]);

    }
    //waitpid( pid, NULL, 0 );
    //while( (wpid = wait(&status)) > 0 ); 
    //exit( 0 );
}
