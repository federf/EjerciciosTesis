//g++ read-file.cpp -o read-using-pipes && ./read-using-pipes l
#include <iostream>
#include <fstream>
#include <string.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <string>

using namespace std;

#define SIZE 2541

int main(int argc, char **argv){
	pid_t pid;
	int p[2], readbytes;
	char buffer[SIZE];
	char ch = tolower(argv[1][0]);
	
	printf("apariciones del caracter '%c'\n", ch);

	pipe(p);
	int countHijo=0;
	int countChar=0;
	 
	if ( (pid=fork()) == 0 )
	{ // hijo
	  	close( p[1] ); /* cerramos el lado de escritura del pipe */
	 
	  	while( (readbytes=read( p[0], buffer, SIZE )) > 0){
			for( unsigned int a = 0; a < SIZE; a = a + 1 ){
				if(buffer[a] == '\n' || buffer[a] == '\0'){
					break;
				}
				if(tolower(buffer[a]) == ch){
					countChar++;
				}
			}
			countHijo++;
		}
	    
	    close( p[0] );
	    //printf("%d apariciones del caracter %c\n", countChar,  argv[1]);
	    printf("apariciones del caracter %d en %d lineas leidas\n", countChar, countHijo);
	}
	else
	{ // padre
		close(p[0]); /* cerramos el lado de lectura del pipe */
		
		std::ifstream file("longFile.txt");
		std::string str; 
		while (std::getline(file, str)) {
			strcpy(buffer,str.c_str());
			write( p[1], buffer, SIZE );
		}
	 
	    close(p[1]);
	}
	waitpid( pid, NULL, 0 );
	exit(0);
}
