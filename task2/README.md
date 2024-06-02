## Используемый проект

https://github.com/Nefari0uss/producer-consumer

## Найденные ошибки

### Helgrind

Пример запуска
```bash
valgrind tool=helgrind ./pc 5 5 5
```

Всего было найдено 473 ошибок, в том числе data race
```bash
==97896== ERROR SUMMARY: 478 errors from 37 contexts (suppressed: 1851 from 525)
```

### ThreadSanitizer

Пример компиляции
```bash
gcc producer-consumer.c -o pc -lpthread -lrt -O3 -Wall -fsanitize=thread
```

Запуск
```bash
./pc 5 5 5
```

Также был обнаружен data race
```bash
==================
WARNING: ThreadSanitizer: data race (pid=98267)
  Write of size 1 at 0x56262278b060 by main thread:
    #0 pthread_mutex_destroy ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:1244 (libtsan.so.0+0x39398)
    #1 main <null> (pc+0x165c)

  Previous atomic read of size 1 at 0x56262278b060 by thread T9:
    #0 pthread_mutex_lock ../../../../src/libsanitizer/sanitizer_common/sanitizer_common_interceptors.inc:4240 (libtsan.so.0+0x53908)
    #1 consumer <null> (pc+0x1ada)

  As if synchronized via sleep:
    #0 sleep ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:352 (libtsan.so.0+0x66757)
    #1 main <null> (pc+0x15e0)

  Location is global 'mutex' of size 40 at 0x56262278b060 (pc+0x000000004060)

  Thread T9 (tid=98277, finished) created by main thread at:
    #0 pthread_create ../../../../src/libsanitizer/tsan/tsan_interceptors_posix.cpp:969 (libtsan.so.0+0x605b8)
    #1 main <null> (pc+0x15b7)
```

Все гонки данных связаны с параллельной обработкой буффера, так как операции над буфером
должны происходить в критических секциях.
