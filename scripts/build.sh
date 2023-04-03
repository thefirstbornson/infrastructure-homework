#!/bin/bash

echo [gradle bulld] building

(exec ./gradlew build dependencyUpdates)

echo [Main App FINISHED] image has been built