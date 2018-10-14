// mpicc hello_world_mpi.c -o hello && mpirun -np 4 hello
#include <stdio.h>
// fichero mpi
#include </usr/lib/openmpi/include/mpi.h>

int main( int argc, char **argv )
{
    // int ierr;

    // ierr = MPI_Init(&argc, &argv);
    // printf("Hello world\n"); 
        
    // ierr = MPI_Finalize();

    int ierr, num_procs, my_id;

    ierr = MPI_Init(&argc, &argv);

    /* find out MY process ID, and how many processes were started. */

    ierr = MPI_Comm_rank(MPI_COMM_WORLD, &my_id);
    ierr = MPI_Comm_size(MPI_COMM_WORLD, &num_procs);

    printf("Hello world! I'm process %i out of %i processes\n", my_id, num_procs);

    ierr = MPI_Finalize();
}