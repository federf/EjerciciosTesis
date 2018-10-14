#!/bin/bash
gcc -o divided_countchar_with_timers divided_count_char_ocurrences_using_pipes_with_timers.c
for i in {1..10}
do
    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat shortFile.txt | ./divided_countchar_with_timers a >> resultados/conTimers/timePipesShortWithTimers.txt') 2>> resultados/conTimers/timePipesShortWithTimers.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longFile.txt | ./divided_countchar_with_timers a >> resultados/conTimers/timePipesLongWithTimers.txt') 2>> resultados/conTimers/timePipesLongWithTimers.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat longloremipsum.txt | ./divided_countchar_with_timers a >> resultados/conTimers/timePipesLoremIpsumWithTimers.txt') 2>> resultados/conTimers/timePipesLoremIpsumWithTimers.txt

    (time -p perf stat -e task-clock,instructions,cache-references,cache-misses sh -c 'cat 124mbloremipsum.txt | ./divided_countchar_with_timers a >> resultados/conTimers/timePipes124mbLoremIpsumWithTimers.txt') 2>> resultados/conTimers/timePipes124mbLoremIpsumWithTimers.txt
done