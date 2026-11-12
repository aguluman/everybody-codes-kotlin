package ec.y2025

import ec.Quest

/** Quests for 2025. `make new` appends here; the compiler checks every entry exists. */
val quests: Map<Int, () -> Quest> = mapOf(
    1 to ::Quest01,
    2 to ::Quest02,
    3 to ::Quest03,
    4 to ::Quest04,
    5 to ::Quest05,
    6 to ::Quest06,
    7 to ::Quest07,
    8 to ::Quest08,
    9 to ::Quest09,
    10 to ::Quest10,
    11 to ::Quest11,
    12 to ::Quest12,
    // make new: quests
)
