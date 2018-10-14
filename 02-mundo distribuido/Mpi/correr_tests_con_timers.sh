#!/bin/bash
rm resultados/conTimers/*.txt;
mpicc char_ocurrences_mpi_with_timers.c -o char_occurrences_with_timers;
for i in {1..10}
do
    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat shortFile.txt | mpirun -np 2 char_occurrences_with_timers a >> resultados/conTimers/timeMpiShortFileWithTimers.txt') 2>> resultados/conTimers/timeMpiShortFileWithTimers.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | mpirun -np 2 char_occurrences_with_timers a >> resultados/conTimers/timeMpiLongFileWithTimers.txt') 2>> resultados/conTimers/timeMpiLongFileWithTimers.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a >> resultados/conTimers/timeMpiLongIpsumWithTimers.txt') 2>> resultados/conTimers/timeMpiLongIpsumWithTimers.txt;

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | mpirun -np 2 char_occurrences_with_timers a >> resultados/conTimers/timeMpi124mbLoremIpsumWithTimers.txt') 2>> resultados/conTimers/timeMpi124mbLoremIpsumWithTimers.txt;
done