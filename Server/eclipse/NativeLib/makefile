include silk_sources.mk
include celt_sources.mk
include opus_sources.mk
include fleetspeak.mk
INCLUDES = -I include -I silk -I celt -I ../src/native -I /usr/lib/jvm/java-8-oracle/include -I /usr/lib/jvm/java-8-oracle/include/linux
CFLAGS = -c -Wall -DUSE_ALLOCA $(INCLUDES)

SOURCES =$(CELT_SOURCES) $(SILK_SOURCES) $(OPUS_SOURCES) $(FLEETSPEAK_SOURCES)
OBJECTS=$(SOURCES:.c=.o)

libNativeAudio.so:      $(OBJECTS)
	ld -shared -soname $@.1 -o $@.1.0 $(OBJECTS) -lc

               
.c.o:
	gcc $(CFLAGS) $< -o $@

