#!/usr/bin/env bash
java -jar ../../target/sercheduler-0.0.1-SNAPSHOT.jar \
jmetal-double \
-W workflows/ \
-H hosts/ \
-T scenario1 \
--seed 1 \
--executions 100000 \
-C experimentConfig.json